#!/usr/bin/env python3
"""
Script to migrate remaining Environment.sysOut calls to Log4j 2.x.
Handles all remaining patterns including complex concatenations.
"""

import os
import re
import sys
from pathlib import Path

def add_log_imports_and_field(content, class_name):
    """Add Log4j imports and LOG field if not present."""
    # Skip for interfaces - they can't have static fields in the same way
    if 'interface ' + class_name in content or 'public interface ' + class_name in content:
        return content
    
    has_log_manager = 'import org.apache.logging.log4j.LogManager' in content
    has_logger = 'import org.apache.logging.log4j.Logger' in content
    has_log_field = f'private static final Logger LOG = LogManager.getLogger({class_name}.class)' in content
    has_log_field_alt = f'Logger LOG = LogManager.getLogger({class_name}.class)' in content
    
    if not has_log_manager or not has_logger:
        # Find the import section - look for package statement
        import_pattern = r'(package\s+[\w.]+;\s*\n)'
        match = re.search(import_pattern, content)
        if match:
            insert_pos = match.end()
            imports_to_add = []
            if not has_log_manager:
                imports_to_add.append('import org.apache.logging.log4j.LogManager;')
            if not has_logger:
                imports_to_add.append('import org.apache.logging.log4j.Logger;')
            
            if imports_to_add:
                content = content[:insert_pos] + '\n'.join(imports_to_add) + '\n' + content[insert_pos:]
    
    if not has_log_field and not has_log_field_alt:
        # Find the class declaration
        class_pattern = rf'public\s+(?:static\s+)?(?:final\s+)?class\s+{class_name}\s*(?:extends\s+\w+)?\s*(?:implements\s+[\w\s,]+)?\s*\{{'
        match = re.search(class_pattern, content)
        if match:
            insert_pos = match.end()
            log_field = f'\n  private static final Logger LOG = LogManager.getLogger({class_name}.class);\n  '
            content = content[:insert_pos] + log_field + content[insert_pos:]
    
    return content

