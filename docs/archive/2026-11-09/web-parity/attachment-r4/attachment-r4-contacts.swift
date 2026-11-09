import AppKit
let d="/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/attachment-r4"
let src=try JSONSerialization.jsonObject(with:Data(contentsOf:URL(fileURLWithPath:"\(d)/official-browser.json"))) as! [[String:Any]]
let local=try JSONSerialization.jsonObject(with:Data(contentsOf:URL(fileURLWithPath:"\(d)/local-browser.json"))) as! [[String:Any]]
let states=[("desktop","light"),("desktop","dark"),("phone","light"),("phone","dark")]
for phase in ["start","wheel","end","keyboard"] {
 let width=1288,height=1120
 let b=NSBitmapImageRep(bitmapDataPlanes:nil,pixelsWide:width,pixelsHigh:height,bitsPerSample:8,samplesPerPixel:4,hasAlpha:true,isPlanar:false,colorSpaceName:.deviceRGB,bytesPerRow:0,bitsPerPixel:0)!
 NSGraphicsContext.saveGraphicsState();NSGraphicsContext.current=NSGraphicsContext(bitmapImageRep:b)
 NSColor.white.setFill();NSRect(x:0,y:0,width:width,height:height).fill()
 for (i,state) in states.enumerated(){for (j,rows) in [src,local].enumerated(){
  let r=rows.first{$0["form"] as? String==state.0 && $0["mode"] as? String==state.1 && $0["phase"] as? String==phase}!,p=r["preview"] as! [String:Any]
  func n(_ key:String)->Int {Int((p[key] as! NSNumber).doubleValue.rounded(.down))}
  let x=n("left"),y=n("top"),w=Int(ceil((p["width"] as! NSNumber).doubleValue)),h=Int(ceil((p["height"] as! NSNumber).doubleValue))
  let file=r["file"] as! String,path="\(d)/\(file)",image=NSImage(contentsOfFile:path)!,rep=NSBitmapImageRep(data:try Data(contentsOf:URL(fileURLWithPath:path)))!
  let label="\(phase) — \(j==0 ? "official":"local") \(state.0) \(state.1)"
  (label as NSString).draw(at:NSPoint(x:j*644+4,y:height-i*280-20),withAttributes:[.font:NSFont.systemFont(ofSize:12),.foregroundColor:NSColor.black])
  image.draw(in:NSRect(x:j*644+3,y:height-i*280-25-h,width:w,height:h),from:NSRect(x:x,y:rep.pixelsHigh-y-h,width:w,height:h),operation:.copy,fraction:1)
 }}
 NSGraphicsContext.restoreGraphicsState();try b.representation(using:.png,properties:[:])!.write(to:URL(fileURLWithPath:"\(d)/contact-\(phase).png"))
}
