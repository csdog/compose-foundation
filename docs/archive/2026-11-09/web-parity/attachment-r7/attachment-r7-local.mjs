const p=(await taskSpace(10)).page('p2'),fs=await import('node:fs/promises'),{pane,png}=await import('file:///private/tmp/attachment-r3-png-native.mjs');
const d='/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/attachment-r7',base='http://127.0.0.1:8765/attachment?audit=r7',rows=[];let current='light';await p.cdp('Page.bringToFront');await p.goto(base);await p.reload();for(let i=0;i<4;i++)await p.screenshot({path:'/private/tmp/attachment-r7-warm.png'});
for(const [form,mode,width,height] of [['desktop','light',1440,1000],['desktop','dark',1440,1000],['phone','dark',390,844],['phone','light',390,844]]){
await p.cdp('Emulation.setDeviceMetricsOverride',{width,height,deviceScaleFactor:2,mobile:false});if(current!==mode){await p.mouse.click(form==='desktop'?1318:268,32,{label:'switch attachment interaction theme'});current=mode;}
await p.goto(base+'#sizes');await p.screenshot({path:'/private/tmp/attachment-r7-nav.png'});await p.goto(base+'#trigger');for(let i=0;i<3;i++)await p.screenshot({path:'/private/tmp/attachment-r7-locate.png'});
const preview=await pane('/private/tmp/attachment-r7-locate.png',form,mode),left=form==='desktop'?493:65,cardWidth=form==='desktop'?384:260,y=preview.top+117;
await p.mouse.click(left+cardWidth-45,y,{label:'focus copy before clearing'});await p.keyboard.press('Tab');await p.keyboard.press('Tab');await p.mouse.move(width-20,height-20,{label:'leave attachment hover target'});
for(const phase of ['rest','hover','action-focus','preview-focus']){
if(phase==='hover')await p.mouse.move(left+60,y,{label:'hover local attachment card'});
if(phase==='action-focus'){await p.mouse.click(left+cardWidth-45,y,{label:'focus independent copy action'});await p.keyboard.press('Tab');await p.mouse.move(width-20,height-20,{label:'inspect attachment keyboard ring'});}
if(phase==='preview-focus'){await p.keyboard.press('Shift+Tab');await p.keyboard.press('Shift+Tab');}
const file=`local-${form}-${mode}-${phase}.png`;for(let i=0;i<3;i++)await p.screenshot({path:d+'/'+file});const im=await png(d+'/'+file),ix=(Math.round(y)*im.w+left+3)*im.bpp;rows.push({form,mode,width,height,phase,file,dpr:await p.evaluate(()=>devicePixelRatio),preview,card:{left,top:y-29,width:cardWidth,height:58},insideFillRgb:[...im.pixels.subarray(ix,ix+3)],focusEvidence:'Real keyboard navigation and inspected pixels; browser DOM activeElement is not a Compose focus ownership proof.'});}
await fs.writeFile(d+'/local-browser.json',JSON.stringify(rows,null,2));console.log({form,mode,pixels:rows.slice(-4).map(x=>[x.phase,x.insideFillRgb])});}
