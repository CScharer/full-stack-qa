#!/usr/bin/env python3
"""Check all markdown links in all documentation files."""
import os
import re
from pathlib import Path

def check_links_in_file(file_path):
    """Check all markdown links in a file and verify they exist."""
    broken_links = []
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
        # Find all markdown links [text](path)
        links = re.findall(r'\[([^\]]+)\]\(([^)]+)\)', content)
        base_dir = Path(file_path).parent.resolve()
        
        for text, link in links:
            # Skip external links
            if link.startswith('http://') or link.startswith('https://') or link.startswith('mailto:'):
                continue
            
            # Handle anchor links (check file part only)
            file_part = link.split('#')[0] if '#' in link else link
            
            # Skip empty links
            if not file_part:
                continue
            
            # Resolve relative paths
            if file_part.startswith('../') or file_part.startswith('./'):
                target = (base_dir / file_part).resolve()
            elif file_part.startswith('/'):
                # Absolute path from project root
                project_root = Path(file_path).resolve()
                while project_root.parent != project_root:
                    if (project_root / '.git').exists() or (project_root / 'README.md').exists():
                        break
                    project_root = project_root.parent
                target = (project_root / file_part[1:]).resolve()
            else:
                target = (base_dir / file_part).resolve()
            
            # Check if file exists
            if not target.exists() or not target.is_file():
                broken_links.append((text, link, str(target)))
    
    return broken_links

def find_all_markdown_files(root_dir):
    """Find all markdown files in a directory."""
    markdown_files = []
    for root, dirs, files in os.walk(root_dir):
        # Skip hidden directories and common exclusions
        dirs[:] = [d for d in dirs if not d.startswith('.') and d not in ['node_modules', 'target', 'build', '__pycache__']]
        for file in files:
            if file.endswith('.md'):
                markdown_files.append(os.path.join(root, file))
    return markdown_files

if __name__ == '__main__':
    # Get project root
    script_dir = Path(__file__).parent
    project_root = script_dir.parent.parent
    
    # Find all markdown files in docs and scripts
    markdown_files = []
    for directory in ['docs', 'scripts']:
        dir_path = project_root / directory
        if dir_path.exists():
            markdown_files.extend(find_all_markdown_files(str(dir_path)))
    
    print(f"Checking {len(markdown_files)} markdown files for broken links...\n")
    
    all_broken = []
    files_with_broken_links = []
    
    for file_path in sorted(markdown_files):
        broken = check_links_in_file(file_path)
        if broken:
            files_with_broken_links.append(file_path)
            all_broken.extend([(file_path, text, link) for text, link, _ in broken])
    
    # Report results
    if not all_broken:
        print("✅ All links are valid in all markdown files!")
    else:
        print(f"❌ Found {len(all_broken)} broken link(s) in {len(files_with_broken_links)} file(s):\n")
        for file_path in files_with_broken_links:
            print(f"\n{file_path}:")
            for fp, text, link in all_broken:
                if fp == file_path:
                    print(f"  - [{text}]({link}) (NOT FOUND)")
