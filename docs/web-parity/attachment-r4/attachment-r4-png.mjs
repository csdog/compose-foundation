import fs from 'node:fs/promises';import zlib from 'node:zlib';
export async function png(path){
 const b=await fs.readFile(path);let at=8,w,h,bpp;const parts=[];
 while(at<b.length){const n=b.readUInt32BE(at),type=b.toString('ascii',at+4,at+8),data=b.subarray(at+8,at+8+n);if(type==='IHDR'){w=data.readUInt32BE(0);h=data.readUInt32BE(4);if(data[8]!==8||![2,6].includes(data[9]))throw Error('unsupported PNG');bpp=data[9]===2?3:4;}if(type==='IDAT')parts.push(data);at+=12+n;}
 const raw=zlib.inflateSync(Buffer.concat(parts)),pixels=Buffer.alloc(w*h*bpp),stride=w*bpp;let p=0;
 function paeth(a,b,c){const q=a+b-c,aa=Math.abs(q-a),bb=Math.abs(q-b),cc=Math.abs(q-c);return aa<=bb&&aa<=cc?a:bb<=cc?b:c;}
 for(let y=0;y<h;y++){const f=raw[p++];for(let x=0;x<stride;x++){const i=y*stride+x,a=x>=bpp?pixels[i-bpp]:0,up=y?pixels[i-stride]:0,c=y&&x>=bpp?pixels[i-stride-bpp]:0;pixels[i]=(raw[p++]+(f===0?0:f===1?a:f===2?up:f===3?Math.floor((a+up)/2):paeth(a,up,c)))&255;}}
 return {w,h,bpp,pixels};
}
export async function pane(path,form,mode){
 const {w,h,bpp,pixels}=await png(path);
 const left=form==='desktop'?366:25,width=form==='desktop'?638:(form==='aligned'?325:340),ratio=w/(form==='desktop'?1440:(form==='aligned'?375:390)),x=Math.round((left+20)*ratio),target=mode==='light'?248:10;let best={top:0,height:0},start=0,length=0;
 for(let y=Math.round(100*ratio);y<h;y++){const i=(y*w+x)*bpp,yes=[0,1,2].every(k=>Math.abs(pixels[i+k]-target)<=1);if(yes){if(!length)start=y;length++;if(length>best.height)best={top:start,height:length};}else length=0;}
 if(best.height<200)throw Error('No Group preview pane '+JSON.stringify(best));return{left,width,top:best.top/ratio,height:best.height/ratio,pixelRatio:ratio};
}

export async function rowSignature(path,region){
 const {w,h,bpp,pixels}=await png(path),ratio=w/(region.form==='desktop'?1440:(region.form==='aligned'?375:390));
 const y=Math.round((region.rect.top+142)*ratio),left=Math.round(region.left*ratio),width=Math.round(region.viewport*ratio);let signature='';
 for(let x=left;x<left+width;x++){const i=(y*w+x)*bpp;signature+=region.mode==='light'?(pixels[i]>=252?'1':'0'):(pixels[i]>=18?'1':'0');}
 return signature;
}
