#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${PROJECT_ROOT}"
java -cp "java_manager_app/build:java_manager_app/lib/sqlite-jdbc.jar" com.revature.expensemanager.ManagerApp
