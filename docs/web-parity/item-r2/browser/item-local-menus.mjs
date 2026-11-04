const fs=await import('node:fs/promises'),p=(await taskSpace(1)).page('p2'),base='/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/item-r2/browser/',rows=[];
async function click(x,y){for(const type of['mouseMoved','mousePressed','mouseReleased'])await p.cdp('Input.dispatchMouseEvent',{type,x,y,button:type==='mouseMoved'?'none':'left',buttons:type==='mousePressed'?1:0,clickCount:type==='mouseMoved'?0:1})}
for(const v of[{n:'desktop-light',w:1440,t:'light'},{n:'mobile-light',w:390,t:'light'},{n:'desktop-dark',w:1440,t:'dark'},{n:'mobile-dark',w:390,t:'dark'}]){
 await p.evaluate(t=>localStorage.setItem('compose-foundation.theme',t),v.t);
 await p.cdp('Emulation.setDeviceMetricsOverride',{width:v.w,height:1000,deviceScaleFactor:1,mobile:false});await p.goto("http://127.0.0.1:8765/item");await p.waitForFunction(()=>document.body.style.height.includes('calc'),undefined,{timeout:30000});await p.waitForTimeout(1000);await p.evaluate(()=>location.hash='dropdown');await p.waitForTimeout(1000);
 const pos={x:v.w===390?187:712,y:253};
 await click(pos.x,pos.y);await p.waitForTimeout(1000);await p.screenshot({path:base+'local-'+v.n+'-menu-open.png'});await p.keyboard.press('ArrowDown');await p.waitForTimeout(750);await p.screenshot({path:base+'local-'+v.n+'-menu-keyboard.png'});await p.keyboard.press('Enter');await p.waitForTimeout(1000);await p.screenshot({path:base+'local-'+v.n+'-menu-selected.png'});rows.push({...v,pos,visualReviewRequired:true}); console.log(v.n+' menu captures ready');
}
await fs.writeFile(base+'local-menus.json',JSON.stringify({app:await p.evaluate(()=>performance.getEntriesByType('resource').filter(e=>e.name.endsWith('.wasm')).map(e=>e.name)),captures:rows},null,2));
