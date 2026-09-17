#!/bin/sh
set -eu
BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
MVN_VERSION=3.9.11
MVN_HOME="$BASE_DIR/.mvn/apache-maven-$MVN_VERSION"
if [ ! -x "$MVN_HOME/bin/mvn" ]; then
  ARCHIVE="$BASE_DIR/.mvn/apache-maven-$MVN_VERSION-bin.tar.gz"
  mkdir -p "$BASE_DIR/.mvn"
  curl -fsSL "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MVN_VERSION/apache-maven-$MVN_VERSION-bin.tar.gz" -o "$ARCHIVE"
  tar -xzf "$ARCHIVE" -C "$BASE_DIR/.mvn"
  rm -f "$ARCHIVE"
fi
exec "$MVN_HOME/bin/mvn" "$@"
