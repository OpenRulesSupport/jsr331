#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

PROGRAM=org.jcp.jsr331.samples.TestReversibleAction
printf 'Run %s ...\n' "$PROGRAM"
printf 'Start Search with Try-Fail Animation\n'

LOGLIBS="./lib/commons-logging-1.1.jar:./lib/commons-logging-api-1.1.jar:./lib/log4j-1.2.15.jar"
LIBS="bin:./lib/jsr331.jar:./lib/jsr331.constrainer.jar:./lib/constrainer.jar:$LOGLIBS"
java -Xmx512m -classpath "$LIBS" "$PROGRAM"
printf 'done\n'
