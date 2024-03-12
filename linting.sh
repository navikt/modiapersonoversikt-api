#! /usr/bin/env bash
if [[ -z "$CI" ]];then
  ktlint '*/src/main/**/*.kt' '*/src/test/**/*.kt' --reporter=plain?group_by_file --color $@
else
  ktlint '*/src/main/**/*.kt' '*/src/test/**/*.kt' --reporter=plain?group_by_file
fi
