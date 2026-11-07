#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

export CLASS_NAME=org.jcp.jsr331.linear.samples.InsideOutsideProduction

# export SOLVER=SCIP
# export SOLVER=GLPK
# export SOLVER=CLP
# export SOLVER=COIN
# export SOLVER=LPSOLVE
export SOLVER=SSC
# export SOLVER=OJALGO
sh ./run.sh
