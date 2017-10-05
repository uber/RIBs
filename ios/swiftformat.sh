#! /usr/bin/env bash

if ! which swiftformat &> /dev/null; then
  printf "\e[1;31mYou don't have SwiftFormat installed.  Please install swift format by visiting https://github.com/nicklockwood/SwiftFormat.\e[0m\n"
  exit 1
fi

HEADER="//\n//  Copyright © {year} Uber Technologies, Inc. All rights reserved.\n//"

TMPFILE=$(mktemp)
OUTPUT=$(swiftformat \
  --cache ignore \
  --indent 4 \
  --allman false \
  --wraparguments afterfirst \
  --removelines disabled \
  --wrapelements beforefirst \
  --exponentcase uppercase \
  --insertlines disabled \
  --binarygrouping none \
  --empty tuples \
  --ranges nospace \
  --trimwhitespace always \
  --hexliteralcase lowercase \
  --linebreaks lf \
  --decimalgrouping none \
  --commas inline \
  --comments ignore \
  --octalgrouping none \
  --hexgrouping none \
  --semicolons inline \
  --header "$HEADER" \
  --disable redundantSelf,unusedArguments,hoistPatternLet,redundantBackticks,redundantReturn,linebreakAtEndOfFile \
  --output "$TMPFILE" \
  **/*.swift)

if [ "$OUTPUT" ]; then
  echo "$OUTPUT" >&2
  exit 1
fi

# output to a temp file and cat it as swiftformat has no option to output to stdout
FORMATTED=$(cat "$TMPFILE")
rm "$TMPFILE"
echo "$FORMATTED"
