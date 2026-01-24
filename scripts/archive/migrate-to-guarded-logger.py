#!/usr/bin/env python3
"""
Migration script to replace Logger declarations with GuardedLogger.

This script:
1. Finds all files with "private static final Logger LOG = LogManager.getLogger"
2. Replaces Logger with GuardedLogger
3. Adds import for GuardedLogger
4. Removes @SuppressWarnings("PMD.GuardLogStatement") annotations
5. Updates the LOG declaration to use GuardedLogger

Usage:
    python3 scripts/archive/migrate-to-guarded-logger.py [--dry-run] [--file <file>]
    
Options:
    --dry-run    Show what would be changed without making changes
    --file       Process only a specific file
"""

import argparse
import os
import re
import sys
from pathlib import Path


def find_java_files(root_dir, specific_file=None):
    """Find all Java files that need migration."""
    java_files = []
    root_path = Path(root_dir)
    
    if specific_file:
        file_path = Path(specific_file)
        if file_path.exists() and file_path.suffix == '.java':
            java_files.append(file_path)
        return java_files
    
    for java_file in root_path.rglob('*.java'):
        # Skip the GuardedLogger class itself
        if 'GuardedLogger.java' in str(java_file):
            continue
        java_files.append(java_file)
    
    return java_files


def needs_migration(content):
    """Check if file needs migration."""
    # Check if it has Logger LOG or LOGGER declaration
    has_logger_decl = re.search(
        r'private\s+static\s+final\s+Logger\s+(LOG|LOGGER)\s*=',
        content
    )
    
    # Check if it has GuardLogStatement suppression
    has_suppression = 'PMD.GuardLogStatement' in content
    
    return has_logger_decl or has_suppression


