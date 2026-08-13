#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"

./build.sh
java -cp out/main com.tictactoe.infra.Main
