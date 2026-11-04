const fs=await import('node:fs/promises'),p=(await taskSpace(1)).page('p2'),base='/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/item-r2/browser/',rows=[];
async function click(x,y){for(const type of['mouseMoved','mousePressed','mouseReleased'])await p.cdp('Input.dispatchMouseEvent',{type,x,y,button:type==='mouseMoved'?'none':'left',buttons:type==='mousePressed'?1:0,clickCount:type==='mouseMoved'?0:1})}
await p.keyboard.press('Escape');
for(const v of[{n:'desktop-light',w:1440,t:'light'},{n:'mobile-light',w:390,t:'light'},{n:'desktop-dark',w:1440,t:'dark'},{n:'mobile-dark',w:390,t:'dark'}]){
 await p.evaluate(t=>localStorage.setItem('compose-foundation.theme',t),v.t);
 await p.cdp('Emulation.setDeviceMetricsOverride',{width:v.w,height:1000,deviceScaleFactor:1,mobile:false});await p.reload();await p.waitForFunction(()=>document.body.style.height.includes('calc'),undefined,{timeout:30000});await p.waitForTimeout(750);await p.evaluate(()=>location.hash='rtl');await p.waitForTimeout(750);
 const x=v.w===390?80:480,y=v.w===390?225:201;
 for(const [name,label,dy]of[['english','English',-28],['hebrew','Hebrew (עברית)',56],['arabic','Arabic (العربية)',-28]]){
  await click(x,y);await p.waitForTimeout(500);await click(x,y+dy);await p.waitForTimeout(500);const snapshot=await p.snapshot();

  await p.screenshot({path:base+'local-'+v.n+'-rtl-'+name+'.png'});rows.push({...v,language:name,label,visualReviewRequired:true,url:await p.url()});
 }console.log(v.n+' three language captures ready for visual review');
}
await fs.writeFile(base+'local-languages.json',JSON.stringify({app:await p.evaluate(()=>performance.getEntriesByType('resource').filter(e=>e.name.endsWith('.wasm')).map(e=>e.name)),captures:rows},null,2));
