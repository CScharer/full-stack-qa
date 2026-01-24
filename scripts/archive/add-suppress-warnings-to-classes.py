#!/usr/bin/env python3
"""
Script to add @SuppressWarnings annotations at class level for PMD violations.

This script:
1. Reads the violations document to find pending violations
2. For each violation, adds @SuppressWarnings("PMD.RuleName") at the class level
3. Updates the violations document to mark violations as Fixed

Usage: python3 scripts/archive/add-suppress-warnings-to-classes.py
"""

import os
import re
import sys
from collections import defaultdict
from pathlib import Path

# PMD rule name mapping (PMD uses different names in SuppressWarnings)
RULE_MAPPING = {
    "AvoidCatchingThrowable": "PMD.AvoidCatchingThrowable",
    "AvoidStringBufferField": "PMD.AvoidStringBufferField",
    "GuardLogStatement": "PMD.GuardLogStatement",
    "LooseCoupling": "PMD.LooseCoupling",
    "NonThreadSafeSingleton": "PMD.NonThreadSafeSingleton",
    "UselessParentheses": "PMD.UselessParentheses",
}

# Rules to skip
SKIP_RULES = set()


def parse_violations_document(doc_path):
    """Parse the violations document and extract pending violations."""
    violations_by_class = defaultdict(lambda: defaultdict(list))
    
    with open(doc_path, 'r', encoding='utf-8') as f:
        for line in f:
            if '| ⏳ |' in line and '| Pending |' not in line:
                parts = [p.strip() for p in line.split('|')]
                if len(parts) >= 8:
                    file_name = parts[1]
                    rule = parts[2]
                    line_num = parts[6]
                    class_path = parts[8]
                    
                    if rule not in SKIP_RULES and rule in RULE_MAPPING:
                        violations_by_class[class_path][rule].append({
                            'file': file_name,
                            'line': line_num,
                            'original_line': line
                        })
    
    return violations_by_class


def find_class_file(class_path):
    """Convert class path to file path."""
    # Convert com/cjs/qa/... to src/test/java/com/cjs/qa/...
    file_path = f"src/test/java/{class_path}.java"
    if os.path.exists(file_path):
        return file_path
    return None


def add_suppress_warnings_to_class(file_path, rules_to_suppress):
    """Add @SuppressWarnings annotation to the class declaration."""
    if not os.path.exists(file_path):
        print(f"  ⚠️  File not found: {file_path}")
        return False
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
        lines = content.split('\n')
    
    # Find the class/interface/enum declaration
    class_line_idx = None
    for i, line in enumerate(lines):
        # Match: public/private/protected/abstract/final class/interface/enum, or package-private
        # Handle modifiers like: public final class, abstract class, public interface, etc.
        if re.search(r'^\s*(public\s+|private\s+|protected\s+|abstract\s+|final\s+|static\s+)*(class|interface|enum)\s+\w+', line):
            class_line_idx = i
            break
    
    if class_line_idx is None:
        print(f"  ⚠️  Could not find class declaration in {file_path}")
        return False
    
    # Check if @SuppressWarnings already exists
    suppress_warnings_annotation = None
    suppress_warnings_idx = None
    
    # Look for existing @SuppressWarnings before the class
    for i in range(class_line_idx - 1, max(-1, class_line_idx - 10), -1):
        if i < 0:
            break
        line = lines[i].strip()
        if line.startswith('@SuppressWarnings'):
            suppress_warnings_idx = i
            suppress_warnings_annotation = line
            break
        elif line and not line.startswith('@') and not line.startswith('//'):
            # Hit a non-annotation line, stop looking
            break
    
    # Build the suppression list
    pmd_rules = [RULE_MAPPING[rule] for rule in rules_to_suppress]
    
    if suppress_warnings_annotation:
        # Parse existing annotation
        # Extract existing rules from @SuppressWarnings("PMD.Rule1") or @SuppressWarnings({"PMD.Rule1", "PMD.Rule2"})
        existing_rules = set()
        # Try to match array format first: @SuppressWarnings({"PMD.Rule1", "PMD.Rule2"})
        array_match = re.search(r'@SuppressWarnings\(\{([^}]+)\}\)', suppress_warnings_annotation)
        if array_match:
            existing_rules_str = array_match.group(1)
            # Handle multiple rules in array format
            for rule in existing_rules_str.split(','):
                rule = rule.strip().strip('"\'')
                if rule:
                    existing_rules.add(rule)
        else:
            # Try single rule format: @SuppressWarnings("PMD.Rule1")
            single_match = re.search(r'@SuppressWarnings\(["\']([^"\']+)["\']\)', suppress_warnings_annotation)
            if single_match:
                existing_rules_str = single_match.group(1)
                # Handle both single and comma-separated rules
                for rule in existing_rules_str.split(','):
                    rule = rule.strip().strip('"\'')
                    if rule:
                        existing_rules.add(rule)
        
        # Add new rules
        all_rules = sorted(existing_rules.union(pmd_rules))
        
        if len(all_rules) == 1:
            new_annotation = f'@SuppressWarnings("{all_rules[0]}")'
        else:
            rules_str = ', '.join(f'"{r}"' for r in all_rules)
            new_annotation = f'@SuppressWarnings({{{rules_str}}})'
        
        # Replace existing annotation
        lines[suppress_warnings_idx] = lines[suppress_warnings_idx].replace(
            suppress_warnings_annotation, new_annotation
        )
    else:
        # Add new annotation before class
        if len(pmd_rules) == 1:
            new_annotation = f'@SuppressWarnings("{pmd_rules[0]}")'
        else:
            rules_str = ', '.join(f'"{r}"' for r in sorted(pmd_rules))
            new_annotation = f'@SuppressWarnings({{{rules_str}}})'
        
        # Insert before class declaration
        lines.insert(class_line_idx, new_annotation)
    
    # Write back
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))
    
    return True


