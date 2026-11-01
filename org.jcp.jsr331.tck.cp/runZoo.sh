#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

export CLASS_NAME=org.jcp.jsr331.samples.Zoo
# export SOLVER=Constrainer
# export SOLVER=CLP
# export SOLVER=Choco
# export SOLVER=JSetL
# export SOLVER=sugar
export SOLVER=MaxiCP
sh ./run.sh
