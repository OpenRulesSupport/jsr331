#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

export CLASS_NAME=org.jcp.jsr331.scheduler.samples.ScheduleAlternativeResourcesOptimal
export SOLVER=CONSTRAINER
# export SOLVER=SUGAR
# export SOLVER=JSETL
sh ./run.sh
