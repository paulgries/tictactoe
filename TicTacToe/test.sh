#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"

./build.sh

rm -rf out/test
mkdir -p out/test
javac -d out/test --release 17 -cp "out/main:lib/*" $(find src/test/java -name "*.java")

SELECTOR=(--scan-class-path)
if [[ $# -gt 0 ]]; then
  SELECTOR=("$@")
fi

java -jar lib/junit-platform-console-standalone-1.14.4.jar execute \
  --class-path "out/main:out/test:lib/assertj-core-3.27.7.jar" \
  --include-classname=".*Test" \
  --details=tree \
  --fail-if-no-tests \
  "${SELECTOR[@]}"
