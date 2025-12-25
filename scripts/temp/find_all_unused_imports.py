#!/usr/bin/env python3
"""
Script to find and remove all unused imports in Java files.
This is a more comprehensive version that checks for any unused imports.
"""

import re
import sys
from pathlib import Path
from collections import defaultdict

def extract_imports(content):
    """Extract all import statements from Java file."""
    imports = []
    import_pattern = re.compile(r'^import\s+([^;]+);', re.MULTILINE)
    for match in import_pattern.finditer(content):
        import_path = match.group(1).strip()
        # Skip static imports for now
        if not import_path.startswith('static '):
            imports.append(import_path)
    return imports

def get_class_name_from_import(import_path):
    """Extract class name from import path."""
    return import_path.split('.')[-1]

def check_import_usage(content, import_path):
    """Check if an import is actually used in the file."""
    class_name = get_class_name_from_import(import_path)
    
    # Skip java.lang imports (automatically imported)
    if import_path.startswith('java.lang.'):
        return None
    
    # Skip if it's a wildcard import
    if import_path.endswith('.*'):
        return None
    
    # Check for usage patterns
    # 1. Direct class name usage (new ClassName, ClassName.method, etc.)
    patterns = [
        rf'\b{re.escape(class_name)}\s*\(',
        rf'\b{re.escape(class_name)}\s*\[',
        rf'\b{re.escape(class_name)}\s*<',
        rf'\b{re.escape(class_name)}\s*\.',
        rf'<{re.escape(class_name)}\s*>',
        rf'extends\s+{re.escape(class_name)}',
        rf'implements\s+.*{re.escape(class_name)}',
        rf'catch\s*\(\s*{re.escape(class_name)}',
        rf'@\s*{re.escape(class_name)}',
    ]
    
    # Check if any pattern matches
    for pattern in patterns:
        if re.search(pattern, content):
            return True
    
    # Special case: Check for fully qualified names (might indicate import is unused)
    # But this is complex, so we'll be conservative
    
    return False

def find_unused_imports(file_path):
    """Find unused imports in a Java file."""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
    except Exception as e:
        print(f"Error reading {file_path}: {e}", file=sys.stderr)
        return []
    
    imports = extract_imports(content)
    unused = []
    
    for import_path in imports:
        # Skip package imports
        if import_path.endswith('.*'):
            continue
        
        usage = check_import_usage(content, import_path)
        if usage is False:  # Explicitly not used
            unused.append(import_path)
    
    return unused

def remove_imports(file_path, imports_to_remove):
    """Remove specified imports from a file."""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            lines = f.readlines()
    except Exception as e:
        print(f"Error reading {file_path}: {e}", file=sys.stderr)
        return False
    
    modified = False
    new_lines = []
    
    for line in lines:
        should_remove = False
        for import_to_remove in imports_to_remove:
            # Match import statement
            import_pattern = re.escape(import_to_remove)
            if re.match(r'^\s*import\s+' + import_pattern + r'\s*;\s*$', line):
                should_remove = True
                modified = True
                break
        
        if not should_remove:
            new_lines.append(line)
    
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
    
    files_with_unused = defaultdict(list)
    total_unused = 0
    
    # Find all Java files
    for java_file in test_dir.rglob('*.java'):
        if '.archived' in str(java_file):
            continue
        
        unused = find_unused_imports(java_file)
        if unused:
            files_with_unused[java_file] = unused
            total_unused += len(unused)
    
    if files_with_unused:
        print(f"Found {len(files_with_unused)} files with {total_unused} unused imports\n")
        for file_path, unused_imports in sorted(files_with_unused.items()):
            rel_path = file_path.relative_to(project_root)
            print(f"{rel_path}:")
            for imp in unused_imports:
                print(f"  - {imp}")
            print()
        
        response = input(f"\nRemove {total_unused} unused imports from {len(files_with_unused)} files? (yes/no): ")
        if response.lower() in ['yes', 'y']:
            removed_count = 0
            for file_path, unused_imports in files_with_unused.items():
                if remove_imports(file_path, unused_imports):
                    removed_count += len(unused_imports)
                    rel_path = file_path.relative_to(project_root)
                    print(f"Removed {len(unused_imports)} import(s) from: {rel_path}")
            print(f"\nRemoved {removed_count} unused imports from {len(files_with_unused)} files")
        else:
            print("Skipped removal")
    else:
        print("No unused imports found")

if __name__ == '__main__':
    main()


