#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

sh ./runLinear.sh org.jcp.jsr331.linear.samples.GroupSizing
