import AppKit
let root="/Users/cscs/IdeaProjects/angryecho/compose-foundation"
let image=NSImage(contentsOfFile:"\(root)/docs/web-parity/attachment-r7/native-svg-reference.png")!
var rect=NSRect(origin:.zero,size:image.size)
let cg=image.cgImage(forProposedRect:&rect,context:nil,hints:nil)!
let dir="\(root)/app/shared/src/jvmTest/resources/attachment"
try FileManager.default.createDirectory(atPath:dir,withIntermediateDirectories:true)
for (x,size,theme) in [(24,16,"light"),(72,16,"dark"),(120,24,"light"),(184,24,"dark")] {
let crop=cg.cropping(to:CGRect(x:x,y:24,width:size,height:size))!,b=NSBitmapImageRep(cgImage:crop)
try b.representation(using:.png,properties:[:])!.write(to:URL(fileURLWithPath:"\(dir)/file-search-source-\(theme)-\(size).png"))
}
