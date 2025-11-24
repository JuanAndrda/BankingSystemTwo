#!/usr/bin/env python3
import re
import sys

def simplify_java_file(input_file, output_file):
    with open(input_file, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    result = []
    in_javadoc = False
    skip_next_blank = False

    for i, line in enumerate(lines):
        stripped = line.strip()

        # Start of JavaDoc comment
        if stripped.startswith('/**'):
            in_javadoc = True
            continue

        # End of JavaDoc comment
        if in_javadoc and '*/' in stripped:
            in_javadoc = False
            skip_next_blank = True
            continue

        # Inside JavaDoc comment
        if in_javadoc:
            continue

        # Skip blank lines after JavaDoc
        if skip_next_blank and stripped == '':
            skip_next_blank = False
            continue

        skip_next_blank = False

        # Remove section comments like // ===== SECTION =====
        if re.match(r'^\s*//\s*={3,}', stripped):
            continue

        # Remove excessive inline comments but keep essential ones
        # Remove comments that start with "Method calls:", "Calls", "Note:", "Why:", "Problem:", etc.
        if re.match(r'^\s*//(Method calls:|Calls|Note:|Why:|Problem:|NEW APPROACH:|OLD APPROACH:|FIXED:|Uses|This demonstrates|Benefits:|Algorithm:|Time Complexity:|Space Complexity:|Pattern:|Demonstrates:)', stripped):
            continue

        # Remove inline comments that explain obvious things
        line = re.sub(r'\s*//\s*(Format:|Extract|Track|Record|Mark|Add to|Shift|Continue|Found|Insert|Safe|Return|Get|Set|Create|Update|Delete|Find|Check|Display|Process|Handle|Show|Print)', '', line)

        # Keep the line
        result.append(line)

    # Write simplified file
    with open(output_file, 'w', encoding='utf-8') as f:
        f.writelines(result)

    print(f"Simplified {input_file} -> {output_file}")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python simplify_comments.py input.java output.java")
        sys.exit(1)

    simplify_java_file(sys.argv[1], sys.argv[2])
