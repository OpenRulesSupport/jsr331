#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

if [ "$#" -gt 0 ]; then
  PROGRAM="org.jcp.jsr331.samples.$1"
else
  PROGRAM=org.jcp.jsr331.samples.SendMoreMoney
fi
printf 'Run %s ...\n' "$PROGRAM"

LOGLIBS="./lib/logging/commons-logging-1.1.jar:./lib/logging/commons-logging-api-1.1.jar:./lib/logging/log4j-1.2.15.jar"

SOLVER="./lib/constrainer/jsr331.constrainer.jar:./lib/constrainer/constrainer.light.jar"
# SOLVER="./lib/choco/jsr331.choco.jar:./lib/choco/choco-solver-2.1.1-20110622-with-sources.jar"
# SOLVER="./lib/jacop/jsr331.jacop.jar:./lib/jacop/jacop-3.0.jar"

LIBS="./bin:./lib/jsr331.jar:$SOLVER:$LOGLIBS"
java -Xmx512m -classpath "$LIBS" "$PROGRAM"
printf 'done\n'
