#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

export CLASS_NAME=cloud.balancing.CloudBalancer
export SOLVER=CONSTRAINER
# export SOLVER=CONSTRAINER
export SOLVER=CHOCO
# export SOLVER=JSETL
sh ./run.sh
