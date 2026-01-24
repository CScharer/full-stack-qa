#!/usr/bin/env python3
"""
Fix SingularField PMD violations by converting fields to local variables

This script identifies fields that are only used in one method and converts
them to local variables within that method.

Example:
  private String gridUrl;
  public void test() {
      gridUrl = "http://example.com";
      // ... use gridUrl
  }
  
  ->
  
  public void test() {
      String gridUrl = "http://example.com";
      // ... use gridUrl
  }
"""

import os
import re
import subprocess
import sys

def get_pmd_violations():
    """Run PMD and extract SingularField violations"""
    result = subprocess.run(
        ['mvn', 'pmd:check'],
        capture_output=True,
        text=True,
        cwd=os.getcwd()
    )
    return result.stderr + result.stdout

def parse_violations(pmd_output):
    """Parse PMD output to extract SingularField violations"""
    violations = []
    
    for line in pmd_output.split('\n'):
        if 'SingularField' not in line or 'PMD Failure' not in line:
            continue
        
        # Format: [WARNING] PMD Failure: com.cjs.qa.class:line Rule:SingularField ... Perhaps 'fieldName' could be replaced by a local variable..
        match = re.search(r'PMD Failure: ([^:]+):(\d+).*Rule:SingularField.*Perhaps \'([^\']+)\'', line)
        if not match:
            continue
        
        class_path = match.group(1)
        line_num = int(match.group(2))
        field_name = match.group(3)
        violations.append((class_path, line_num, field_name))
    
    return violations

def class_path_to_file_path(class_path):
    """Convert class path to file path"""
    file_path = class_path.replace('.', '/')
    return f"src/test/java/{file_path}.java"

def fix_singular_field(file_path, line_num, field_name):
    """Fix SingularField violation by converting field to local variable"""
    with open(file_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    if line_num > len(lines):
        return False
    
    # Find the field declaration
    field_line_idx = line_num - 1
    field_line = lines[field_line_idx]
    
    # Extract field type and modifiers
    # Pattern: [modifiers] type fieldName [= initializer];
    field_match = re.match(r'^(\s*)(\w+\s+)*(\w+)\s+' + re.escape(field_name) + r'(\s*=\s*[^;]+)?;', field_line)
    if not field_match:
        # Try without initializer
        field_match = re.match(r'^(\s*)(\w+\s+)*(\w+)\s+' + re.escape(field_name) + r'\s*;', field_line)
        if not field_match:
            return False
    
    indent = field_match.group(1)
    modifiers = field_match.group(2) if field_match.group(2) else ""
    field_type = field_match.group(3)
    initializer = field_match.group(4) if len(field_match.groups()) > 4 and field_match.group(4) else ""
    
    # Find the method that uses this field
    # Look for method that contains assignment or usage of field_name
    method_start = None
    method_end = None
    brace_count = 0
    in_method = False
    
    # Search backwards from field to find class start
    class_start = 0
    for i in range(field_line_idx, -1, -1):
        if 'class ' in lines[i] or 'interface ' in lines[i]:
            class_start = i
            break
    
    # Search forward from field to find method that uses it
    for i in range(field_line_idx + 1, len(lines)):
        line = lines[i]
        
        # Check if we're entering a method
        if re.search(r'^\s*(public|private|protected|static|\s)*\s*\w+\s+\w+\s*\(', line):
            if method_start is None:
                method_start = i
                brace_count = 0
                in_method = True
        
        if in_method:
            brace_count += line.count('{') - line.count('}')
            
            # Check if field is used in this method
            if re.search(r'\b' + re.escape(field_name) + r'\b', line):
                if method_start is None:
                    method_start = i
                    brace_count = 0
                    in_method = True
                
                if brace_count == 0 and method_start is not None:
                    method_end = i
                    break
        
        if brace_count < 0:
            break
    
    if method_start is None:
        return False
    
    # Find method end if not found
    if method_end is None:
        brace_count = 0
        for i in range(method_start, len(lines)):
            brace_count += lines[i].count('{') - lines[i].count('}')
            if brace_count == 0 and i > method_start:
                method_end = i
                break
    
    if method_end is None:
        return False
    
    # Extract field declaration
    field_declaration = field_line.strip()
    
    # Remove field from class
    new_lines = lines[:field_line_idx] + lines[field_line_idx + 1:]
    
    # Find first usage in method and replace with local variable declaration
    method_lines = new_lines[method_start:method_end + 1]
    method_text = ''.join(method_lines)
    
    # Find first assignment or usage
    first_usage_pattern = r'(\s*)' + re.escape(field_name) + r'(\s*=\s*[^;]+);'
    first_usage_match = re.search(first_usage_pattern, method_text)
    
    if first_usage_match:
        # Replace first assignment with local variable declaration
        local_var_decl = f"{first_usage_match.group(1)}{field_type} {field_name}{first_usage_match.group(2)};"
        method_text = method_text.replace(first_usage_match.group(0), local_var_decl, 1)
    else:
        # No assignment found, add declaration at method start
        method_indent = re.match(r'^(\s*)', new_lines[method_start]).group(1)
        method_body_start = method_start + 1
        for i in range(method_start + 1, method_end + 1):
            if '{' in new_lines[i]:
                method_body_start = i + 1
                break
        
        # Add local variable declaration after opening brace
        local_var_decl = f"{method_indent}    {field_type} {field_name};\n"
        new_lines = new_lines[:method_body_start] + [local_var_decl] + new_lines[method_body_start:]
        method_end += 1
    
    # Write back
    with open(file_path, 'w', encoding='utf-8') as f:
        f.writelines(new_lines)
    
    return True

def main():
    print("=" * 60)
    print("Fix SingularField PMD Violations")
    print("=" * 60)
    print()
    
    print("Step 1: Analyzing PMD violations...")
    pmd_output = get_pmd_violations()
    
    violations = parse_violations(pmd_output)
    print(f"Found {len(violations)} SingularField violations")
    print()
    
    if not violations:
        print("No violations found!")
        return
    
    print("Step 2: Fixing violations...")
    fixed_count = 0
    skipped_count = 0
    error_count = 0
    
    for class_path, line_num, field_name in violations:
        file_path = class_path_to_file_path(class_path)
        if not os.path.exists(file_path):
            print(f"  ⚠️  File not found: {file_path}")
            error_count += 1
            continue
        
        try:
            if fix_singular_field(file_path, line_num, field_name):
                fixed_count += 1
                print(f"  ✅ Fixed {file_path}:{line_num} ({field_name})")
            else:
                skipped_count += 1
                print(f"  ⏭️  Skipped {file_path}:{line_num} ({field_name})")
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
