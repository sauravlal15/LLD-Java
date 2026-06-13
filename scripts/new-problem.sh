#!/usr/bin/env bash
# Scaffold a new isolated LLD problem subproject.
# Usage: ./scripts/new-problem.sh elevator-system

set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SLUG="${1:-}"

if [[ -z "$SLUG" ]]; then
  echo "Usage: $0 <problem-slug>" >&2
  echo "Example: $0 elevator-system" >&2
  exit 1
fi

if [[ ! "$SLUG" =~ ^[a-z][a-z0-9]*(-[a-z0-9]+)*$ ]]; then
  echo "Slug must be kebab-case (e.g. parking-lot, elevator-system)." >&2
  exit 1
fi

PROBLEM_DIR="$ROOT/$SLUG"
if [[ -e "$PROBLEM_DIR" ]]; then
  echo "Already exists: $PROBLEM_DIR" >&2
  exit 1
fi

# parking-lot -> parkinglot (valid Java package segment)
PACKAGE_SEGMENT="${SLUG//-/}"
PACKAGE_NAME="com.saurav.lld.${PACKAGE_SEGMENT}"
MAIN_CLASS="${PACKAGE_NAME}.Main"

# Title: parking-lot -> Parking Lot
PROBLEM_TITLE="$(echo "$SLUG" | sed -E 's/-/ /g' | awk '{for(i=1;i<=NF;i++) $i=toupper(substr($i,1,1)) tolower(substr($i,2));}1')"

TEMPLATE="$ROOT/templates/lld-problem"
mkdir -p "$PROBLEM_DIR/src/main/java/$(echo "$PACKAGE_NAME" | tr '.' '/')"

sed -e "s|{{MAIN_CLASS}}|${MAIN_CLASS}|g" \
  "$TEMPLATE/build.gradle.kts" > "$PROBLEM_DIR/build.gradle.kts"

sed -e "s|{{PROBLEM_SLUG}}|${SLUG}|g" \
    -e "s|{{PROBLEM_TITLE}}|${PROBLEM_TITLE}|g" \
    -e "s|{{PACKAGE_NAME}}|${PACKAGE_NAME}|g" \
  "$TEMPLATE/README.md" > "$PROBLEM_DIR/README.md"

sed -e "s|{{PROBLEM_SLUG}}|${SLUG}|g" \
    -e "s|{{PACKAGE_NAME}}|${PACKAGE_NAME}|g" \
  "$TEMPLATE/src/main/java/Main.java" > "$PROBLEM_DIR/src/main/java/$(echo "$PACKAGE_NAME" | tr '.' '/')/Main.java"

SETTINGS="$ROOT/settings.gradle.kts"
if grep -q "\"${SLUG}\"" "$SETTINGS"; then
  echo "settings.gradle.kts already includes ${SLUG}"
else
  awk -v slug="$SLUG" '
    /include\(/ { in_include = 1 }
    in_include && /^\)/ {
      print "    \"" slug "\","
      in_include = 0
    }
    { print }
  ' "$SETTINGS" > "${SETTINGS}.tmp"
  mv "${SETTINGS}.tmp" "$SETTINGS"
fi

chmod +x "$ROOT/scripts/new-problem.sh" 2>/dev/null || true

echo "Created: $SLUG"
echo "  Package: $PACKAGE_NAME"
echo "  Run:     ./gradlew :${SLUG}:run"
