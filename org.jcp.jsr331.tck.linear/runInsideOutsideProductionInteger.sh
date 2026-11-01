#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

export CLASS_NAME=org.jcp.jsr331.linear.samples.InsideOutsideProductionInteger

# export SOLVER=Scip
# export SOLVER=GLPK
export SOLVER=CLP
# export SOLVER=Coin
# export SOLVER=lpsolve
# export SOLVER=SSC

sh ./run.sh
