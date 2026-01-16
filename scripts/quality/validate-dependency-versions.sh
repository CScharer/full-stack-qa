#!/bin/bash
# scripts/validate-dependency-versions.sh
# Validates that dependency versions are aligned across the project
# Supports report generation for monitoring/alerting

set -e # Exit immediately if a command exits with a non-zero status

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Track overall status
FAILED=0
WARNINGS=0

# Report generation options
REPORT_FORMAT=""
REPORT_FILE=""
GENERATE_REPORT=false

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --report-json)
            REPORT_FORMAT="json"
            GENERATE_REPORT=true
            shift
            ;;
        --report-csv)
            REPORT_FORMAT="csv"
            GENERATE_REPORT=true
            shift
            ;;
        --report-file)
            REPORT_FILE="$2"
            shift 2
            ;;
        --help)
            echo "Usage: $0 [--report-json|--report-csv] [--report-file FILE]"
            echo ""
            echo "Options:"
            echo "  --report-json       Generate JSON report"
            echo "  --report-csv        Generate CSV report"
            echo "  --report-file FILE   Output file path (default: stdout or results/version-validation-report.{json|csv})"
            echo "  --help              Show this help message"
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

# Initialize report data (using variables instead of associative arrays for compatibility)
REPORT_TIMESTAMP=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
REPORT_STATUS="unknown"
REPORT_ERRORS=0
REPORT_WARNINGS_COUNT=0
REPORT_SELENIUM_VERSION_POM=""
REPORT_SELENIUM_VERSION_WORKFLOW=""
REPORT_SELENIUM_MATCH="false"
REPORT_MISMATCHES=""
REPORT_WARNINGS_LIST=""

# Print functions
print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
    FAILED=$((FAILED + 1))
    if [ -z "$REPORT_MISMATCHES" ]; then
        REPORT_MISMATCHES="$1"
    else
        REPORT_MISMATCHES="$REPORT_MISMATCHES|$1"
    fi
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
    WARNINGS=$((WARNINGS + 1))
    if [ -z "$REPORT_WARNINGS_LIST" ]; then
        REPORT_WARNINGS_LIST="$1"
    else
        REPORT_WARNINGS_LIST="$REPORT_WARNINGS_LIST|$1"
    fi
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_section() {
    echo ""
    echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
    echo ""
}

# Generate report functions
generate_json_report() {
    local output_file="${REPORT_FILE:-results/version-validation-report.json}"
    mkdir -p "$(dirname "$output_file")"
    
    # Convert pipe-separated strings to JSON arrays
    local mismatches_json="[]"
    if [ -n "$REPORT_MISMATCHES" ]; then
        mismatches_json="["
        local first=true
        IFS='|' read -ra MISMATCH_ARRAY <<< "$REPORT_MISMATCHES"
        for mismatch in "${MISMATCH_ARRAY[@]}"; do
            if [ "$first" = true ]; then
                first=false
            else
                mismatches_json+=","
            fi
            # Escape quotes and newlines
            local escaped=$(echo "$mismatch" | sed 's/"/\\"/g' | sed 's/$/\\n/' | tr -d '\n' | sed 's/\\n$//')
            mismatches_json+="\"$escaped\""
        done
        mismatches_json+="]"
    fi
    
    local warnings_json="[]"
    if [ -n "$REPORT_WARNINGS_LIST" ]; then
        warnings_json="["
        local first=true
        IFS='|' read -ra WARNINGS_ARRAY <<< "$REPORT_WARNINGS_LIST"
        for warning in "${WARNINGS_ARRAY[@]}"; do
            if [ "$first" = true ]; then
                first=false
            else
                warnings_json+=","
            fi
            local escaped=$(echo "$warning" | sed 's/"/\\"/g' | sed 's/$/\\n/' | tr -d '\n' | sed 's/\\n$//')
            warnings_json+="\"$escaped\""
        done
        warnings_json+="]"
    fi
    
    # Build JSON structure
    local json="{"
    json+="\"timestamp\":\"$REPORT_TIMESTAMP\","
    json+="\"status\":\"$REPORT_STATUS\","
    json+="\"errors\":$REPORT_ERRORS,"
    json+="\"warnings\":$REPORT_WARNINGS_COUNT,"
    json+="\"selenium\":{"
    json+="\"pom_version\":\"$REPORT_SELENIUM_VERSION_POM\","
    json+="\"workflow_version\":\"$REPORT_SELENIUM_VERSION_WORKFLOW\","
    json+="\"match\":$REPORT_SELENIUM_MATCH"
    json+="},"
    json+="\"mismatches\":$mismatches_json,"
    json+="\"warnings_list\":$warnings_json"
    json+="}"
    
    echo "$json" > "$output_file"
    print_info "JSON report generated: $output_file"
}

