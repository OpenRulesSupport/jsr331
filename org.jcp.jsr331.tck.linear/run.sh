#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

: "${CLASS_NAME:=org.jcp.jsr331.linear.samples.Knapsack}"
: "${SOLVER:=Coin}"

mvn -P "$SOLVER" compile exec:java \
  -Dexec.mainClass="$CLASS_NAME" \
  -Dexec.classpathScope=test -q -e
