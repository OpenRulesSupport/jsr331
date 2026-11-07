#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

export CLASS_NAME=org.jcp.jsr331.linear.samples.KnapsackProblem
export SOLVER=CLP
# export SOLVER=SCIP
# export SOLVER=GLPK
sh ./run.sh
