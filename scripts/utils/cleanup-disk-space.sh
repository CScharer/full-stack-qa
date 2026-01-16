#!/bin/bash
# scripts/cleanup-disk-space.sh
# Cleanup script to free up disk space by removing build artifacts, caches, and temporary files

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Get the project root
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

# Function to calculate directory size
calculate_size() {
    if [ -d "$1" ]; then
        du -sh "$1" 2>/dev/null | cut -f1
    else
        echo "0"
    fi
}

# Function to remove directory and report size
remove_directory() {
    local dir="$1"
    local description="$2"
    
    if [ -d "$dir" ]; then
        local size=$(calculate_size "$dir")
        echo -e "${YELLOW}  Removing: ${CYAN}$dir${NC} (${size})"
        rm -rf "$dir"
        echo -e "${GREEN}    ✅ Removed $description (freed ${size})${NC}"
        return 0
    else
        echo -e "${BLUE}  Skipping: ${CYAN}$dir${NC} (does not exist)"
        return 1
    fi
}

# Function to remove files matching pattern
remove_files() {
    local pattern="$1"
    local description="$2"
    
    local count=$(find . -type f -name "$pattern" 2>/dev/null | wc -l | tr -d ' ')
    if [ "$count" -gt 0 ]; then
        local total_size=$(find . -type f -name "$pattern" -exec du -ch {} + 2>/dev/null | tail -1 | cut -f1)
        echo -e "${YELLOW}  Removing: ${CYAN}$pattern${NC} (${count} file(s), ~${total_size})"
        find . -type f -name "$pattern" -delete 2>/dev/null
        echo -e "${GREEN}    ✅ Removed $description (freed ~${total_size})${NC}"
        return 0
    else
        echo -e "${BLUE}  Skipping: ${CYAN}$pattern${NC} (no files found)"
        return 1
    fi
}

