#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

if [ "$#" -gt 0 ]; then
  PROGRAM="org.jcp.jsr331.samples.$1"
else
  PROGRAM=org.jcp.jsr331.linear.samples.Knapsack
fi
printf 'Run %s ...\n' "$PROGRAM"

LIB=../org.jcp.jsr331.tck/lib
LOGLIBS="$LIB/logging/commons-logging-1.1.jar:$LIB/logging/commons-logging-api-1.1.jar:$LIB/logging/log4j-1.2.15.jar"
LPCOMMON=../org.jcp.jsr331.linear/lib/jsr331.linear.jar
OPTIONS=${OPTIONS:-}

# SOLVER="../org.jcp.jsr331.tck/lib/constrainer/jsr331.constrainer.jar:../org.jcp.jsr331.tck/lib/constrainer/constrainer.light.jar"
# OPTIONS='-DLP_SOLVER_OPTIONS=set limits time 12000 set limits stallnodes 1000 set limits gap 1.05 set heuristics emphasis aggressive'
# SOLVER="../org.jcp.jsr331.linear.scip/lib/jsr331.scip.jar:$LPCOMMON"
# OPTIONS='-DLP_SOLVER_OPTIONS=Threads=1 Cuts=2 timelimit=15000'
# SOLVER="../org.jcp.jsr331.linear.gurobi/lib/jsr331.gurobi.jar:$LPCOMMON"
# SOLVER="../org.jcp.jsr331.linear.glpk/lib/jsr331.glpk.jar:$LPCOMMON"
# SOLVER="../org.jcp.jsr331.linear.cplex/lib/jsr331.cplex.jar:$LPCOMMON"
# SOLVER="../org.jcp.jsr331.linear.lpsolve/lib/jsr331.lpsolve.jar:$LPCOMMON"
SOLVER="../org.jcp.jsr331.linear.coin/lib/jsr331.coin.jar:$LPCOMMON"
# SOLVER="../org.jcp.jsr331.linear.ojAlgo/lib/ojalgo.jar:$LPCOMMON"

LIBS="./bin:$LIB/jsr331.jar:$SOLVER:$LOGLIBS"
if [ -n "$OPTIONS" ]; then
  java -Xms1024m -Xmx1024m "$OPTIONS" -classpath "$LIBS" "$PROGRAM"
else
  java -Xms1024m -Xmx1024m -classpath "$LIBS" "$PROGRAM"
fi
printf 'done\n'
