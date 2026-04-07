#!/usr/bin/env bash
# CivicTrack JavaFX Runner for Linux
# Always runs from the project root (folder that contains pom.xml),
# even if you invoke this script from another directory.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$PROJECT_ROOT"

echo "Project root: $PROJECT_ROOT"
if [[ ! -f "$PROJECT_ROOT/pom.xml" ]]; then
  echo "ERROR: pom.xml not found. Expected project root at: $PROJECT_ROOT"
  exit 1
fi
if [[ ! -f "$PROJECT_ROOT/schema.sql" ]]; then
  echo "WARNING: schema.sql not found in project root. Load DB with:"
  echo "  mysql -u root -p < /full/path/to/oop-java-project/schema.sql"
fi

echo "Starting CivicTrack JavaFX (ensure MySQL is running and schema is loaded)..."
mvn clean compile javafx:run

echo "JavaFX application finished."
