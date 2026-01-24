#!/usr/bin/env python3
"""
Fix GuardLogStatement PMD violations by adding log level guards

This script wraps logger calls in log level guards to prevent unnecessary
string concatenation when logging is disabled.

Example:
  LOG.debug("Processing: " + value);
  ->
  if (LOG.isDebugEnabled()) {
      LOG.debug("Processing: " + value);
  }
"""

import os
import re
import subprocess
import sys

def get_pmd_violations():
    """Run PMD and extract GuardLogStatement violations"""
    result = subprocess.run(
        ['mvn', 'pmd:check'],
        capture_output=True,
        text=True,
        cwd=os.getcwd()
    )
    return result.stderr + result.stdout

def parse_violations(pmd_output):
    """Parse PMD output to extract GuardLogStatement violations"""
    violations = []
    
    for line in pmd_output.split('\n'):
        if 'GuardLogStatement' not in line or 'PMD Failure' not in line:
            continue
        
        # Format: [WARNING] PMD Failure: com.cjs.qa.class:line Rule:GuardLogStatement ...
        match = re.search(r'PMD Failure: ([^:]+):(\d+).*Rule:GuardLogStatement', line)
        if not match:
            continue
        
        class_path = match.group(1)
        line_num = int(match.group(2))
        violations.append((class_path, line_num))
    
    return violations

def class_path_to_file_path(class_path):
    """Convert class path to file path"""
    file_path = class_path.replace('.', '/')
    return f"src/test/java/{file_path}.java"

def fix_guard_log_statement(file_path, line_num):
    """Fix GuardLogStatement violation by adding log level guard"""
    with open(file_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    if line_num > len(lines):
        return False
    
    line = lines[line_num - 1]
    original_line = line.rstrip()
    
    # Skip if already has guard or NOPMD comment
    if 'NOPMD' in line or 'isDebugEnabled' in line or 'isInfoEnabled' in line or 'isWarnEnabled' in line or 'isErrorEnabled' in line or 'isTraceEnabled' in line:
        return False
    
    # Find the indentation
    indent_match = re.match(r'^(\s*)', line)
    if not indent_match:
        return False
    indent = indent_match.group(1)
    
    # Detect log level and method call
    log_level = None
    log_method = None
    
    if re.search(r'LOG\.debug\s*\(', line):
        log_level = 'debug'
        log_method = 'isDebugEnabled'
    elif re.search(r'LOG\.info\s*\(', line):
        log_level = 'info'
        log_method = 'isInfoEnabled'
    elif re.search(r'LOG\.warn\s*\(', line):
        log_level = 'warn'
        log_method = 'isWarnEnabled'
    elif re.search(r'LOG\.error\s*\(', line):
        log_level = 'error'
        log_method = 'isErrorEnabled'
    elif re.search(r'LOG\.trace\s*\(', line):
        log_level = 'trace'
        log_method = 'isTraceEnabled'
    
    if not log_level:
        return False
    
    # Check if it's a multi-line statement
    # Count opening and closing parentheses
    open_parens = line.count('(') - line.count(')')
    is_multiline = open_parens > 0
    
    if is_multiline:
        # Find the end of the statement
        end_line_idx = line_num - 1
        paren_count = open_parens
        while paren_count > 0 and end_line_idx < len(lines) - 1:
            end_line_idx += 1
            paren_count += lines[end_line_idx].count('(') - lines[end_line_idx].count(')')
        
        # Extract the full statement
        statement_lines = lines[line_num - 1:end_line_idx + 1]
        full_statement = ''.join(statement_lines)
        
        # Remove trailing semicolon and whitespace
        full_statement = full_statement.rstrip()
        if full_statement.endswith(';'):
            full_statement = full_statement[:-1].rstrip()
        
        # Create guard
        guard_start = f"{indent}if (LOG.{log_method}()) {{\n"
        guard_end = f"{indent}}}\n"
        
        # Indent the statement
        statement_indented = '\n'.join([indent + '    ' + l.lstrip() if l.strip() else l for l in statement_lines])
        statement_indented = statement_indented.rstrip()
        if statement_indented.endswith(';'):
            statement_indented = statement_indented[:-1].rstrip()
        statement_indented += ';\n'
        
        # Replace lines
        new_lines = lines[:line_num - 1]
        new_lines.append(guard_start)
        new_lines.append(statement_indented)
        new_lines.append(guard_end)
        new_lines.extend(lines[end_line_idx + 1:])
        
        lines = new_lines
    else:
        # Single line statement
        # Remove trailing semicolon
        statement = original_line.rstrip()
        if statement.endswith(';'):
            statement = statement[:-1].rstrip()
        
        # Create guard
        guard_start = f"{indent}if (LOG.{log_method}()) {{\n"
        guard_end = f"{indent}}}\n"
        statement_indented = f"{indent}    {statement};\n"
        
        # Replace line
        lines[line_num - 1] = guard_start + statement_indented + guard_end
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.writelines(lines)
    
    return True

def main():
    print("=" * 60)
    print("Fix GuardLogStatement PMD Violations")
    print("=" * 60)
    print()
    
    print("Step 1: Analyzing PMD violations...")
    pmd_output = get_pmd_violations()
    
    violations = parse_violations(pmd_output)
    print(f"Found {len(violations)} GuardLogStatement violations")
    print()
    
    if not violations:
        print("No violations found!")
        return
    
    print("Step 2: Fixing violations...")
    fixed_count = 0
    skipped_count = 0
    error_count = 0
    
    for class_path, line_num in violations:
        file_path = class_path_to_file_path(class_path)
        if not os.path.exists(file_path):
            print(f"  ⚠️  File not found: {file_path}")
            error_count += 1
            continue
        
        try:
            if fix_guard_log_statement(file_path, line_num):
                fixed_count += 1
                if fixed_count % 50 == 0:
                    print(f"  ✅ Fixed {fixed_count} violations...")
            else:
                skipped_count += 1
        except Exception as e:
            print(f"  ❌ Error fixing {file_path}:{line_num}: {e}")
            error_count += 1
    
    print()
    print("=" * 60)
    print("✅ Script completed")
    print("=" * 60)
    print()
    print(f"Summary:")
    print(f"  ✅ Fixed: {fixed_count}")
    print(f"  ⏭️  Skipped: {skipped_count}")
    print(f"  ❌ Errors: {error_count}")
    print()

if __name__ == '__main__':
    main()
