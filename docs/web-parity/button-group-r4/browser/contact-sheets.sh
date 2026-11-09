#!/usr/bin/env bash
set -euo pipefail

base="$(cd "$(dirname "$0")" && pwd)"
recipes=(default orientation size nested separator split input input-group dropdown-menu select popover rtl)
supporting=(composition accessibility buttongroup-vs-togglegroup api-reference)

function sheet() {
  local prefix="$1"
  shift
  local sections=("$@")
  for state in desktop-light desktop-dark phone-light phone-dark; do
    inputs=()
    filters=()
    rows=()
    index=0
    for section in "${sections[@]}"; do
      inputs+=(-i "$base/sections/official-$section-$state.png")
      inputs+=(-i "$base/sections/local-$section-$state.png")
      filters+=("[$index:v][$((index + 1)):v]hstack=inputs=2[row$index];")
      rows+=("[row$index]")
      index=$((index + 2))
    done
    filters+=("${rows[*]}vstack=inputs=${#sections[@]}[out]")
    ffmpeg -loglevel error -y "${inputs[@]}" -filter_complex "${filters[*]}" \
      -map "[out]" "$base/$prefix-$state.png"
  done
}

sheet contact "${recipes[@]}"
sheet support "${supporting[@]}"
