import AppKit
let d="/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/attachment-r6"
let src=try JSONSerialization.jsonObject(with:Data(contentsOf:URL(fileURLWithPath:"\(d)/official-browser.json"))) as! [[String:Any]]
let loc=try JSONSerialization.jsonObject(with:Data(contentsOf:URL(fileURLWithPath:"\(d)/local-browser.json"))) as! [[String:Any]]
for section in ["features","accessibility","label-icon-only-actions","label-the-trigger","keyboard-scrolling","meaning-beyond-color"] {for form in ["desktop","phone"] {
 let cw=form=="desktop" ? 648:390,ch=form=="desktop" ? (section=="label-the-trigger" ? 868:520):844,w=cw*2,h=(ch+24)*2
 let b=NSBitmapImageRep(bitmapDataPlanes:nil,pixelsWide:w,pixelsHigh:h,bitsPerSample:8,samplesPerPixel:4,hasAlpha:true,isPlanar:false,colorSpaceName:.deviceRGB,bytesPerRow:0,bitsPerPixel:0)!
 NSGraphicsContext.saveGraphicsState();NSGraphicsContext.current=NSGraphicsContext(bitmapImageRep:b);NSColor.white.setFill();NSRect(x:0,y:0,width:w,height:h).fill()
 for (i,mode) in ["light","dark"].enumerated(){for(j,rows) in [src,loc].enumerated(){
 let r=rows.first{$0["form"] as? String==form && $0["mode"] as? String==mode && $0["section"] as? String==section}!,file=r["file"] as! String,path="\(d)/\(file)",im=NSImage(contentsOfFile:path)!,rep=NSBitmapImageRep(data:try Data(contentsOf:URL(fileURLWithPath:path)))!
 let x=form=="phone" ? 0:(j==0 ? Int(((r["heading"] as! [String:Any])["left"] as! NSNumber).doubleValue)-4:361),y=form=="phone" ? 0:64
 let label="\(j==0 ? "official":"local") \(form) \(mode) — \(section)"
 (label as NSString).draw(at:NSPoint(x:j*cw+4,y:h-i*(ch+24)-20),withAttributes:[.font:NSFont.systemFont(ofSize:12),.foregroundColor:NSColor.black])
 im.draw(in:NSRect(x:j*cw,y:h-(i+1)*(ch+24),width:cw,height:ch),from:NSRect(x:x,y:rep.pixelsHigh-y-ch,width:cw,height:ch),operation:.copy,fraction:1)
 }}
 NSGraphicsContext.restoreGraphicsState();try b.representation(using:.png,properties:[:])!.write(to:URL(fileURLWithPath:"\(d)/contact-\(form)-\(section).png"))
}}
