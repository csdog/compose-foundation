from fontTools.ttLib import TTFont
from fontTools.varLib.instancer import instantiateVariableFont
from fontTools.pens.recordingPen import DecomposingRecordingPen
from pathlib import Path
import json
root=Path('/Users/cscs/IdeaProjects/angryecho/compose-foundation')
p=root/'docs/web-parity/attachment-r7/font-comparison.json';data=json.loads(p.read_text());source=TTFont('/private/tmp/attachment-r7-geist-latin.woff2')
for row in data['rows']:
 local=TTFont(root/row['localFile']);ref=instantiateVariableFont(source,{'wght':row['weight']},inplace=False)
 def outlines(font):
  glyphs=font.getGlyphSet();cmap=font.getBestCmap();out={}
  for c in set(data['text']):
   pen=DecomposingRecordingPen(glyphs);glyphs[cmap[ord(c)]].draw(pen);out[c]=pen.value
  return out
 a,b=outlines(local),outlines(ref);deltas=[];incompatible=[]
 for c in a:
  if [x[0] for x in a[c]]!=[x[0] for x in b[c]]:incompatible.append(c);continue
  def numbers(value):
   if isinstance(value,(int,float)):yield float(value)
   elif isinstance(value,(list,tuple)):
    for item in value:yield from numbers(item)
  x=list(numbers([op[1] for op in a[c]]));y=list(numbers([op[1] for op in b[c]]))
  if len(x)!=len(y):incompatible.append(c);continue
  deltas.extend(abs(i-j) for i,j in zip(x,y))
 row['decomposedOutlineComparison']={'incompatibleCommands':incompatible,'maxCoordinateDeltaFontUnits':max(deltas,default=0),'maxCoordinateDeltaAt14px':max(deltas,default=0)*14/1000}
 row['hintTables']={'local':[t for t in ['fpgm','prep','cvt ','gasp'] if t in local],'source':[t for t in ['fpgm','prep','cvt ','gasp'] if t in ref]}
data['advanceMetricNote']='advanceAt14px is an unshaped hmtx sum, not the real text width. Actual website Canvas width148.008163px / DOM Range148.015625px are in official-shaped-title.json. Actual Compose measurement147.99402px passes0.1px tolerance.'
p.write_text(json.dumps(data,indent=2));print([(r['weight'],r['decomposedOutlineComparison'],r['hintTables']) for r in data['rows']])
