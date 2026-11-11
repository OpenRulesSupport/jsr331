set CLASS_NAME=org.jcp.jsr331.linear.samples.InsideOutsideProduction

rem set SOLVER=SCIP
rem set SOLVER=GLPK
set SOLVER=ORTOOLS-LP
rem set SOLVER=CLP
rem set SOLVER=COIN
rem set SOLVER=LPSOLVE
rem set SOLVER=SSC
rem set SOLVER=OJALGO
rem set SOLVER=HIGHS
rem set SOLVER=CPLEX
rem set SOLVER=GUROBI
@echo off
cd %~dp0
call .\run
pause