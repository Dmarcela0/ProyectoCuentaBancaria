#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BUILD_DIR="$SCRIPT_DIR/out"
SOURCES_LIST="$(mktemp)"
trap 'rm -f "$SOURCES_LIST"' EXIT
find "$SCRIPT_DIR/src/main/java/com/tuticuenta" -name "*.java" > "$SOURCES_LIST"
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"
javac --release 17 -d "$BUILD_DIR" @"$SOURCES_LIST"
java -cp "$BUILD_DIR" com.tuticuenta.server.AppServer