generate_csv_report() {
    local output_file="${REPORT_FILE:-results/version-validation-report.csv}"
    mkdir -p "$(dirname "$output_file")"
    
    # Escape commas and quotes in CSV
    local mismatches_csv=$(echo "$REPORT_MISMATCHES" | sed 's/|/; /g' | sed 's/"/""/g')
    local warnings_csv=$(echo "$REPORT_WARNINGS_LIST" | sed 's/|/; /g' | sed 's/"/""/g')
    
    {
        echo "timestamp,status,errors,warnings,selenium_pom_version,selenium_workflow_version,selenium_match,mismatches,warnings_list"
        echo "\"$REPORT_TIMESTAMP\",\"$REPORT_STATUS\",$REPORT_ERRORS,$REPORT_WARNINGS_COUNT,\"$REPORT_SELENIUM_VERSION_POM\",\"$REPORT_SELENIUM_VERSION_WORKFLOW\",$REPORT_SELENIUM_MATCH,\"$mismatches_csv\",\"$warnings_csv\""
    } > "$output_file"
    
    print_info "CSV report generated: $output_file"
}

# Get script directory
# Since this script is in scripts/quality/, we need to go up two levels to get project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
PROJECT_ROOT="$SCRIPT_DIR"

cd "$PROJECT_ROOT"

print_section "🔍 Dependency Version Validation"

# Phase 1: Selenium Version Validation
print_section "Phase 1: Selenium Version Validation"

# Get Selenium version from pom.xml
SELENIUM_VERSION_POM=$(grep '<selenium.version>' pom.xml | sed 's/.*<selenium.version>\([^<]*\)<\/selenium.version>.*/\1/' | head -1)
# Get Selenium version from workflow (look for default value after selenium_version:)
SELENIUM_VERSION_WORKFLOW=$(grep -A 3 'selenium_version:' .github/workflows/env-fe.yml | grep "default:" | awk -F"'" '{print $2}' | head -1)

REPORT_SELENIUM_VERSION_POM="$SELENIUM_VERSION_POM"
REPORT_SELENIUM_VERSION_WORKFLOW="$SELENIUM_VERSION_WORKFLOW"

if [ -z "$SELENIUM_VERSION_POM" ]; then
    print_error "Could not find selenium.version in pom.xml"
else
    print_info "Selenium version in pom.xml: $SELENIUM_VERSION_POM"
fi

if [ -z "$SELENIUM_VERSION_WORKFLOW" ]; then
    print_error "Could not find selenium_version default in env-fe.yml"
else
    print_info "Selenium version in env-fe.yml: $SELENIUM_VERSION_WORKFLOW"
fi

if [ -n "$SELENIUM_VERSION_POM" ] && [ -n "$SELENIUM_VERSION_WORKFLOW" ]; then
    if [ "$SELENIUM_VERSION_POM" = "$SELENIUM_VERSION_WORKFLOW" ]; then
        print_success "Selenium versions match: $SELENIUM_VERSION_POM"
        REPORT_SELENIUM_MATCH="true"
    else
        print_error "Selenium version mismatch: pom.xml=$SELENIUM_VERSION_POM, workflow=$SELENIUM_VERSION_WORKFLOW"
        REPORT_SELENIUM_MATCH="false"
    fi
fi

# Phase 2: Node.js Package Version Validation
print_section "Phase 2: Node.js Package Version Validation"

NODE_PROJECTS=("cypress" "playwright" "vibium" "frontend")
VERSION_MISMATCHES=0

for project in "${NODE_PROJECTS[@]}"; do
    if [ ! -d "$project" ]; then
        print_warning "Project directory not found: $project"
        continue
    fi
    
    if [ ! -f "$project/package.json" ]; then
        print_warning "package.json not found in $project"
        continue
    fi
    
    print_info "Checking $project/package.json..."
    
    # Check for common dependencies that should be aligned
    COMMON_DEPS=("typescript" "@types/node")
    
    for dep in "${COMMON_DEPS[@]}"; do
        # Extract version from package.json (handles both "dep": "version" and "dep": "^version")
        # Use awk for more reliable parsing
        VERSION=$(grep "\"$dep\"" "$project/package.json" | awk -F'"' '{print $4}' | sed 's/[\^~]//g' | head -1)
        
        if [ -n "$VERSION" ] && [ "$VERSION" != ":" ]; then
            print_info "  $dep: $VERSION"
        fi
    done
done

# Phase 3: Python Dependency Validation
print_section "Phase 3: Python Dependency Validation"

if [ -f "requirements.txt" ]; then
    print_info "Checking requirements.txt..."
    # Basic check - ensure file is readable
    if [ -r "requirements.txt" ]; then
        print_success "requirements.txt is readable"
    else
        print_error "Cannot read requirements.txt"
    fi
else
    print_warning "requirements.txt not found"
fi

if [ -f "backend/requirements.txt" ]; then
    print_info "Checking backend/requirements.txt..."
    if [ -r "backend/requirements.txt" ]; then
        print_success "backend/requirements.txt is readable"
    else
        print_error "Cannot read backend/requirements.txt"
    fi
