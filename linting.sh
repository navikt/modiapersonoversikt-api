#! /usr/bin/env bash
if [[ -z "$CI" ]];then
  ktlint '*/src/main/**/*.kt' '*/src/test/**/*.kt' --reporter=plain?group_by_file --color --experimental $@
else
  ktlint '*/src/main/**/*.kt' '*/src/test/**/*.kt' --reporter=plain?group_by_file --experimental
fi
