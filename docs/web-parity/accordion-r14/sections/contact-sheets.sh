#!/usr/bin/env bash
set -euo pipefail

base="$(cd "$(dirname "$0")" && pwd)"
sections=(basic multiple disabled borders card rtl)

for state in desktop-light desktop-dark phone-light phone-dark; do
  inputs=()
  filters=()
  rows=()
  index=0
  for section in "${sections[@]}"; do
    inputs+=(-i "$base/official-$section-$state.png")
    inputs+=(-i "$base/local-$section-$state.png")
    filters+=("[$index:v][$((index + 1)):v]hstack=inputs=2[row$section];")
    rows+=("[row$section]")
    index=$((index + 2))
  done
  filters+=("${rows[*]}vstack=inputs=${#sections[@]}[out]")
  ffmpeg -loglevel error -y "${inputs[@]}" -filter_complex "${filters[*]}" \
    -map "[out]" "$base/contact-$state.png"
done