def migrate_file(file_path, dry_run=False):
    """Migrate a single file to use GuardedLogger."""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
    except Exception as e:
        print(f"Error reading {file_path}: {e}", file=sys.stderr)
        return False
    
    if not needs_migration(content):
        return False
    
    original_content = content
    changes_made = []
    
    # 1. Replace Logger LOG/LOGGER declaration with GuardedLogger
    # Pattern: private static final Logger LOG = LogManager.getLogger(...);
    # Pattern: private static final Logger LOGGER = LogManager.getLogger(...);
    pattern1a = re.compile(
        r'private\s+static\s+final\s+Logger\s+LOG\s*=\s*LogManager\.getLogger\(([^)]+)\);',
        re.MULTILINE
    )
    
    def replace_logger_log(match):
        class_name = match.group(1)
        changes_made.append(f"Replaced Logger LOG with GuardedLogger")
        return f'private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger({class_name}));'
    
    content = pattern1a.sub(replace_logger_log, content)
    
    # Also handle LOGGER (not just LOG)
    pattern1b = re.compile(
        r'private\s+static\s+final\s+Logger\s+LOGGER\s*=\s*LogManager\.getLogger\(([^)]+)\);',
        re.MULTILINE
    )
    
    def replace_logger_logger(match):
        class_name = match.group(1)
        changes_made.append(f"Replaced Logger LOGGER with GuardedLogger")
        return f'private static final GuardedLogger LOGGER = new GuardedLogger(LogManager.getLogger({class_name}));'
    
    content = pattern1b.sub(replace_logger_logger, content)
    
    # 2. Add import for GuardedLogger if not present
    if 'GuardedLogger' in content and 'import com.cjs.qa.utilities.GuardedLogger;' not in content:
        # Find the last import statement
        import_pattern = re.compile(r'^import\s+[^;]+;', re.MULTILINE)
        imports = list(import_pattern.finditer(content))
        
        if imports:
            last_import = imports[-1]
            insert_pos = last_import.end()
            new_import = '\nimport com.cjs.qa.utilities.GuardedLogger;'
            content = content[:insert_pos] + new_import + content[insert_pos:]
            changes_made.append("Added GuardedLogger import")
        else:
            # No imports found, add after package declaration
            package_match = re.search(r'^package\s+[^;]+;', re.MULTILINE)
            if package_match:
                insert_pos = package_match.end()
                new_import = '\n\nimport com.cjs.qa.utilities.GuardedLogger;'
                content = content[:insert_pos] + new_import + content[insert_pos:]
                changes_made.append("Added GuardedLogger import")
    
    # 3. Remove @SuppressWarnings("PMD.GuardLogStatement") from class level
    # Pattern: @SuppressWarnings("PMD.GuardLogStatement") or @SuppressWarnings({"PMD.GuardLogStatement", ...})
    # Remove standalone GuardLogStatement suppression
    pattern2a = re.compile(
        r'@SuppressWarnings\("PMD\.GuardLogStatement"\)\s*\n',
        re.MULTILINE
    )
    if pattern2a.search(content):
        content = pattern2a.sub('', content)
        changes_made.append("Removed standalone @SuppressWarnings(\"PMD.GuardLogStatement\")")
    
    # Remove from multi-value SuppressWarnings
    # Pattern: @SuppressWarnings({"PMD.GuardLogStatement", "other"}) -> @SuppressWarnings("other")
    pattern2b = re.compile(
        r'@SuppressWarnings\(\{\s*"PMD\.GuardLogStatement"\s*,\s*"([^"]+)"\s*\}\)',
        re.MULTILINE
    )
    if pattern2b.search(content):
        def remove_guardlog(match):
            other_warning = match.group(1)
            changes_made.append(f"Removed PMD.GuardLogStatement from multi-value SuppressWarnings")
            return f'@SuppressWarnings("{other_warning}")'
        content = pattern2b.sub(remove_guardlog, content)
    
    # Pattern: @SuppressWarnings({"other", "PMD.GuardLogStatement"}) -> @SuppressWarnings("other")
    pattern2c = re.compile(
        r'@SuppressWarnings\(\{\s*"([^"]+)"\s*,\s*"PMD\.GuardLogStatement"\s*\}\)',
        re.MULTILINE
    )
    if pattern2c.search(content):
        def remove_guardlog2(match):
            other_warning = match.group(1)
            changes_made.append(f"Removed PMD.GuardLogStatement from multi-value SuppressWarnings")
            return f'@SuppressWarnings("{other_warning}")'
        content = pattern2c.sub(remove_guardlog2, content)
    
    # Pattern: @SuppressWarnings({"PMD.GuardLogStatement"}) -> remove entirely
    pattern2d = re.compile(
        r'@SuppressWarnings\(\{\s*"PMD\.GuardLogStatement"\s*\}\)\s*\n',
        re.MULTILINE
    )
    if pattern2d.search(content):
        content = pattern2d.sub('', content)
        changes_made.append("Removed @SuppressWarnings({\"PMD.GuardLogStatement\"})")
    
    if content != original_content:
        if dry_run:
            print(f"\n{'='*80}")
            print(f"Would modify: {file_path}")
            print(f"{'='*80}")
            for change in changes_made:
                print(f"  - {change}")
            print(f"\nDiff preview:")
            # Show a simple diff of key changes
            if 'GuardedLogger' in content and 'GuardedLogger' not in original_content:
                logger_line_old = [l for l in original_content.split('\n') if 'Logger LOG =' in l]
                logger_line_new = [l for l in content.split('\n') if 'GuardedLogger LOG =' in l]
                if logger_line_old and logger_line_new:
                    print(f"  - {logger_line_old[0].strip()}")
                    print(f"  + {logger_line_new[0].strip()}")
        else:
            try:
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"✅ Migrated: {file_path}")
                for change in changes_made:
                    print(f"   - {change}")
                return True
            except Exception as e:
                print(f"❌ Error writing {file_path}: {e}", file=sys.stderr)
                return False
    else:
        if dry_run:
            print(f"⏭️  No changes needed: {file_path}")
    
    return False


def main():
    parser = argparse.ArgumentParser(
        description='Migrate Logger declarations to GuardedLogger'
    )
    parser.add_argument(
        '--dry-run',
        action='store_true',
        help='Show what would be changed without making changes'
    )
    parser.add_argument(
        '--file',
        type=str,
        help='Process only a specific file'
    )
    parser.add_argument(
        '--root',
        type=str,
        default='src',
        help='Root directory to search (default: src)'
    )
    
    args = parser.parse_args()
    
    java_files = find_java_files(args.root, args.file)
    
    if not java_files:
        print("No Java files found to process.")
        return
    
    print(f"Found {len(java_files)} Java file(s) to check...")
    
    migrated_count = 0
    for java_file in java_files:
        if migrate_file(java_file, dry_run=args.dry_run):
            migrated_count += 1
    
    if args.dry_run:
        print(f"\n{'='*80}")
        print(f"DRY RUN: Would migrate {migrated_count} file(s)")
        print(f"{'='*80}")
    else:
        print(f"\n{'='*80}")
        print(f"Migration complete: {migrated_count} file(s) migrated")
        print(f"{'='*80}")


if __name__ == '__main__':
    main()
