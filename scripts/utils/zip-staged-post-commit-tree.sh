#!/usr/bin/env bash
# Build a zip of every *staged* path that would still exist in the repo after a
# commit (index blobs only). Staged deletions are omitted because those paths
# are absent from the index.
#
# How to run (from the repo root, after staging files with git add):
#   bash ./scripts/utils/zip-staged-post-commit-tree.sh
#
# From elsewhere (script resolves the repo via git):
#   bash /path/to/full-stack-qa/scripts/utils/zip-staged-post-commit-tree.sh
#
# Writes the zip next to the clone directory: the parent of the git repo root,
# with filename SARAPlus-Zipped-<datetimestamp>.zip (e.g. .../dev/ when the repo
# is .../dev/SARAPlus-portal-automation).
set -euo pipefail

ROOT="$(git rev-parse --show-toplevel 2>/dev/null)" || {
  echo "error: not inside a git repository" >&2
  exit 1
}
cd "$ROOT"

STAGED_LIST="$(mktemp)"
trap 'rm -f "$STAGED_LIST"' EXIT
git diff --cached --name-only >"$STAGED_LIST"

if ! grep -q . "$STAGED_LIST"; then
  echo "error: nothing staged; stage changes first" >&2
  exit 1
fi

TS="$(date +%m%d_%H%M)"
PARENT="$(dirname "$ROOT")"
OUT_ZIP="${PARENT}/SARAPlus-Zipped-${TS}.zip"

TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"; rm -f "$STAGED_LIST"' EXIT

while IFS= read -r file || [[ -n "${file:-}" ]]; do
  [[ -z "$file" ]] && continue
  if git cat-file -e ":$file" 2>/dev/null; then
    mkdir -p "$TMP/$(dirname "$file")"
    git show ":$file" >"$TMP/$file"
  fi
done <"$STAGED_LIST"

mkdir -p "$(dirname "$OUT_ZIP")"
( cd "$TMP" && zip -r -q "$OUT_ZIP" . )

echo "$OUT_ZIP"
