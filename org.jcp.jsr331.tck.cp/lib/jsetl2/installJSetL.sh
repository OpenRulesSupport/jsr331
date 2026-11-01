#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

mvn install:install-file -DgroupId=com.javasolver -DartifactId=jsr331-jsetl2 -Dversion=2.3.0 -Dpackaging=jar -Dfile=jsetl.jar
