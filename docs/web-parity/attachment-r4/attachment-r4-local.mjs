const p=(await taskSpace(10)).page('p2'),fs=await import('node:fs/promises'),crypto=await import('node:crypto'),{pane,rowSignature}=await import('file:///private/tmp/attachment-r4-png.mjs');
const d='/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/attachment-r4',base='http://127.0.0.1:8765/attachment?audit=attachment-r4-final',rows=[];let modeNow='light',region=null;
await p.cdp('Page.bringToFront');
await p.goto(base);await p.reload();for(let i=0;i<4;i++)await p.screenshot({path:'/private/tmp/attachment-r4-warm.png'});
async function capture(path){if(!region){for(let i=0;i<3;i++)await p.screenshot({path});return;}let last='',stableSince=Date.now(),started=Date.now();while(Date.now()-started<5000){await p.screenshot({path});const h=await rowSignature(path,region);if(h!==last){stableSince=Date.now();last=h;}else if(Date.now()-stableSince>=250)return;}throw Error('Card positions did not settle');}

for(const [form,mode,width,height] of [['desktop','light',1440,1000],['desktop','dark',1440,1000],['phone','dark',390,844],['phone','light',390,844],['aligned','light',375,844],['aligned','dark',375,844]]){
 region=null;await p.cdp('Emulation.setDeviceMetricsOverride',{width,height,deviceScaleFactor:2,mobile:false});if(modeNow!==mode){await p.mouse.click(form!=='desktop'?width-122:1318,32,{label:'switch attachment snap theme'});modeNow=mode;}
 await p.goto(base+'#trigger');await p.screenshot({path:'/private/tmp/attachment-r4-nav.png'});await p.goto(base+'#group');await capture('/private/tmp/attachment-r4-locate.png');
 await p.evaluate(()=>{if(window.__attachmentWheelAudit)removeEventListener('wheel',window.__attachmentWheelAudit.listener,true);const items=[];const listener=e=>items.push({dx:e.deltaX,dy:e.deltaY,mode:e.deltaMode});window.__attachmentWheelAudit={items,listener};addEventListener('wheel',listener,{capture:true,passive:true})});
 const rect=await pane('/private/tmp/attachment-r4-locate.png',form,mode),left=form==='desktop'?493:65,viewport=form==='desktop'?384:(width-130),y=rect.top+121;
 region={form:form,mode,rect,left,viewport};await p.mouse.move(left+viewport/2,y,{label:'inspect attachment snap row'});await p.cdp('Input.dispatchMouseEvent',{type:'mouseWheel',x:left+viewport/2,y,deltaX:-10000,deltaY:0});await capture(`${d}/local-${form}-${mode}-start.png`);
 await p.cdp('Input.dispatchMouseEvent',{type:'mouseWheel',x:left+viewport/2,y,deltaX:330,deltaY:0});await capture(`${d}/local-${form}-${mode}-wheel.png`);
 await p.cdp('Input.dispatchMouseEvent',{type:'mouseWheel',x:left+viewport/2,y,deltaX:10000,deltaY:0});await capture(`${d}/local-${form}-${mode}-end.png`);
 await p.mouse.click(left+viewport-21,y,{label:'focus final attachment action'});await p.keyboard.press('ArrowLeft');await capture(`${d}/local-${form}-${mode}-keyboard.png`);
 const nativeInputs=await p.evaluate(()=>({dpr:devicePixelRatio,events:window.__attachmentWheelAudit.items}));if(nativeInputs.dpr!==2||nativeInputs.events.length!==3||nativeInputs.events[1].dx!==330)throw Error('Native input calibration changed '+JSON.stringify(nativeInputs));
 for(const phase of ['start','wheel','end','keyboard'])rows.push({role:'local',form,mode,width,height,phase,nativeInputs,dpr:2,input:'single CDP wheel at native host DPR; pixel330 for wheel phase',file:`local-${form}-${mode}-${phase}.png`,preview:rect,group:{left,width:viewport,centreY:y},scroll:'Canvas pixels, no numeric DOM scroll state',review:'pending visual contact review'});
 console.log({form,mode,preview:rect});await fs.writeFile(d+'/local-browser.json',JSON.stringify(rows,null,2));
}
await fs.writeFile(d+'/local-browser.json',JSON.stringify(rows,null,2));

await p.evaluate(()=>{if(window.__attachmentWheelAudit){removeEventListener('wheel',window.__attachmentWheelAudit.listener,true);delete window.__attachmentWheelAudit}});
