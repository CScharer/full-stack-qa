#!/bin/bash
# scripts/ci/detect-changes.sh
# Logic to detect if code files or only documentation changed

set -e

EVENT_NAME=$1
BASE_SHA=$2
HEAD_SHA=$3

if [ "$EVENT_NAME" = "workflow_dispatch" ]; then
    echo "🔧 Manual workflow trigger - will run full pipeline"
    echo "code-changed=true" >> "$GITHUB_OUTPUT"
elif [ "$EVENT_NAME" = "pull_request" ]; then
    CHANGED_FILES=$(git diff --name-only "$BASE_SHA" "$HEAD_SHA")
    echo "Changed files:"
    echo "$CHANGED_FILES"

    CODE_CHANGED=$(echo "$CHANGED_FILES" | grep -v -E '\.(md|log|txt|rst|adoc)$' || true)

    if [ -z "$CODE_CHANGED" ]; then
        echo "✅ Documentation-only change - will skip build/test"
        echo "code-changed=false" >> "$GITHUB_OUTPUT"
    else
        echo "📝 Code files changed - will run full pipeline"
        echo "code-changed=true" >> "$GITHUB_OUTPUT"
    fi
else
    # For push events
    CHANGED_FILES=$(git diff --name-only HEAD^1 HEAD 2>/dev/null || git ls-files)
    echo "Changed files in this push:"
    echo "$CHANGED_FILES"

    CODE_CHANGED=$(echo "$CHANGED_FILES" | grep -v -E '\.(md|log|txt|rst|adoc)$' || true)

    if [ -z "$CODE_CHANGED" ]; then
        echo "✅ Documentation-only change - will skip build/test"
        echo "code-changed=false" >> "$GITHUB_OUTPUT"
    else
        echo "📝 Code files changed - will run full pipeline"
        echo "code-changed=true" >> "$GITHUB_OUTPUT"
    fi
fi
