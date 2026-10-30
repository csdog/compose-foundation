const p=(await taskSpace(10)).page('p2');await p.cdp('Page.bringToFront');const rows=[],fs=await import('node:fs/promises');let modeNow='dark';const d='/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/attachment-r4';
for(const [form,mode,width,height] of [['desktop','light',1440,1000],['desktop','dark',1440,1000],['phone','dark',390,844],['phone','light',390,844]]){
await p.cdp('Emulation.setDeviceMetricsOverride',{width,height,deviceScaleFactor:2,mobile:false});if(modeNow!==mode){await p.mouse.click(form==='desktop'?1318:268,32,{label:'inspect attachment size theme'});modeNow=mode;}
await p.goto('http://127.0.0.1:8765/attachment?audit=attachment-r4-final#sizes');for(let i=0;i<3;i++)await p.screenshot({path:`${d}/local-${form}-${mode}-sizes.png`});rows.push({form,mode,width,height,file:`local-${form}-${mode}-sizes.png`});}
await fs.writeFile(d+'/local-sizes.json',JSON.stringify(rows,null,2));console.log(rows);