# Parse command line arguments
CLEANUP_LEVEL="standard"
DRY_RUN=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --level|-l)
            CLEANUP_LEVEL="$2"
            shift 2
            ;;
        --dry-run|-n)
            DRY_RUN=true
            shift
            ;;
        --help|-h)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Cleanup script to free up disk space"
            echo ""
            echo "Options:"
            echo "  --level, -l LEVEL    Cleanup level: minimal, standard (default), aggressive"
            echo "                       Default: standard (if not specified)"
            echo "  --dry-run, -n        Show what would be removed without actually removing"
            echo "  --help, -h           Show this help message"
            echo ""
            echo "Cleanup Levels:"
            echo "  minimal      - Remove only obvious temporary files (logs, caches)"
            echo "  standard     - Remove build artifacts, test results, caches (default, recommended)"
            echo "  aggressive   - Remove everything including node_modules and venv"
            echo ""
            echo "Examples:"
            echo "  $0                    # Run with default (standard) cleanup"
            echo "  $0 --level standard   # Explicitly use standard cleanup"
            echo "  $0 --dry-run         # Preview what would be removed"
            exit 0
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}🧹 Disk Space Cleanup Script${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""
echo -e "${CYAN}Project Root:${NC} $PROJECT_ROOT"
echo -e "${CYAN}Cleanup Level:${NC} $CLEANUP_LEVEL"
if [ "$DRY_RUN" = true ]; then
    echo -e "${YELLOW}Mode:${NC} DRY RUN (no files will be deleted)"
fi
echo ""

# Track total space freed
TOTAL_FREED=0

# Start cleanup
echo -e "${BLUE}Starting cleanup...${NC}"
echo ""

# ============================================================================
# MINIMAL CLEANUP (always run)
# ============================================================================
echo -e "${CYAN}📋 Minimal Cleanup (Logs, Temporary Files)${NC}"

# Log files
remove_files "*.log" "log files" || true
remove_files "*.log.*" "rotated log files" || true

# Temporary files
remove_files "*.tmp" "temporary files" || true
remove_files "*.temp" "temporary files" || true
remove_files ".DS_Store" "macOS system files" || true
find . -type f -name ".DS_Store" -delete 2>/dev/null || true

# Python cache
remove_directory "__pycache__" "Python cache directories" || true
find . -type d -name "__pycache__" -exec rm -rf {} + 2>/dev/null || true
remove_files "*.pyc" "Python bytecode files" || true
remove_files "*.pyo" "Python optimized bytecode" || true

# Java class files in test directories (keep source)
find . -type f -name "*.class" -path "*/test/*" -delete 2>/dev/null || true

echo ""

# ============================================================================
# STANDARD CLEANUP (default)
# ============================================================================
if [ "$CLEANUP_LEVEL" = "standard" ] || [ "$CLEANUP_LEVEL" = "aggressive" ]; then
    echo -e "${CYAN}📦 Standard Cleanup (Build Artifacts, Test Results)${NC}"
    
    # Maven build artifacts
    remove_directory "target" "Maven build directory" || true
    
    # Test results and reports
    remove_directory "allure-results" "Allure test results" || true
    remove_directory "allure-results-combined" "Combined Allure results" || true
    remove_directory "allure-report" "Allure reports" || true
    remove_directory "allure-report-combined" "Combined Allure reports" || true
    remove_directory "playwright-report" "Playwright reports" || true
    remove_directory "test-results" "Test results" || true
    remove_directory "coverage" "Test coverage reports" || true
    remove_directory ".coverage" "Python coverage data" || true
    remove_directory "htmlcov" "HTML coverage reports" || true
    
    # Cypress results
    remove_directory "cypress/cypress/results" "Cypress test results" || true
    remove_directory "cypress/results" "Cypress results" || true
    remove_directory "cypress/screenshots" "Cypress screenshots" || true
    remove_directory "cypress/videos" "Cypress videos" || true
    
    # Playwright results
    remove_directory "playwright/artillery-results" "Artillery test results" || true
    remove_directory "playwright/test-results" "Playwright test results" || true
    remove_directory "playwright/playwright-report" "Playwright reports" || true
    remove_directory "playwright/playwright/.cache" "Playwright cache" || true
    
    # Robot Framework results
    remove_directory "robot-reports" "Robot Framework reports" || true
    remove_directory "src/test/robot/reports" "Robot test reports" || true
    remove_directory "src/test/robot/logs" "Robot test logs" || true
    
    # Gatling results
    remove_directory "target/gatling" "Gatling simulation results" || true
    
    # JMeter results (only in results directories, not test data)
    remove_files "*.jtl" "JMeter test results" || true
    # Only remove CSV files that are clearly test results, not test data
    find . -type f -name "*.csv" \
        -not -path "*/src/test/resources/*" \
        -not -path "*/test-data/*" \
        -not -path "*/data/*" \
        -not -path "*/.git/*" \
        -delete 2>/dev/null && echo -e "${GREEN}    ✅ Removed JMeter CSV results (preserved test data files)${NC}" || echo -e "${BLUE}  Skipping: CSV files (none found or all are test data)${NC}"
    
    # Locust results
    remove_files "locust_*.log" "Locust log files" || true
    
    # Build caches
    remove_directory ".gradle" "Gradle cache" || true
    remove_directory ".m2" "Maven local repository" || true
    
    # IDE files (keep .idea for IntelliJ, but clean caches)
    remove_directory ".idea/caches" "IntelliJ IDEA caches" || true
    remove_directory ".idea/system" "IntelliJ IDEA system files" || true
    remove_directory ".vscode/.ropeproject" "VS Code Python cache" || true
    
    # OS-specific
    remove_directory ".Trash" "macOS Trash" || true
    
    # Docker (if not using)
    remove_directory ".docker" "Docker cache" || true
    
    echo ""
fi

# ============================================================================
# AGGRESSIVE CLEANUP (optional)
# ============================================================================
if [ "$CLEANUP_LEVEL" = "aggressive" ]; then
    echo -e "${CYAN}🔥 Aggressive Cleanup (Dependencies, Virtual Environments)${NC}"
    echo -e "${YELLOW}⚠️  WARNING: This will remove node_modules and Python virtual environments!${NC}"
    echo -e "${YELLOW}    You will need to run 'npm install' and recreate venv after this.${NC}"
    echo ""
    
    # Node modules
    remove_directory "node_modules" "Node.js dependencies" || true
    remove_directory "frontend/node_modules" "Frontend dependencies" || true
    remove_directory "cypress/node_modules" "Cypress dependencies" || true
    remove_directory "playwright/node_modules" "Playwright dependencies" || true
    remove_directory "vibium/node_modules" "Vibium dependencies" || true
    
    # Python virtual environments
    remove_directory "venv" "Python virtual environment" || true
    remove_directory "backend/venv" "Backend virtual environment" || true
    remove_directory ".venv" "Python virtual environment" || true
    remove_directory "env" "Python virtual environment" || true
    
    # Package lock files (optional - be careful!)
    # remove_files "package-lock.json" "npm lock files" || true
    
    # Build outputs
    remove_directory "dist" "Distribution builds" || true
    remove_directory "build" "Build outputs" || true
    remove_directory ".next" "Next.js build cache" || true
    remove_directory "frontend/.next" "Next.js build cache" || true
    remove_directory "frontend/.turbo" "Turborepo cache" || true
    
    # TypeScript build outputs
    remove_directory "*.tsbuildinfo" "TypeScript build info" || true
    find . -type f -name "*.tsbuildinfo" -delete 2>/dev/null || true
    
    echo ""
fi

# ============================================================================
# SUMMARY
# ============================================================================
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}✅ Cleanup Complete!${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""

# Show current disk usage
echo -e "${CYAN}Current Disk Usage:${NC}"
df -h . | tail -1 | awk '{print "  Total: " $2 " | Used: " $3 " (" $5 ") | Available: " $4}'
echo ""

# Show largest directories
echo -e "${CYAN}Largest Directories (top 10):${NC}"
du -h --max-depth=1 2>/dev/null | sort -hr | head -11 | tail -10 | while read size dir; do
    echo "  $size  $dir"
done
echo ""

if [ "$DRY_RUN" = true ]; then
    echo -e "${YELLOW}ℹ️  This was a DRY RUN - no files were actually deleted${NC}"
    echo -e "${YELLOW}   Run without --dry-run to perform the cleanup${NC}"
else
    echo -e "${GREEN}💡 Tip: Run 'npm install' and recreate venv if you used aggressive cleanup${NC}"
fi

echo ""

