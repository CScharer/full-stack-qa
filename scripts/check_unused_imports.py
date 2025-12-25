#!/usr/bin/env python3
"""
Script to identify and remove unused imports, particularly Environment imports
that were only used for logging (sysOut, sysOutFailure).
"""

import os
import re
import sys
from pathlib import Path

def check_environment_usage(file_path):
    """Check if Environment is used for non-logging purposes."""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
    except Exception as e:
        print(f"Error reading {file_path}: {e}", file=sys.stderr)
        return None
    
    # Check if file imports Environment
    has_import = bool(re.search(r'^import\s+com\.cjs\.qa\.core\.Environment;', content, re.MULTILINE))
    if not has_import:
        return None
    
    # Patterns that indicate Environment is used for non-logging purposes
    # Exclude logging methods: sysOut, sysOutFailure, isLogAll
    non_logging_patterns = [
        r'\bEnvironment\.(?!sysOut|sysOutFailure|isLogAll)',  # Any Environment method except logging methods
        r'\bEnvironment\s*=',  # Environment as variable
        r'Environment\s*\(',  # Environment constructor
        r'extends\s+Environment',  # Extending Environment
        r'implements.*Environment',  # Implementing Environment
    ]
    
    # Check for non-logging usage
    for pattern in non_logging_patterns:
        if re.search(pattern, content):
            return False  # Environment is used for non-logging
    
    # Check if only logging methods are used (or not used at all)
    logging_patterns = [
        r'Environment\.sysOut\s*\(',
        r'Environment\.sysOutFailure\s*\(',
        r'Environment\.isLogAll\s*\(',
        r'VivitEnvironment\.sysOut\s*\(',
        r'MicrosoftEnvironment\.sysOut\s*\(',
    ]
    
    has_logging_usage = any(re.search(pattern, content) for pattern in logging_patterns)
    
    # If logging usage found, import is still being used (needs migration first)
    if has_logging_usage:
        return False  # Still in use (needs migration)
    
    # If no logging usage found and no non-logging usage, import is unused
    return True  # Unused

def remove_unused_imports(file_path, unused_imports):
    """Remove unused imports from a file."""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            lines = f.readlines()
    except Exception as e:
        print(f"Error reading {file_path}: {e}", file=sys.stderr)
        return False
    
    modified = False
    new_lines = []
    i = 0
    
    while i < len(lines):
        line = lines[i]
        should_remove = False
        
        for unused_import in unused_imports:
            # Match exact import statement
            import_pattern = re.escape(unused_import)
            if re.match(r'^\s*import\s+' + import_pattern + r'\s*;\s*$', line):
                should_remove = True
                modified = True
                break
        
        if not should_remove:
            new_lines.append(line)
        
        i += 1
    
    if modified:
        try:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.writelines(new_lines)
            return True
        except Exception as e:
            print(f"Error writing {file_path}: {e}", file=sys.stderr)
            return False
    
    return False

def main():
    project_root = Path(__file__).parent.parent
    test_dir = project_root / 'src' / 'test' / 'java'
    
    if not test_dir.exists():
        print(f"Test directory not found: {test_dir}", file=sys.stderr)
        sys.exit(1)
    
    files_to_fix = []
    
    # Find all Java files
    for java_file in test_dir.rglob('*.java'):
        if '.archived' in str(java_file):
            continue
        
        result = check_environment_usage(java_file)
        if result is True:  # Unused
            files_to_fix.append(java_file)
        elif result is None:
            continue  # No Environment import
    
    print(f"Found {len(files_to_fix)} files with unused Environment imports")
    
    if files_to_fix:
        print("\nFiles with unused Environment imports:")
        for f in files_to_fix:
            print(f"  {f.relative_to(project_root)}")
        
        # Auto-remove without prompt for non-interactive use
        removed_count = 0
        for java_file in files_to_fix:
            if remove_unused_imports(java_file, ['com.cjs.qa.core.Environment']):
                removed_count += 1
                print(f"Removed unused import from: {java_file.relative_to(project_root)}")
        print(f"\nRemoved unused imports from {removed_count} files")
    else:
        print("No unused Environment imports found")

if __name__ == '__main__':
    main()