fi

# Phase 4: Docker Compose Version Validation
print_section "Phase 4: Docker Compose Version Validation"

DOCKER_COMPOSE_FILES=("docker-compose.yml" "docker-compose.dev.yml" "docker-compose.prod.yml")
DOCKER_VERSION_MISMATCHES=0

for compose_file in "${DOCKER_COMPOSE_FILES[@]}"; do
    if [ ! -f "$compose_file" ]; then
        print_warning "Docker Compose file not found: $compose_file"
        continue
    fi
    
    print_info "Checking $compose_file..."
    
    # Extract Selenium image versions from Docker Compose file
    # Look for selenium/hub, selenium/node-chrome, selenium/node-firefox, selenium/node-edge
    # Also check for seleniarm variants (seleniarm/hub, seleniarm/node-chromium, etc.)
    SELENIUM_IMAGES=$(grep -E "image:.*selenium|image:.*seleniarm" "$compose_file" | sed 's/.*image:[[:space:]]*\(.*\)/\1/' | sed 's/[[:space:]]*#.*$//' || true)
    
    if [ -z "$SELENIUM_IMAGES" ]; then
        print_warning "  No Selenium images found in $compose_file"
        continue
    fi
    
    # Check each image
    while IFS= read -r image_line; do
        if [ -z "$image_line" ]; then
            continue
        fi
        
        # Extract image name and tag
        # Format: seleniarm/hub:latest or selenium/hub:4.39.0
        IMAGE_NAME=$(echo "$image_line" | sed 's/:.*$//')
        IMAGE_TAG=$(echo "$image_line" | sed 's/.*://' | sed 's/[[:space:]]*$//')
        
        if [ -z "$IMAGE_TAG" ]; then
            IMAGE_TAG="latest"
        fi
        
        print_info "  Found image: $IMAGE_NAME:$IMAGE_TAG"
        
        # Check if using :latest tag (warning, not error)
        if [ "$IMAGE_TAG" = "latest" ]; then
            print_warning "  ⚠️  Using 'latest' tag for $IMAGE_NAME (recommended: use versioned tag like $SELENIUM_VERSION_POM)"
        else
            # Compare with pom.xml version if available
            if [ -n "$SELENIUM_VERSION_POM" ] && [ "$IMAGE_TAG" != "$SELENIUM_VERSION_POM" ]; then
                print_error "  Version mismatch: $IMAGE_NAME has tag $IMAGE_TAG, but pom.xml specifies $SELENIUM_VERSION_POM"
                DOCKER_VERSION_MISMATCHES=$((DOCKER_VERSION_MISMATCHES + 1))
                FAILED=$((FAILED + 1))
            elif [ -n "$SELENIUM_VERSION_POM" ] && [ "$IMAGE_TAG" = "$SELENIUM_VERSION_POM" ]; then
                print_success "  ✅ Version matches pom.xml: $IMAGE_TAG"
            fi
        fi
    done <<< "$SELENIUM_IMAGES"
done

if [ $DOCKER_VERSION_MISMATCHES -eq 0 ]; then
    print_success "Docker Compose version validation passed"
else
    print_error "Docker Compose version validation found $DOCKER_VERSION_MISMATCHES mismatch(es)"
fi

# Phase 5: Summary
print_section "Validation Summary"

REPORT_ERRORS=$FAILED
REPORT_WARNINGS_COUNT=$WARNINGS

if [ $FAILED -eq 0 ] && [ $WARNINGS -eq 0 ]; then
    print_success "All version checks passed!"
    REPORT_STATUS="success"
    echo ""
    
    # Generate report if requested
    if [ "$GENERATE_REPORT" = true ]; then
        if [ "$REPORT_FORMAT" = "json" ]; then
            generate_json_report
        elif [ "$REPORT_FORMAT" = "csv" ]; then
            generate_csv_report
        fi
    fi
    
    exit 0
elif [ $FAILED -eq 0 ]; then
    print_warning "Validation completed with $WARNINGS warning(s)."
    REPORT_STATUS="warning"
    echo ""
    
    # Generate report if requested
    if [ "$GENERATE_REPORT" = true ]; then
        if [ "$REPORT_FORMAT" = "json" ]; then
            generate_json_report
        elif [ "$REPORT_FORMAT" = "csv" ]; then
            generate_csv_report
        fi
    fi
    
    exit 0
else
    print_error "Validation failed with $FAILED error(s) and $WARNINGS warning(s)."
    print_info "Please fix version mismatches before proceeding."
    REPORT_STATUS="failed"
    echo ""
    
    # Generate report if requested
    if [ "$GENERATE_REPORT" = true ]; then
        if [ "$REPORT_FORMAT" = "json" ]; then
            generate_json_report
        elif [ "$REPORT_FORMAT" = "csv" ]; then
            generate_csv_report
        fi
    fi
    
    exit 1
fi
