#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${PROJECT_ROOT}"
mkdir -p java_manager_app/build

javac \
  -cp "java_manager_app/lib/sqlite-jdbc.jar" \
  -d java_manager_app/build \
  $(find java_manager_app/src -name '*.java')
