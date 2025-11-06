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
POSTGRES_JAR="${POSTGRES_JDBC_JAR:-}"
if [[ -z "$POSTGRES_JAR" && -d "$SCRIPT_DIR/lib" ]]; then
  POSTGRES_JAR="$(find "$SCRIPT_DIR/lib" -maxdepth 1 -name '*.jar' | head -n 1)"
fi

CLASSPATH="$BUILD_DIR"
if [[ -n "$POSTGRES_JAR" ]]; then
  CLASSPATH="$CLASSPATH:$POSTGRES_JAR"
else
  echo "Aviso: no se encontró el controlador JDBC de PostgreSQL. Coloca el .jar en backend/lib o exporta POSTGRES_JDBC_JAR." >&2
fi

java -cp "$CLASSPATH" com.tuticuenta.server.AppServer
