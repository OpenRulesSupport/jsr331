#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

if mvn install:install-file \
  -Dfile=scip-java-1.0.0-full.jar \
  -DpomFile=pom.xml; then
  printf '\nscip-java    INSTALLATION COMPLETED\n'
else
  exit $?
fi
