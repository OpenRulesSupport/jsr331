#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

mvn install:install-file -DgroupId=com.javasolver -DartifactId=jsr331-sugar-213 -Dversion=2.1.3 -Dpackaging=jar -Dfile=sugar-v2-1-3.jar
mvn install:install-file -DgroupId=com.javasolver -DartifactId=jsr331-sat4j -Dversion=2.1.3 -Dpackaging=jar -Dfile=org.sat4j.core.jar
