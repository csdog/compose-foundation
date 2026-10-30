import AppKit
let d="/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/attachment-r7"
let src=try JSONSerialization.jsonObject(with:Data(contentsOf:URL(fileURLWithPath:"\(d)/official-browser.json"))) as! [[String:Any]]
let local=try JSONSerialization.jsonObject(with:Data(contentsOf:URL(fileURLWithPath:"\(d)/local-browser.json"))) as! [[String:Any]]
let states=[("desktop","light"),("desktop","dark"),("phone","light"),("phone","dark")]
for phase in ["rest","hover","action-focus","preview-focus"] {
 let w=880,h=520,b=NSBitmapImageRep(bitmapDataPlanes:nil,pixelsWide:880,pixelsHigh:520,bitsPerSample:8,samplesPerPixel:4,hasAlpha:true,isPlanar:false,colorSpaceName:.deviceRGB,bytesPerRow:0,bitsPerPixel:0)!
 NSGraphicsContext.saveGraphicsState();NSGraphicsContext.current=NSGraphicsContext(bitmapImageRep:b);NSColor.white.setFill();NSRect(x:0,y:0,width:w,height:h).fill()
 for (i,state) in states.enumerated(){for (j,rows) in [src,local].enumerated(){
 let r=rows.first{$0["form"] as? String==state.0 && $0["mode"] as? String==state.1 && $0["phase"] as? String==phase}!,c=r["card"] as! [String:Any]
 func n(_ k:String)->Double {(c[k] as! NSNumber).doubleValue}
 let x=floor(n("left"))-10,y=floor(n("top"))-10,cw=ceil(n("width"))+20,ch=ceil(n("height"))+20,file=r["file"] as! String,path="\(d)/\(file)",im=NSImage(contentsOfFile:path)!,rep=NSBitmapImageRep(data:try Data(contentsOf:URL(fileURLWithPath:path)))!
 let label="\(phase) \(j==0 ? "official (Rhea)":"local (Nova)") \(state.0) \(state.1)"
 (label as NSString).draw(at:NSPoint(x:j*440+10,y:h-i*130-20),withAttributes:[.font:NSFont.systemFont(ofSize:12),.foregroundColor:NSColor.black])
 im.draw(in:NSRect(x:Double(j*440+10),y:Double(h-i*130-25)-ch,width:cw,height:ch),from:NSRect(x:x,y:Double(rep.pixelsHigh)-y-ch,width:cw,height:ch),operation:.copy,fraction:1)
 }}
 NSGraphicsContext.restoreGraphicsState();try b.representation(using:.png,properties:[:])!.write(to:URL(fileURLWithPath:"\(d)/contact-\(phase).png"))
}