def migrate_environment_sysout(content, include_comments=False):
    """Replace all Environment.sysOut patterns with appropriate LOG levels."""
    
    # First, handle VivitEnvironment and MicrosoftEnvironment
    content = re.sub(r'VivitEnvironment\.sysOut', 'Environment.sysOut', content)
    content = re.sub(r'MicrosoftEnvironment\.sysOut', 'Environment.sysOut', content)
    
    # Handle System.out.println and System.err.println
    # System.err.println should be LOG.error
    content = re.sub(r'System\.err\.println\("([^"]+)"\);', r'LOG.error("\1");', content)
    content = re.sub(r'System\.err\.println\(([^)]+)\);', r'LOG.error("{}", \1);', content)
    
    # System.out.println should be LOG.debug or LOG.info based on content
    def replace_system_out(match):
        msg = match.group(1) if match.lastindex else match.group(0)
        if isinstance(msg, str) and any(word in msg.lower() for word in ['error', 'exception', 'failed', 'fail']):
            return 'LOG.error("' + msg + '");'
        elif isinstance(msg, str) and any(word in msg.lower() for word in ['warn', 'warning']):
            return 'LOG.warn("' + msg + '");'
        else:
            return 'LOG.debug("' + msg + '");'
    
    # Simple string messages
    content = re.sub(r'System\.out\.println\("([^"]+)"\);', replace_system_out, content)
    # With expressions
    content = re.sub(r'System\.out\.println\(([^)]+)\);', r'LOG.debug("{}", \1);', content)
    
    # Handle commented-out calls if requested
    if include_comments:
        # Uncomment and migrate commented-out calls
        # Pattern: // Environment.sysOut(...)
        content = re.sub(r'//\s*Environment\.sysOut', 'LOG.debug', content)
        # Pattern: //   Environment.sysOut(...) (with indentation)
        content = re.sub(r'//\s+Environment\.sysOut', 'LOG.debug', content)
        # Pattern: // System.out.println(...)
        content = re.sub(r'//\s*System\.out\.println', 'LOG.debug', content)
        # Pattern: // System.err.println(...)
        content = re.sub(r'//\s*System\.err\.println', 'LOG.error', content)
    
    # Handle Environment.sysOutFailure - treat as error
    content = re.sub(r'Environment\.sysOutFailure\("([^"]+)"\);', r'LOG.error("\1");', content)
    
    # Pattern 1: Multi-line Environment.sysOut with JavaHelpers.getCurrentClassMethodName() + URL pattern
    pattern1 = re.compile(
        r'Environment\.sysOut\(\s*\n\s*JavaHelpers\.getCurrentClassMethodName\(\)\s*\n\s*\+\s*"\s*-\s*"\s*\n\s*\+\s*URL_YM_API_DOC\s*\n\s*\+\s*"([^"]+)"\s*\n\s*\+\s*IExtension\.HTM\s*\);',
        re.MULTILINE
    )
    def replace1(match):
        method_name = match.group(1)
        return 'LOG.debug("{} - {}{}{}", JavaHelpers.getCurrentClassMethodName(), URL_YM_API_DOC, "' + method_name + '", IExtension.HTM);'
    content = pattern1.sub(replace1, content)
    
    # Pattern 2: Single-line version of pattern 1
    pattern2 = re.compile(
        r'Environment\.sysOut\(\s*JavaHelpers\.getCurrentClassMethodName\(\)\s*\+\s*"\s*-\s*"\s*\+\s*URL_YM_API_DOC\s*\+\s*"([^"]+)"\s*\+\s*IExtension\.HTM\s*\);',
        re.MULTILINE
    )
    def replace2(match):
        method_name = match.group(1)
        return 'LOG.debug("{} - {}{}{}", JavaHelpers.getCurrentClassMethodName(), URL_YM_API_DOC, "' + method_name + '", IExtension.HTM);'
    content = pattern2.sub(replace2, content)
    
    # Pattern 3: Simple string messages in quotes
    pattern3 = re.compile(
        r'Environment\.sysOut\("([^"]+)"\);',
        re.MULTILINE
    )
    def replace3(match):
        msg = match.group(1)
        # Use appropriate log level based on message content
        if any(word in msg.lower() for word in ['error', 'exception', 'failed', 'fail']):
            return 'LOG.error("' + msg + '");'
        elif any(word in msg.lower() for word in ['warn', 'warning']):
            return 'LOG.warn("' + msg + '");'
        else:
            return 'LOG.debug("' + msg + '");'
    content = pattern3.sub(replace3, content)
    
    # Pattern 4: Environment.sysOut with string concatenation (variable + string)
    pattern4 = re.compile(
        r'Environment\.sysOut\("([^"]+)"\s*\+\s*([^)]+)\);',
        re.MULTILINE
    )
    def replace4(match):
        prefix = match.group(1)
        var_expr = match.group(2).strip()
        # Extract variable name if possible
        var_match = re.search(r'(\w+)\.(toString\(\)|getText\(\)|getMessage\(\))', var_expr)
        if var_match:
            var_name = var_match.group(1)
            if 'toString()' in var_expr:
                return 'LOG.debug("' + prefix + '[{}]", ' + var_name + '.toString());'
            elif 'getText()' in var_expr:
                return 'LOG.debug("' + prefix + '[{}]", ' + var_name + '.getText());'
            elif 'getMessage()' in var_expr:
                return 'LOG.error("' + prefix + '[{}]", ' + var_name + '.getMessage());'
        # Fallback: use the full expression
        return 'LOG.debug("' + prefix + '[{}]", ' + var_expr + ');'
    content = pattern4.sub(replace4, content)
    
    # Pattern 4b: String concatenation with parentheses: "Item Index (" + var + ")"
    pattern4b = re.compile(
        r'Environment\.sysOut\("([^"]+)\("\s*\+\s*([^)]+)\s*\+\s*"\)"\);',
        re.MULTILINE
    )
    def replace4b(match):
        prefix = match.group(1)
        var_expr = match.group(2).strip()
        return 'LOG.debug("' + prefix + '({})", ' + var_expr + ');'
    content = pattern4b.sub(replace4b, content)
    
    # Pattern 4c: String concatenation with closing bracket: "text:" + var + "]"
    pattern4c = re.compile(
        r'Environment\.sysOut\("([^"]+):"\s*\+\s*([^)]+)\s*\+\s*"\]"\);',
        re.MULTILINE
    )
    def replace4c(match):
        prefix = match.group(1)
        var_expr = match.group(2).strip()
        return 'LOG.debug("' + prefix + ':[{}]", ' + var_expr + ');'
    content = pattern4c.sub(replace4c, content)
    
    # Pattern 4d: String concatenation with opening bracket: "text:]" + var + "]"
    pattern4d = re.compile(
        r'Environment\.sysOut\("([^"]+):\]"\s*\+\s*([^)]+)\s*\+\s*"\]"\);',
        re.MULTILINE
    )
    def replace4d(match):
        prefix = match.group(1)
        var_expr = match.group(2).strip()
        return 'LOG.debug("' + prefix + ':[{}]", ' + var_expr + ');'
    content = pattern4d.sub(replace4d, content)
    
    # Pattern 4e: Simple concatenation: "text" + var
    pattern4e = re.compile(
        r'Environment\.sysOut\("([^"]+)"\s*\+\s*([^)]+)\);',
        re.MULTILINE
    )
    def replace4e(match):
        prefix = match.group(1)
        var_expr = match.group(2).strip()
        # Check if it's an error message
        if 'ERROR' in prefix.upper() or 'Error' in prefix:
            return 'LOG.error("' + prefix + '{}", ' + var_expr + ');'
        return 'LOG.debug("' + prefix + '{}", ' + var_expr + ');'
    content = pattern4e.sub(replace4e, content)
    
    # Pattern 4g: Multiple concatenations: "text:[" + var1 + "], text:" + var2 + "]"
    pattern4g = re.compile(
        r'Environment\.sysOut\("([^"]+):\["\s*\+\s*([^)]+)\s*\+\s*"\],\s*([^"]+):"\s*\+\s*([^)]+)\s*\+\s*"\]"\);',
        re.MULTILINE
    )
    def replace4g(match):
        label1 = match.group(1)
        var1 = match.group(2).strip()
        label2 = match.group(3)
        var2 = match.group(4).strip()
        return 'LOG.debug("' + label1 + ':[{}], ' + label2 + ':[{}]", ' + var1 + ', ' + var2 + ');'
    content = pattern4g.sub(replace4g, content)
    
    # Pattern 4h: Simple concatenation with parentheses: "text (" + var + ")"
    pattern4h = re.compile(
        r'Environment\.sysOut\("([^"]+)\s*\(\("\s*\+\s*([^)]+)\s*\+\s*"\)"\);',
        re.MULTILINE
    )
    def replace4h(match):
        prefix = match.group(1)
        var_expr = match.group(2).strip()
        return 'LOG.debug("' + prefix + ' ({})", ' + var_expr + ');'
    content = pattern4h.sub(replace4h, content)
    
    # Pattern 4f: Multi-line string concatenation: "text:[" + var + "]"
    pattern4f = re.compile(
        r'Environment\.sysOut\(\s*\n\s*"([^"]+):\["\s*\n\s*\+\s*([^)]+)\s*\n\s*\+\s*"\]"\s*\n\s*\);',
        re.MULTILINE | re.DOTALL
    )
    def replace4f(match):
        prefix = match.group(1)
        var_expr = match.group(2).strip()
        return 'LOG.debug("' + prefix + ':[{}]", ' + var_expr + ');'
    content = pattern4f.sub(replace4f, content)
    
    # Pattern 5: Environment.sysOut with method calls like toString(), getText(), etc.
    pattern5 = re.compile(
        r'Environment\.sysOut\(([^)]+\.(?:toString|getText|getMessage|getEventsAllSearch|getEventsEventGet|getSaEventsEventRegistrationGet|split|asNormalizedText)\([^)]*\))\);',
        re.MULTILINE
    )
    def replace5(match):
        expr = match.group(1).strip()
        # Check if it's a length call (like .split(...).length)
        if '.length' in expr:
            # Extract the expression before .length
            base_expr = expr.replace('.length', '')
            return 'LOG.debug("Length: {}", ' + base_expr + '.length);'
        return 'LOG.debug("{}", ' + expr + ');'
    content = pattern5.sub(replace5, content)
    
    # Pattern 5f: Environment.sysOut with .split(...).length pattern
    pattern5f = re.compile(
        r'Environment\.sysOut\(([^)]+\.split\([^)]+\)\.length)\);',
        re.MULTILINE
    )
    def replace5f(match):
        expr = match.group(1).strip()
        # Extract the base expression before .split
        base_match = re.match(r'([^.]+\.[^.]+)\.split', expr)
        if base_match:
            base_expr = base_match.group(1)
            return 'LOG.debug("Length: {}", ' + expr + ');'
        return 'LOG.debug("Length: {}", ' + expr + ');'
    content = pattern5f.sub(replace5f, content)
    
    # Pattern 5b: Environment.sysOut with simple method calls like toString()
    pattern5b = re.compile(
        r'Environment\.sysOut\((\w+)\(\)\);',
        re.MULTILINE
    )
    def replace5b(match):
        method = match.group(1)
        if method == 'toString':
            return 'LOG.debug("{}", toString());'
        return 'LOG.debug("{}", ' + method + '());'
    content = pattern5b.sub(replace5b, content)
    
    # Pattern 5c: Environment.sysOut with complex expressions (method chains, etc.)
    # Fixed ReDoS: Use more specific patterns to avoid exponential backtracking
    # Split into two parts: method call and optional concatenations
    pattern5c = re.compile(
        r'Environment\.sysOut\(([^)]*\.(?:toString|getText|getMessage|getCurrentMethodName|getCurrentClassMethodName|getParameters|getEventsAllSearch|getEventsEventGet|getSaEventsEventRegistrationGet)\([^)]*\)(?:\s*\+\s*"[^"]*")*(?:\s*\+\s*[^)]*)?)\);',
        re.MULTILINE
    )
    def replace5c(match):
        expr = match.group(1).strip()
        # Check if it contains ERROR
        if 'ERROR' in expr or 'Error' in expr:
            return 'LOG.error("{}", ' + expr + ');'
        return 'LOG.debug("{}", ' + expr + ');'
    content = pattern5c.sub(replace5c, content)
    
    # Pattern 5g: Simple method call chains like yourMembershipResponse.getEventsAllSearch().toString()
    pattern5g = re.compile(
        r'Environment\.sysOut\(([^)]+\.(?:getEventsAllSearch|getEventsEventGet|getSaEventsEventRegistrationGet)\(\)\.toString\(\))\);',
        re.MULTILINE
    )
    def replace5g(match):
        expr = match.group(1).strip()
        return 'LOG.debug("{}", ' + expr + ');'
    content = pattern5g.sub(replace5g, content)
    
    # Pattern 5d: Environment.sysOut(e) - exception logging
    pattern5d = re.compile(
        r'Environment\.sysOut\((\w+)\);',
        re.MULTILINE
    )
    def replace5d(match):
        var = match.group(1)
        if var == 'e' or 'Exception' in var or 'Throwable' in var:
            return 'LOG.error("Exception occurred", ' + var + ');'
        return 'LOG.debug("{}", ' + var + ');'
    content = pattern5d.sub(replace5d, content)
    
    # Pattern 5e: Environment.sysOut("") - empty string
    pattern5e = re.compile(
        r'Environment\.sysOut\(""\);',
        re.MULTILINE
    )
    def replace5e(match):
        return 'LOG.debug("");'
    content = pattern5e.sub(replace5e, content)
    
    # Pattern 6: Complex multi-line concatenations (CONNECTED_TO pattern)
    pattern6 = re.compile(
        r'Environment\.sysOut\(\s*\n\s*CONNECTED_TO\s*\n\s*\+\s*(\w+)\s*\n\s*\+\s*"\]\s*(unsuccessful|successfull)\s+with\s+response\s+of\s+\["\s*\n\s*\+\s*(\w+)\s*\n\s*\+\s*":"\s*\n\s*\+\s*(\w+)\s*\([^)]+\)\s*\n\s*\+\s*"\]\.\s*"\);',
        re.MULTILINE
    )
    def replace6(match):
        url_var = match.group(1)
        status = match.group(2)
        code_var = match.group(3)
        func_call = match.group(4)
        level = 'warn' if 'unsuccessful' in status else 'debug'
        return f'LOG.{level}("{{}}{{}}] {status} with response of [{{}}:{{}}].", CONNECTED_TO, {url_var}, {code_var}, {func_call}({code_var}));'
    content = pattern6.sub(replace6, content)
    
    # Pattern 7: Simple concatenation patterns like "text:[" + variable + "]"
    pattern7 = re.compile(
        r'Environment\.sysOut\("([^"]+):\["\s*\+\s*([^)]+)\s*\+\s*"\]"\);',
        re.MULTILINE
    )
    def replace7(match):
        label = match.group(1)
        var_expr = match.group(2).strip()
        return 'LOG.debug("' + label + ':[{}]", ' + var_expr + ');'
    content = pattern7.sub(replace7, content)
    
    # Pattern 8: Array/list toString patterns
    pattern8 = re.compile(
        r'Environment\.sysOut\("([^"]+):\["\s*\+\s*(\w+)\.toString\(\)\s*\+\s*"\]"\);',
        re.MULTILINE
    )
    def replace8(match):
        label = match.group(1)
        var_name = match.group(2)
        return 'LOG.debug("' + label + ':[{}]", ' + var_name + '.toString());'
    content = pattern8.sub(replace8, content)
    
    # Pattern 9: Multi-line Environment.sysOut calls with concatenation
    pattern9 = re.compile(
        r'Environment\.sysOut\(\s*\n\s*([^;]+)\s*\n\s*\);',
        re.MULTILINE | re.DOTALL
    )
    def replace9(match):
        expr = match.group(1).strip()
        # Try to extract meaningful parts
        if 'CONNECTED_TO' in expr:
            # Already handled by pattern6, skip
            return match.group(0)
        # Check if it's an error message
        if 'ERROR' in expr or 'Error' in expr:
            return 'LOG.error("{}", ' + expr + ');'
        # For other multi-line expressions, try to parameterize
        return 'LOG.debug("{}", ' + expr + ');'
    content = pattern9.sub(replace9, content)
    
    # Pattern 10: Multi-line with method calls and concatenation (like ParameterHelper.getParameters)
    pattern10 = re.compile(
        r'Environment\.sysOut\(\s*\n\s*([^;]+\([^)]*\))\s*\n\s*\);',
        re.MULTILINE | re.DOTALL
    )
    def replace10(match):
        expr = match.group(1).strip()
        return 'LOG.debug("{}", ' + expr + ');'
    content = pattern10.sub(replace10, content)
    
    # Pattern 11: Multi-line concatenation with ERROR + method + string
    pattern11 = re.compile(
        r'Environment\.sysOut\(\s*\n\s*ERROR\s*\+\s*([^)]+)\s*\+\s*":([^"]+)"\s*\n\s*\);',
        re.MULTILINE | re.DOTALL
    )
    def replace11(match):
        method_expr = match.group(1).strip()
        message = match.group(2).strip()
        return 'LOG.error("{}:{}", ERROR, ' + method_expr + ', "' + message + '");'
    content = pattern11.sub(replace11, content)
    
    # Pattern 12: Single-line ERROR + method + string
    pattern12 = re.compile(
        r'Environment\.sysOut\(ERROR\s*\+\s*([^)]+)\s*\+\s*":([^"]+)"\);',
        re.MULTILINE
    )
    def replace12(match):
        method_expr = match.group(1).strip()
        message = match.group(2).strip()
        return 'LOG.error("{}:{}", ERROR, ' + method_expr + ', "' + message + '");'
    content = pattern12.sub(replace12, content)
    
    return content

