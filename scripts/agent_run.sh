#!/usr/bin/env bash
# Run one command with full output offloaded to disk and a compact tail in context.
set -u

if [ "$#" -lt 4 ]; then
  printf 'Usage: %s <run-dir> <label> -- <command> [args...]\n' "$0" >&2
  exit 64
fi

run_dir=$1
label=$2
shift 2
if [ "${1:-}" != "--" ]; then
  printf 'Expected -- before the command\n' >&2
  exit 64
fi
shift

if [ "$#" -eq 0 ]; then
  printf 'Missing command\n' >&2
  exit 64
fi

safe_label=$(printf '%s' "$label" | tr -c 'A-Za-z0-9._-' '-')
log_dir="$run_dir/logs"
mkdir -p "$log_dir"
stamp=$(date '+%Y%m%dT%H%M%S')
full_log="$log_dir/${stamp}-${safe_label}-$$.log"
start_epoch=$(date '+%s')

"$@" >"$full_log" 2>&1
status=$?
end_epoch=$(date '+%s')
duration=$((end_epoch - start_epoch))
line_count=$(wc -l <"$full_log" | tr -d ' ')
byte_count=$(wc -c <"$full_log" | tr -d ' ')
tail_lines=${AGENT_OUTPUT_LINES:-80}

printf '%s\t%s\t%s\t%s\t%s\t%s\n' \
  "$(date '+%Y-%m-%dT%H:%M:%S%z')" "$label" "$status" "$duration" \
  "$line_count" "$full_log" >>"$run_dir/commands.log"

printf 'command:'
printf ' %q' "$@"
printf '\n'
printf 'exit: %s\n' "$status"
printf 'duration_s: %s\n' "$duration"
printf 'full_log: %s\n' "$full_log"
printf 'bytes: %s; lines: %s\n' "$byte_count" "$line_count"
printf 'output_tail (%s lines):\n' "$tail_lines"
tail -n "$tail_lines" "$full_log" | sed $'s/\033\\[[0-9;]*[[:alpha:]]//g'

exit "$status"
