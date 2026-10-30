const p=(await taskSpace(10)).page('p2'),fs=await import('node:fs/promises'),crypto=await import('node:crypto'),d='/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/attachment-r6';await p.cdp('Page.bringToFront');await p.cdp('Emulation.setDeviceMetricsOverride',{width:1440,height:1000,deviceScaleFactor:2,mobile:false});const rows=[];
for(const [fragment,y] of [['features',245],['accessibility',437],['keyboard-scrolling',533]]){
await p.mouse.click(1260,y,{label:'navigate actual guidance directory'});await p.waitForURL('http://127.0.0.1:8765/attachment?audit=r6#'+fragment);const file=`local-toc-${fragment}.png`;let previous='',since=Date.now(),start=Date.now(),settled=false;
while(Date.now()-start<5000){await p.screenshot({path:d+'/'+file});const hash=crypto.createHash('sha256').update(await fs.readFile(d+'/'+file)).digest('hex');if(hash!==previous){previous=hash;since=Date.now();}else if(Date.now()-since>=250){settled=true;break;}}
if(!settled)throw Error('TOC did not settle');rows.push({fragment,file,url:await p.url(),input:{x:1260,y},settled});}
await fs.writeFile(d+'/browser-toc-check.json',JSON.stringify(rows,null,2));console.log(rows);