def process_file(file_path, include_comments=False):
    """Process a single Java file."""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Check if file has Environment.sysOut, System.out.println, or System.err.println calls
        if include_comments:
            # Include commented-out calls
            lines_with_calls = [line for line in content.split('\n') 
                               if ('Environment.sysOut' in line or 'MicrosoftEnvironment.sysOut' in line or 
                                   'VivitEnvironment.sysOut' in line or 'System.out.println' in line or 
                                   'System.err.println' in line)]
        else:
            # Exclude commented-out calls
            lines_with_calls = [line for line in content.split('\n') 
                               if (('Environment.sysOut' in line or 'MicrosoftEnvironment.sysOut' in line or 
                                   'VivitEnvironment.sysOut' in line or 'System.out.println' in line or 
                                   'System.err.println' in line) 
                               and not line.strip().startswith('//'))]
        if not lines_with_calls:
            return False
        
        # Extract class or interface name
        class_match = re.search(r'public\s+(?:static\s+)?(?:final\s+)?class\s+(\w+)', content)
        interface_match = re.search(r'public\s+interface\s+(\w+)', content)
        
        if not class_match and not interface_match:
            return False
        
        class_name = class_match.group(1) if class_match else interface_match.group(1)
        is_interface = interface_match is not None
        original_content = content
        
        # Add imports and LOG field (skip for interfaces - they'll need special handling)
        if not is_interface:
            content = add_log_imports_and_field(content, class_name)
        else:
            # For interfaces, add imports but use a different approach for LOG
            # We'll use a helper class or keep Environment.sysOut for now
            # Actually, let's add a public static final Logger for interfaces
            has_log_manager = 'import org.apache.logging.log4j.LogManager' in content
            has_logger = 'import org.apache.logging.log4j.Logger' in content
            has_log_field = f'Logger LOG = LogManager.getLogger({class_name}.class)' in content
            
            if not has_log_manager or not has_logger:
                import_pattern = r'(package\s+[\w.]+;\s*\n)'
                match = re.search(import_pattern, content)
                if match:
                    insert_pos = match.end()
                    imports_to_add = []
                    if not has_log_manager:
                        imports_to_add.append('import org.apache.logging.log4j.LogManager;')
                    if not has_logger:
                        imports_to_add.append('import org.apache.logging.log4j.Logger;')
                    
                    if imports_to_add:
                        content = content[:insert_pos] + '\n'.join(imports_to_add) + '\n' + content[insert_pos:]
            
            if not has_log_field:
                interface_pattern = rf'public\s+interface\s+{class_name}\s*(?:extends\s+[\w\s,]+)?\s*\{{'
                match = re.search(interface_pattern, content)
                if match:
                    insert_pos = match.end()
                    # Interfaces can have public static final fields
                    log_field = f'\n  public static final Logger LOG = LogManager.getLogger({class_name}.class);\n  '
                    content = content[:insert_pos] + log_field + content[insert_pos:]
        
        # Migrate Environment.sysOut calls (including commented-out if requested)
        content = migrate_environment_sysout(content, include_comments)
        
        # Only write if content changed
        if content != original_content:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            return True
        
        return False
    except Exception as e:
        print(f"Error processing {file_path}: {e}", file=sys.stderr)
        import traceback
        traceback.print_exc()
        return False

def main():
    """Main function to process all remaining files."""
    import argparse
    parser = argparse.ArgumentParser(description='Migrate Environment.sysOut to Log4j 2.x')
    parser.add_argument('--include-comments', action='store_true', 
                       help='Also migrate commented-out Environment.sysOut calls')
    args = parser.parse_args()
    
    # Get the project root (parent of scripts directory)
    script_dir = Path(__file__).parent
    project_root = script_dir.parent
    base_dir = project_root / 'src/test/java/com/cjs/qa'
    
    # Find all Java files with Environment.sysOut
    all_files = []
    for pattern in ['**/*.java']:
        all_files.extend(base_dir.rglob(pattern))
    
    migrated_count = 0
    for file_path in sorted(all_files):
        if process_file(file_path, include_comments=args.include_comments):
            migrated_count += 1
            print(f"Migrated: {file_path}")
    
    print(f"\nTotal files migrated: {migrated_count}")

if __name__ == '__main__':
    main()
