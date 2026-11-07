#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

export CLASS_NAME=org.jcp.jsr331.samples.Zebra
# export SOLVER=CONSTRAINER
# export SOLVER=CHOCO
# export SOLVER=JSETL
# export SOLVER=sugar
# export SOLVER=MAXICP
export SOLVER=JaCoP
sh ./run.sh
