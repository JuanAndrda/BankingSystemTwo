#!/usr/bin/env python3
import re
import sys

def simplify_java_file(input_file, output_file):
    with open(input_file, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    result = []
    in_javadoc = False
    in_block_comment = False
    skip_next_blank = False

    for i, line in enumerate(lines):
        stripped = line.strip()

        # Start of JavaDoc comment /**
        if stripped.startswith('/**'):
            in_javadoc = True
            continue

        # End of JavaDoc or block comment */
        if (in_javadoc or in_block_comment) and '*/' in stripped:
            in_javadoc = False
            in_block_comment = False
            skip_next_blank = True
            continue

        # Inside JavaDoc or block comment
        if in_javadoc or in_block_comment:
            continue

        # Skip blank lines immediately after JavaDoc
        if skip_next_blank and stripped == '':
            skip_next_blank = False
            continue

        skip_next_blank = False

        # Remove section divider comments like // ===== SECTION =====
        if re.match(r'^\s*//\s*={3,}.*={3,}\s*$', stripped):
            continue

        # Keep the line
        result.append(line)

    # Write simplified file
    with open(output_file, 'w', encoding='utf-8') as f:
        f.writelines(result)

    print(f"Simplified {input_file} -> {output_file}")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python simplify_better.py input.java output.java")
        sys.exit(1)

    simplify_java_file(sys.argv[1], sys.argv[2])
