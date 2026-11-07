#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

export CLASS_NAME=org.jcp.jsr331.linear.samples.Knapsack

# export SOLVER=SCIP
# export SOLVER=GLPK
# export SOLVER=COIN
# export SOLVER=CLP
# export SOLVER=LPSOLVE
# export SOLVER=SSC
# export SOLVER=OJALGO
export SOLVER=SSC
sh ./run.sh
