#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

export CLASS_NAME=org.jcp.jsr331.samples.CarSequence
# export SOLVER=Constrainer
# export SOLVER=Choco
export SOLVER=MaxiCP
# export SOLVER=JSetL
# export SOLVER=Sugar
sh ./run.sh
