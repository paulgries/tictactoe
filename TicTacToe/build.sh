#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"

rm -rf out/main
mkdir -p out/main

MAIN_SOURCES=$(find src/main/java -name "*.java")
if [[ -n "$MAIN_SOURCES" ]]; then
  javac -d out/main --release 17 $MAIN_SOURCES
fi
