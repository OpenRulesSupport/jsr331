#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

export CLASS_NAME=cloud.balancing.CloudBalancer
export SOLVER=Constrainer
# export SOLVER=Constrainer
export SOLVER=Choco
# export SOLVER=JSetL
sh ./run.sh