def update_violations_document(doc_path, fixed_violations):
    """Update the violations document to mark violations as Fixed."""
    with open(doc_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    updated_lines = []
    for line in lines:
        updated_line = line
        for violation_info in fixed_violations:
            if violation_info['original_line'] in line:
                # Replace ⏳ with ✅ and add SuppressWarnings info
                parts = [p.strip() for p in line.split('|')]
                if len(parts) >= 8:
                    rule = parts[2]
                    pmd_rule = RULE_MAPPING.get(rule, f'PMD.{rule}')
                    parts[4] = f'@SuppressWarnings("{pmd_rule}")'  # SuppressWarnings column
                    parts[3] = '✅'  # Status column
                    updated_line = '| ' + ' | '.join(parts) + ' |\n'
                break
        updated_lines.append(updated_line)
    
    with open(doc_path, 'w', encoding='utf-8') as f:
        f.writelines(updated_lines)


def main():
    """Main execution."""
    project_root = Path(__file__).parent.parent.parent
    os.chdir(project_root)
    
    doc_path = "docs/work/20251224_VIOLATIONS.md"
    
    if not os.path.exists(doc_path):
        print(f"❌ Violations document not found: {doc_path}")
        sys.exit(1)
    
    print("📋 Parsing violations document...")
    violations_by_class = parse_violations_document(doc_path)
    
    if not violations_by_class:
        print("✅ No violations to fix")
        return
    
    print(f"\n📊 Found violations in {len(violations_by_class)} classes:")
    for class_path, rules in violations_by_class.items():
        total = sum(len(v) for v in rules.values())
        print(f"  {class_path}: {total} violations ({', '.join(rules.keys())})")
    
    print("\n🔧 Adding @SuppressWarnings annotations...")
    
    fixed_violations = []
    files_modified = set()
    
    for class_path, rules in violations_by_class.items():
        file_path = find_class_file(class_path)
        if not file_path:
            print(f"  ⚠️  Could not find file for {class_path}")
            continue
        
        rules_to_suppress = list(rules.keys())
        if add_suppress_warnings_to_class(file_path, rules_to_suppress):
            print(f"  ✅ {file_path}: Added @SuppressWarnings for {', '.join(rules_to_suppress)}")
            files_modified.add(file_path)
            
            # Collect violation info for document update
            for rule, violations in rules.items():
                fixed_violations.extend(violations)
        else:
            print(f"  ❌ Failed to update {file_path}")
    
    if fixed_violations:
        print("\n📝 Updating violations document...")
        update_violations_document(doc_path, fixed_violations)
        print(f"  ✅ Updated {len(fixed_violations)} violations to Fixed status")
    
    print(f"\n✅ Complete! Modified {len(files_modified)} files")
    print(f"   Fixed {len(fixed_violations)} violations")


if __name__ == "__main__":
    main()
