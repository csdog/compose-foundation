const fs=await import('node:fs/promises'),p=(await taskSpace(1)).page('p1'),base='/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/item-r2/browser/',rows=[];
for(const v of[{n:'desktop-light',w:1440,t:'light'},{n:'mobile-light',w:390,t:'light'},{n:'desktop-dark',w:1440,t:'dark'},{n:'mobile-dark',w:390,t:'dark'}]){
 await p.cdp('Emulation.setDeviceMetricsOverride',{width:v.w,height:1000,deviceScaleFactor:1,mobile:false});
 if(await p.evaluate(()=>localStorage.getItem('theme'))!==v.t){const m=(await p.snapshot()).match(/button \[ref=(\d+)\]\n\s+svg_root(?:\n\s+text "Toggle theme")?\n\s+anchor/);if(!m)throw Error('theme');await p.click('@'+m[1]);}
 await p.evaluate(()=>document.getElementById('dropdown').scrollIntoView());await p.click('loc=role:button[name="Select"]');await p.waitForSelector('[data-slot="dropdown-menu-content"]',{state:'visible'});await p.screenshot({path:base+'official-'+v.n+'-menu-open.png'});
 await p.keyboard.press('ArrowDown');rows.push({...v,step:'keyboard-menu',dom:await p.evaluate(()=>({active:document.activeElement?.textContent,items:[...document.querySelectorAll('[role=menuitem]')].map(e=>({text:e.textContent,highlighted:e.hasAttribute('data-highlighted'),rect:e.getBoundingClientRect().toJSON()}))}))});await p.screenshot({path:base+'official-'+v.n+'-menu-keyboard.png'});
 await p.keyboard.press('Enter');await p.waitForFunction(()=>![...document.querySelectorAll('[data-slot="dropdown-menu-content"]')].some(e=>e.getBoundingClientRect().height>0));await p.screenshot({path:base+'official-'+v.n+'-menu-selected.png'});
 await p.evaluate(()=>document.getElementById('rtl').scrollIntoView());await p.waitForTimeout(200);
 for(const [name,label]of[['english','English'],['hebrew','Hebrew (עברית)'],['arabic','Arabic (العربية)']]){await p.click('loc=css:[data-slot=select-trigger]');await p.click('loc=role:option[name="'+label+'"]');await p.waitForTimeout(250);await p.screenshot({path:base+'official-'+v.n+'-rtl-'+name+'.png'});rows.push({...v,step:'rtl-'+name})}
 console.log(v.n+' source menu and languages captured');
}
await fs.writeFile(base+'official-actions.json',JSON.stringify(rows,null,2));
