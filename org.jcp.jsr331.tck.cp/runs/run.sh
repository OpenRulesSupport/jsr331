#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

: "${CLASS_NAME:=org.jcp.jsr331.samples.SendMoreMoney}"
: "${SOLVER:=Constrainer}"

mvn -f ../pom.xml -P "$SOLVER" compile exec:java \
  -Dexec.mainClass="$CLASS_NAME" -e
