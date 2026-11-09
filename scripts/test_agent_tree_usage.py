import importlib.util
import json
import tempfile
import unittest
from pathlib import Path

spec = importlib.util.spec_from_file_location('agent_tree_usage', Path(__file__).with_name('agent_tree_usage.py'))
usage = importlib.util.module_from_spec(spec)
spec.loader.exec_module(usage)


class TreeTests(unittest.TestCase):
    def test_tree_and_snapshots_and_coordination(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            for key, parent, amount in [('root', None, 100), ('child', 'root', 200), ('leaf', 'child', 300), ('other', None, 900)]:
                source = {'subagent': {'thread_spawn': {'parent_thread_id': parent}}} if parent else 'cli'
                counter = {'input_tokens': amount, 'cached_input_tokens': amount-10, 'output_tokens': 5, 'total_tokens': amount+5}
                rows = [
                    {'type': 'session_meta', 'payload': {'id': key, 'source': source}},
                    {'type': 'response_item', 'payload': {'type': 'custom_tool_call', 'name': 'wait_agent' if key == 'root' else 'exec'}},
                    {'type': 'token_usage_record', 'payload': {'usage': counter, 'thread_token_usage': counter}},
                    *[{'type': 'event_msg', 'payload': {'type': 'token_count', 'info': {'total_token_usage': counter}}}]*2,
                ]
                (root / (key+'.jsonl')).write_text(''.join(json.dumps(r)+'\n' for r in rows)+'{"partial":')
            result = usage.report('root', [root])
            self.assertEqual(3, result['sessions'])
            self.assertEqual(600, result['usage']['input_tokens'])
            self.assertEqual(30, result['usage']['uncached_input_tokens'])
            self.assertEqual(100, result['input_after_coordination'])
            self.assertNotIn('details', result)
            self.assertEqual(3, len(usage.report('root', [root], True)['details']))

    def test_missing_usage_and_root(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            (root/'session.jsonl').write_text(json.dumps({'type':'session_meta', 'payload':{'id':'root'}}))
            self.assertEqual(1, usage.report('root', [root])['missing_usage_sessions'])
            with self.assertRaises(ValueError):
                usage.report('absent', [root])


if __name__ == '__main__':
    unittest.main()
