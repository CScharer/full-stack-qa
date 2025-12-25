#!/usr/bin/env python3
"""
Fix PMD Violations: UnnecessaryFullyQualifiedName, UselessParentheses, LiteralsFirstInComparisons
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
    """Parse PMD output to extract violations"""
    violations = {
        'UnnecessaryFullyQualifiedName': [],
        'UselessParentheses': [],
        'LiteralsFirstInComparisons': []
    }
    
    for line in pmd_output.split('\n'):
        if 'PMD Failure' not in line:
            continue
        
        # Format: [WARNING] PMD Failure: com.cjs.qa.class:line Rule:RuleName ...
        match = re.search(r'PMD Failure: ([^:]+):(\d+).*Rule:(\w+)', line)
        if not match:
            continue
        
        class_path = match.group(1)
        line_num = int(match.group(2))
        rule = match.group(3)
        
        if rule == 'UnnecessaryFullyQualifiedName':
            # Extract qualifier: ... qualifier 'QualifierName'
            qualifier_match = re.search(r"qualifier '([^']+)'", line)
            if qualifier_match:
                violations[rule].append((class_path, line_num, qualifier_match.group(1)))
        elif rule in ['UselessParentheses', 'LiteralsFirstInComparisons']:
            violations[rule].append((class_path, line_num, None))
    
    return violations

def class_path_to_file_path(class_path):
    """Convert class path to file path"""
    file_path = class_path.replace('.', '/')
    return f"src/test/java/{file_path}.java"

def fix_unnecessary_qualified_name(file_path, line_num, qualifier):
    """Remove unnecessary qualifier"""
    with open(file_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    if line_num > len(lines):
        return False
    
    line = lines[line_num - 1]
    escaped_qualifier = re.escape(qualifier)
    new_line = re.sub(rf'\b{escaped_qualifier}\.', '', line)
    
    if new_line != line:
        lines[line_num - 1] = new_line
        with open(file_path, 'w', encoding='utf-8') as f:
            f.writelines(lines)
        return True
    return False

def fix_useless_parentheses(file_path, line_num):
    """Remove useless parentheses"""
    with open(file_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    if line_num > len(lines):
        return False
    
    line = lines[line_num - 1]
    original = line
    
    # Fix !(methodCall()) -> !methodCall()
    line = re.sub(r'!\s*\(([a-zA-Z_][a-zA-Z0-9_.]*\s*\([^)]*\))\s*\)', r'!\1', line)
    
    # Fix return (expression); -> return expression;
    # Handle multi-line return statements with concatenation
    line = re.sub(r'return\s+\(([^)]+)\)\s*;', r'return \1;', line)
    
    # Fix ((expression)) -> (expression)
    line = re.sub(r'\(\(([^)]+)\)\)', r'(\1)', line)
    
    # Fix (Boolean) (expression) -> (Boolean) expression
    line = re.sub(r'\(([A-Z][a-zA-Z0-9_.]*)\)\s+\(([^)]+)\)', r'(\1) \2', line)
    
    # Fix (expression.length() + 1) -> expression.length() + 1 (when in return or assignment)
    # This is more context-dependent, so we'll be conservative
    if 'return' in line or '=' in line:
        line = re.sub(r'\(([a-zA-Z_][a-zA-Z0-9_.]*\.length\(\)\s*[+\-*/]\s*[^)]+)\)', r'\1', line)
    
    if line != original:
        lines[line_num - 1] = line
        with open(file_path, 'w', encoding='utf-8') as f:
            f.writelines(lines)
        return True
    return False

def fix_literals_first(file_path, line_num):
    """Put literals first in comparisons"""
    with open(file_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    if line_num > len(lines):
        return False
    
    line = lines[line_num - 1]
    original = line
    
    # Fix variable.equals("literal") -> "literal".equals(variable)
    line = re.sub(
        r'([a-zA-Z_][a-zA-Z0-9_.]*(?:\[[^\]]*\])?)\.equals\((["\'])([^"\']+)\2\)',
        r'\2\3\2.equals(\1)',
        line
    )
    
    # Fix variable.equals(Constant.VALUE) -> Constant.VALUE.equals(variable)
    line = re.sub(
        r'([a-zA-Z_][a-zA-Z0-9_.]*)\.equals\(([A-Z][a-zA-Z0-9_.]*)\)',
        r'\2.equals(\1)',
        line
    )
    
    if line != original:
        lines[line_num - 1] = line
        with open(file_path, 'w', encoding='utf-8') as f:
            f.writelines(lines)
        return True
    return False

def main():
    print("=" * 40)
    print("Fix PMD Violations")
    print("=" * 40)
    print()
    
    print("Step 1: Analyzing PMD violations...")
    pmd_output = get_pmd_violations()
    violations = parse_violations(pmd_output)
    
    qualified_count = len(violations['UnnecessaryFullyQualifiedName'])
    paren_count = len(violations['UselessParentheses'])
    literals_count = len(violations['LiteralsFirstInComparisons'])
    
    print(f"Found {qualified_count} UnnecessaryFullyQualifiedName violations")
    print(f"Found {paren_count} UselessParentheses violations")
    print(f"Found {literals_count} LiteralsFirstInComparisons violations")
    print()
    
    fixed_qualified = 0
    fixed_paren = 0
    fixed_literals = 0
    
    # Fix UnnecessaryFullyQualifiedName
    if qualified_count > 0:
        print("Step 2: Fixing UnnecessaryFullyQualifiedName violations...")
        for class_path, line_num, qualifier in violations['UnnecessaryFullyQualifiedName']:
            file_path = class_path_to_file_path(class_path)
            if os.path.exists(file_path):
                if fix_unnecessary_qualified_name(file_path, line_num, qualifier):
                    fixed_qualified += 1
        print(f"✅ Fixed {fixed_qualified} UnnecessaryFullyQualifiedName violations")
        print()
    
    # Fix UselessParentheses
    if paren_count > 0:
        print("Step 3: Fixing UselessParentheses violations...")
        for class_path, line_num, _ in violations['UselessParentheses']:
            file_path = class_path_to_file_path(class_path)
            if os.path.exists(file_path):
                if fix_useless_parentheses(file_path, line_num):
                    fixed_paren += 1
        print(f"✅ Fixed {fixed_paren} UselessParentheses violations")
        print()
    
    # Fix LiteralsFirstInComparisons
    if literals_count > 0:
        print("Step 4: Fixing LiteralsFirstInComparisons violations...")
        for class_path, line_num, _ in violations['LiteralsFirstInComparisons']:
            file_path = class_path_to_file_path(class_path)
            if os.path.exists(file_path):
                if fix_literals_first(file_path, line_num):
                    fixed_literals += 1
        print(f"✅ Fixed {fixed_literals} LiteralsFirstInComparisons violations")
        print()
    
    print("=" * 40)
    print("✅ Script completed")
    print("=" * 40)
    print()
    print("Summary:")
    print(f"  - UnnecessaryFullyQualifiedName violations fixed: {fixed_qualified}")
    print(f"  - UselessParentheses violations fixed: {fixed_paren}")
    print(f"  - LiteralsFirstInComparisons violations fixed: {fixed_literals}")
    print()

if __name__ == '__main__':
    main()
