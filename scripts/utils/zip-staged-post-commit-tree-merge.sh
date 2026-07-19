#!/usr/bin/env bash
# Unpack SARAPlus-Zipped-<timestamp>.zip from ~/Downloads into this repo (inverse of
# zip-staged-post-commit-tree.sh). Each path inside the zip is written under the
# git repo root, preserving relative directories (e.g. tests/config/foo.ts).
#
# How to run (from the repo root):
#   ./scripts/utils/zip-staged-post-commit-tree-merge.sh
# "C:\Program Files\Git\bin\bash.exe" ./scripts/utils/zip-staged-post-commit-tree-merge.sh
#
# Use a specific zip (still searched under ~/Downloads unless given as absolute path):
#   ./scripts/utils/zip-staged-post-commit-tree-merge.sh SARAPlus-Zipped-20250517_143022.zip
#
# Keep the zip after a successful merge (default is to delete it):
#   ./scripts/utils/zip-staged-post-commit-tree-merge.sh --keep-zip
#
# From elsewhere (script resolves the repo via git):
#   bash /path/to/full-stack-qa/scripts/utils/zip-staged-post-commit-tree-merge.sh
#
# Zip timestamp format matches zip-staged-post-commit-tree.sh: YYYYMMDD_HHMMSS
# (e.g. SARAPlus-Zipped-20250517_143022.zip).
set -euo pipefail

ROOT="$(git rev-parse --show-toplevel 2>/dev/null)" || {
  echo "error: not inside a git repository" >&2
  exit 1
}
cd "$ROOT"

DOWNLOADS="${HOME}/Downloads"
ZIP_GLOB='SARAPlus-Zipped-*.zip'
KEEP_ZIP=false
ZIP_ARG=""

usage() {
  sed -n '2,17p' "$0" | sed 's/^# \{0,1\}//'
  echo
  echo "Options:"
  echo "  --keep-zip    Do not delete the zip after a successful merge"
  echo "  -h, --help    Show this help"
}

while (($# > 0)); do
  case "$1" in
    --keep-zip)
      KEEP_ZIP=true
      shift
      ;;
    -h | --help)
      usage
      exit 0
      ;;
    --)
      shift
      break
      ;;
    -*)
      echo "error: unknown option: $1" >&2
      exit 1
      ;;
    *)
      if [[ -n "$ZIP_ARG" ]]; then
        echo "error: unexpected extra argument: $1" >&2
        exit 1
      fi
      ZIP_ARG="$1"
      shift
      ;;
  esac
done

resolve_zip() {
  local name="${1:-}"
  if [[ -n "$name" ]]; then
    if [[ -f "$name" ]]; then
      printf '%s\n' "$(cd "$(dirname "$name")" && pwd)/$(basename "$name")"
      return 0
    fi
    if [[ -f "${DOWNLOADS}/${name}" ]]; then
      printf '%s\n' "${DOWNLOADS}/${name}"
      return 0
    fi
    echo "error: zip not found: $name (also checked ${DOWNLOADS}/)" >&2
    return 1
  fi

  shopt -s nullglob
  local -a matches=("${DOWNLOADS}"/${ZIP_GLOB})
  shopt -u nullglob

  if ((${#matches[@]} == 0)); then
    echo "error: no ${ZIP_GLOB} found in ${DOWNLOADS}" >&2
    return 1
  fi

  local newest="${matches[0]}"
  local f
  for f in "${matches[@]}"; do
    if [[ "$f" -nt "$newest" ]]; then
      newest="$f"
    fi
  done
  printf '%s\n' "$newest"
}

IN_ZIP="$(resolve_zip "$ZIP_ARG")"

EXTRACT="$(mktemp -d)"
trap 'rm -rf "$EXTRACT"' EXIT

unzip -q -o "$IN_ZIP" -d "$EXTRACT"

copied=0
while IFS= read -r -d '' src; do
  rel="${src#"${EXTRACT}/"}"
  [[ -z "$rel" ]] && continue
  if [[ "$rel" == ..* ]] || [[ "$rel" == */../* ]] || [[ "$rel" == */.. ]] || [[ "$rel" == /* ]]; then
    echo "error: refusing unsafe zip path: $rel" >&2
    exit 1
  fi

  dest="${ROOT}/${rel}"
  mkdir -p "$(dirname "$dest")"
  cp -f "$src" "$dest"
  copied=$((copied + 1))
done < <(find "$EXTRACT" -type f -print0)

if ((copied == 0)); then
  echo "error: zip contained no files: $IN_ZIP" >&2
  exit 1
fi

echo "Merged ${copied} file(s) from ${IN_ZIP} into ${ROOT}"

if ! $KEEP_ZIP; then
  rm -f "$IN_ZIP"
  echo "Removed ${IN_ZIP}"
fi
