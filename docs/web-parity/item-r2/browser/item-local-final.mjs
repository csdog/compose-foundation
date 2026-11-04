const fs=await import('node:fs/promises'),p=(await taskSpace(1)).page('p2'),base='/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/item-r2/browser/',rows=[];
await p.cdp('Emulation.setDeviceMetricsOverride',{width:1440,height:1000,deviceScaleFactor:1,mobile:false});await p.goto('http://127.0.0.1:8765/item');await p.waitForFunction(()=>document.body.style.height.includes('calc'),undefined,{timeout:30000});
for(const v of[{n:'desktop-light',w:1440,t:'light'},{n:'mobile-light',w:390,t:'light'},{n:'desktop-dark',w:1440,t:'dark'},{n:'mobile-dark',w:390,t:'dark'}]){
 await p.cdp('Emulation.setDeviceMetricsOverride',{width:1440,height:1000,deviceScaleFactor:1,mobile:false});await p.waitForTimeout(150);if(await p.evaluate(()=>localStorage.getItem('compose-foundation.theme'))!==v.t){await p.mouse.click(1304,32,{label:'switch Item theme'});await p.waitForFunction(t=>localStorage.getItem('compose-foundation.theme')===t,v.t)}await p.cdp('Emulation.setDeviceMetricsOverride',{width:v.w,height:1000,deviceScaleFactor:1,mobile:false});
 for(const id of['demo','variant','size','icon','avatar','image','group','header','link','dropdown','rtl']){
  await p.evaluate(id=>location.hash=id==='demo'?'':id,id);await p.waitForTimeout(750);
  await p.waitForFunction(()=>{const r=document.getElementById('compose-root'),c=r.firstElementChild.firstElementChild.shadowRoot.querySelector('canvas');return Math.abs(c.width-r.clientWidth*devicePixelRatio)<1.1&&Math.abs(c.getBoundingClientRect().y)<.1});
  await p.screenshot({path:base+'local-'+v.n+'-'+id+'.png'});rows.push({...v,id,...await p.evaluate(()=>({url:location.href,scrollY,theme:localStorage.getItem('compose-foundation.theme')}))});
 }console.log(v.n+' final Item captured');
}
await fs.writeFile(base+'local-matrix.json',JSON.stringify({app:await p.evaluate(()=>performance.getEntriesByType('resource').filter(e=>e.name.endsWith('.wasm')).map(e=>e.name)),captures:rows},null,2));
