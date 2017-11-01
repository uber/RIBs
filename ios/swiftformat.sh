#! /usr/bin/env bash

if ! which swiftformat &> /dev/null; then
  printf "\e[1;31mYou don't have SwiftFormat installed.  Please install swift format by visiting https://github.com/nicklockwood/SwiftFormat.\e[0m\n"
  exit 1
fi

HEADER="
//
//  Copyright (c) {year}. Uber Technologies
//
//  Licensed under the Apache License, Version 2.0 (the \"License\");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an \"AS IS\" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.\n
"

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
  --exclude tooling,Pods,tutorials/tutorial1/Pods,tutorials/tutorial2/Pods,tutorials/tutorial3/Pods,tutorials/tutorial3-rib-di-and-communication-finished/Pods,tutorials/tutorial4/Pods \
  .)

if [ "$OUTPUT" ]; then
  echo "$OUTPUT" >&2
  exit 1
fi
