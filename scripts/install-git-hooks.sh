#!/bin/sh
# Copy all hook scripts from .git-hooks/ into the repo .git/hooks/ (does not use core.hooksPath).
# Usage: ./gradlew installGitHooks or sh scripts/install-git-hooks.sh
set -e
HERE="$(cd "$(dirname "$0")/.." && pwd)"
cd "$HERE" || exit 1
HOOKS_SRC="$HERE/.git-hooks"
if [ ! -d "$HOOKS_SRC" ]; then
  echo "Directory not found: $HOOKS_SRC" >&2
  exit 1
fi
GIT_DIR="$(git rev-parse --absolute-git-dir)"
mkdir -p "$GIT_DIR/hooks"
installed=0
for f in "$HOOKS_SRC"/*; do
  [ -f "$f" ] || continue
  base=$(basename "$f")
  case "$base" in
  .*) continue ;;
  README*|*.md|*.txt) continue ;;
  esac
  cp "$f" "$GIT_DIR/hooks/$base"
  chmod +x "$GIT_DIR/hooks/$base"
  echo "Installed $GIT_DIR/hooks/$base"
  installed=$((installed + 1))
done
if [ "$installed" -eq 0 ]; then
  echo "No installable hook files found under $HOOKS_SRC" >&2
  exit 1
fi
