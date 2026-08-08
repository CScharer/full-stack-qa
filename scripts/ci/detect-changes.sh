#!/bin/bash
# scripts/ci/detect-changes.sh
# Detect whether code changed and whether the change is Maven-deps/CI-infra only
# (narrow CI: compile/quality/smoke — skip full FE matrix, BE, FS).

set -e

EVENT_NAME=$1
BASE_SHA=$2
HEAD_SHA=$3

# Documentation-only extensions (same as historical behavior)
DOC_PATTERN='\.(md|log|txt|rst|adoc)$'

# Paths that do not warrant the full FE/BE/FS matrix when they are the only
# non-doc changes (Maven dependency bumps + CI/docs infrastructure).
is_maven_narrow_path() {
  local path="$1"
  case "$path" in
    pom.xml|*/pom.xml) return 0 ;;
    .mvn/*|mvnw|mvnw.cmd) return 0 ;;
    .github/*) return 0 ;;
    scripts/ci/*) return 0 ;;
    docs/*) return 0 ;;
    *) return 1 ;;
  esac
}

classify_changed_files() {
  local changed_files="$1"
  local code_changed="false"
  local maven_deps_only="false"

  if [ -z "$changed_files" ]; then
    echo "code-changed=false" >> "$GITHUB_OUTPUT"
    echo "maven-deps-only=false" >> "$GITHUB_OUTPUT"
    echo "✅ No changed files detected - will skip build/test"
    return
  fi

  local non_doc=""
  local non_narrow=""
  local has_maven_manifest="false"

  while IFS= read -r file; do
    [ -z "$file" ] && continue
    if echo "$file" | grep -qE "$DOC_PATTERN"; then
      continue
    fi
    non_doc="${non_doc}${file}"$'\n'
    case "$file" in
      pom.xml|*/pom.xml|.mvn/*|mvnw|mvnw.cmd) has_maven_manifest="true" ;;
    esac
    if ! is_maven_narrow_path "$file"; then
      non_narrow="${non_narrow}${file}"$'\n'
    fi
  done <<< "$changed_files"

  if [ -z "$(echo "$non_doc" | sed '/^$/d')" ]; then
    echo "✅ Documentation-only change - will skip build/test"
    code_changed="false"
    maven_deps_only="false"
  else
    code_changed="true"
    # Narrow scope when every non-doc path is Maven/CI/docs infra AND at least
    # one Maven manifest changed (pure workflow-only edits keep full defaults
    # unless they are docs-only, which is handled above).
    if [ -z "$(echo "$non_narrow" | sed '/^$/d')" ] && [ "$has_maven_manifest" = "true" ]; then
      maven_deps_only="true"
      echo "📦 Maven-deps / CI-infra change - will run narrow CI (compile, quality, smoke)"
    else
      maven_deps_only="false"
      echo "📝 Code files changed - will run full pipeline"
    fi
  fi

  echo "code-changed=${code_changed}" >> "$GITHUB_OUTPUT"
  echo "maven-deps-only=${maven_deps_only}" >> "$GITHUB_OUTPUT"
}

if [ "$EVENT_NAME" = "workflow_dispatch" ]; then
  echo "🔧 Manual workflow trigger - will run full pipeline"
  echo "code-changed=true" >> "$GITHUB_OUTPUT"
  echo "maven-deps-only=false" >> "$GITHUB_OUTPUT"
elif [ "$EVENT_NAME" = "pull_request" ]; then
  CHANGED_FILES=$(git diff --name-only "$BASE_SHA" "$HEAD_SHA")
  echo "Changed files:"
  echo "$CHANGED_FILES"
  classify_changed_files "$CHANGED_FILES"
else
  # For push events
  CHANGED_FILES=$(git diff --name-only HEAD^1 HEAD 2>/dev/null || git ls-files)
  echo "Changed files in this push:"
  echo "$CHANGED_FILES"
  classify_changed_files "$CHANGED_FILES"
fi
