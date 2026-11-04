#!/usr/bin/env python3
"""Keep dated parity records newest first without changing their creation order.

Older records only include dates. Their original append order breaks ties; it is
persisted as metadata so repeated sorting or later edits cannot reorder them.
Overview, open differences and the component checklist remain above the records.
"""
from pathlib import Path
import re

PROGRESS = Path(__file__).resolve().parents[1] / 'docs/shadcn-web-progress.md'
HEADING = re.compile(r'^#{2,3} (.+?(\d{4}-\d{2}-\d{2}).*)$', re.M)
CREATED = re.compile(r'<!-- progress-created: (\d{4}-\d{2}-\d{2}); order: (\d+) -->')
RECORDS = '## 进度记录（按创建时间倒序）'


def main():
    text = PROGRESS.read_text()
    if RECORDS in text:
        overview, timeline = text.split(RECORDS, 1)
        # The ordering explanation is outside the dated records.
        first = HEADING.search(timeline)
        if not first:
            raise ValueError('No dated progress records')
        timeline = timeline[first.start():]
    else:
        overview, timeline = text.split('## 旧 Badge 基准排查', 1)
        timeline = '## 旧 Badge 基准排查（2026-10-28）' + timeline
        # This dated historical entry used to sit between overview sections.
        start = overview.index('## 第二批：导航、正文结构与 Badge（2026-10-28）')
        end = overview.index('## 尚未关闭的页面差异', start)
        timeline = overview[start:end] + timeline
        overview = overview[:start] + overview[end:]
    starts = list(HEADING.finditer(timeline))
    existing = [int(m.group(2)) for m in CREATED.finditer(timeline)]
    next_order = max(existing, default=-1) + 1
    records = []
    for i, match in enumerate(starts):
        end = starts[i+1].start() if i+1 < len(starts) else len(timeline)
        block = timeline[match.end():end].strip()
        metadata = CREATED.search(block)
        date = metadata.group(1) if metadata else match.group(2)
        order = int(metadata.group(2)) if metadata else next_order
        if not metadata:
            next_order += 1
        block = CREATED.sub('', block).strip()
        # Undated subheadings stay with the entry, below its level-three title.
        block = re.sub(r'^#{2,3} ', '#### ', block, flags=re.M)
        records.append((date, order, match.group(1), block))
    if len({order for _, order, _, _ in records}) != len(records):
        raise ValueError('Duplicate creation-order metadata')
    records.sort(key=lambda row: (row[0], row[1]), reverse=True)
    output = overview.rstrip() + '\n\n' + RECORDS + '\n\n'
    output += '按记录创建日期倒序；同日按原追加顺序倒序。旧记录未注明时分秒，不补造精确时间；后续修改不改变创建顺序。\n\n'
    output += '\n\n'.join(f'### {title}\n\n<!-- progress-created: {date}; order: {order} -->\n\n{body}' for date, order, title, body in records)
    PROGRESS.write_text(output.rstrip() + '\n')
    print(f'Sorted {len(records)} progress records by creation date/order descending.')


if __name__ == '__main__':
    main()
