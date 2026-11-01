#!/bin/sh

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR" || exit 1

PROGRAM=${1:-org.jcp.jsr331.samples.SendMoreMoney}
printf 'Run %s ...\n' "$PROGRAM"

LIB=../org.jcp.jsr331.tck/lib
LOGLIBS="$LIB/logging/commons-logging-1.1.jar:$LIB/logging/commons-logging-api-1.1.jar:$LIB/logging/log4j-1.2.15.jar"

# SOLVER="$LIB/constrainer/jsr331.constrainer.jar:$LIB/constrainer/constrainer.light.jar"
# SOLVER="$LIB/choco/jsr331.choco.jar:$LIB/choco/choco-solver-2.1.5-20120603-with-sources.jar"
# SOLVER="$LIB/jacop/jsr331.jacop.jar:$LIB/jacop/jacop-3.0.jar"
# SOLVER="$LIB/jsetl/jsr331.jsetl.jar:$LIB/jsetl/jsetl.jar"

LPCOMMON="$LIB/linear/jsr331.linear.jar"
LINEAR="$LPCOMMON:$LIB/linear"
OPTIONS=${OPTIONS:-}
# SOLVER="$LINEAR/jsr331.glpk.jar"
# SOLVER="$LIB/linear/jsr331.cplex.jar:$LPCOMMON"
# SOLVER="$LIB/linear/jsr331.lpsolve.jar:$LPCOMMON"
SOLVER="$LIB/linear/jsr331.coin.jar:$LPCOMMON"
# SOLVER="$LIB/linear/ojalgo.jar:$LPCOMMON"

# OPTIONS='-DLP_SOLVER_OPTIONS=set limits time 12000 set limits stallnodes 1000 set limits gap 1.05 set heuristics emphasis aggressive'
# SOLVER="$LINEAR/jsr331.scip.jar"

# OPTIONS='-DLP_SOLVER_OPTIONS=Threads=1 Cuts=2 timelimit=15000'
# SOLVER="$LIB/linear/jsr331.gurobi.jar:$LPCOMMON"

LIBS="./bin:$LIB/jsr331.jar:$SOLVER:$LOGLIBS"
if [ -n "$OPTIONS" ]; then
  java -Xms1024m -Xmx1024m "$OPTIONS" -classpath "$LIBS" "$PROGRAM"
else
  java -Xms1024m -Xmx1024m -classpath "$LIBS" "$PROGRAM"
fi
printf 'done\n'
