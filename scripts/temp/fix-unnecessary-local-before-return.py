#!/usr/bin/env python3
"""
Fix PMD UnnecessaryLocalBeforeReturn violations

This script fixes violations where a local variable is assigned a value
and then immediately returned, when the value could be returned directly.
"""

import os
import re
import subprocess
import sys

def get_pmd_violations():
    """Run PMD and extract violations"""
    result = subprocess.run(
        ['mvn', 'pmd:check'],
        capture_output=True,
        text=True,
        cwd=os.getcwd()
    )
    return result.stderr + result.stdout

def parse_violations(pmd_output):
    """Parse PMD output to extract UnnecessaryLocalBeforeReturn violations"""
    violations = []
    
    for line in pmd_output.split('\n'):
        if 'UnnecessaryLocalBeforeReturn' not in line or 'PMD Failure' not in line:
            continue
        
        # Format: [WARNING] PMD Failure: com.cjs.qa.class:line Rule:UnnecessaryLocalBeforeReturn ...
        match = re.search(r'PMD Failure: ([^:]+):(\d+).*Rule:UnnecessaryLocalBeforeReturn', line)
        if not match:
            continue
        
        class_path = match.group(1)
        line_num = int(match.group(2))
        
        # Extract variable name from the message
        var_match = re.search(r"local variable '([^']+)'", line)
        variable_name = var_match.group(1) if var_match else None
        
        violations.append((class_path, line_num, variable_name))
    
    return violations

def class_path_to_file_path(class_path):
    """Convert class path to file path"""
    file_path = class_path.replace('.', '/')
    return f"src/test/java/{file_path}.java"

def fix_unnecessary_local_before_return(file_path, line_num, variable_name):
    """Fix UnnecessaryLocalBeforeReturn violation"""
    with open(file_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    if line_num > len(lines):
        return False
    
    # Get the line with the return statement
    return_line_idx = line_num - 1
    return_line = lines[return_line_idx]
    
    # Find the assignment line (look backwards from return line)
    assignment_line_idx = None
    for i in range(return_line_idx - 1, max(-1, return_line_idx - 20), -1):
        if i < 0:
            break
        line = lines[i].strip()
        # Look for pattern: variableName = ...;
        if variable_name and re.match(rf'^\s*{re.escape(variable_name)}\s*=\s*.+;\s*$', line):
            assignment_line_idx = i
            break
    
    if assignment_line_idx is None:
        return False
    
    assignment_line = lines[assignment_line_idx]
    return_line = lines[return_line_idx]
    
    # Extract the value being assigned
    # Pattern: variableName = value;
    assignment_match = re.search(rf'{re.escape(variable_name)}\s*=\s*(.+);', assignment_line)
    if not assignment_match:
        return False
    
    assigned_value = assignment_match.group(1).strip()
    
    # Extract return statement
    # Pattern: return variableName;
    return_match = re.search(r'return\s+' + re.escape(variable_name) + r'\s*;', return_line)
    if not return_match:
        return False
    
    # Replace return statement with direct return
    new_return_line = re.sub(
        r'return\s+' + re.escape(variable_name) + r'\s*;',
        f'return {assigned_value};',
        return_line
    )
    
    # Remove the assignment line
    lines[assignment_line_idx] = ''
    lines[return_line_idx] = new_return_line
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.writelines(lines)
    
    return True

def main():
    print("=" * 40)
    print("Fix PMD UnnecessaryLocalBeforeReturn Violations")
    print("=" * 40)
    print()
    
    print("Step 1: Analyzing PMD violations...")
    pmd_output = get_pmd_violations()
    violations = parse_violations(pmd_output)
    
    violation_count = len(violations)
    print(f"Found {violation_count} UnnecessaryLocalBeforeReturn violations")
    print()
    
    if violation_count == 0:
        print("✅ No violations found!")
        return
    
    fixed_count = 0
    
    print("Step 2: Fixing violations...")
    for class_path, line_num, variable_name in violations:
        file_path = class_path_to_file_path(class_path)
        if os.path.exists(file_path):
            print(f"  Fixing {class_path}:{line_num} (variable: {variable_name})")
            if fix_unnecessary_local_before_return(file_path, line_num, variable_name):
                fixed_count += 1
                print(f"    ✅ Fixed")
            else:
                print(f"    ⚠️  Could not auto-fix (may need manual review)")
        else:
            print(f"  ⚠️  File not found: {file_path}")
    
    print()
    print("=" * 40)
    print("✅ Script completed")
    print("=" * 40)
    print()
    print(f"Summary:")
    print(f"  - Violations found: {violation_count}")
    print(f"  - Violations fixed: {fixed_count}")
    print()

if __name__ == '__main__':
    main()
