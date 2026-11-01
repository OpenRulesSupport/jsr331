#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

export CLASS_NAME=org.jcp.jsr331.linear.samples.GroupSizing
# export SOLVER=Constrainer
# export SOLVER=ojAlgo
export SOLVER=SSC
sh ./run.sh
