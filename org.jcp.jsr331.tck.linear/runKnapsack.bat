set CLASS_NAME=org.jcp.jsr331.linear.samples.Knapsack

rem set SOLVER=SCIP
rem set SOLVER=GLPK
rem set SOLVER=COIN
rem set SOLVER=CLP
rem set SOLVER=LPSOLVE
rem set SOLVER=SSC
rem set SOLVER=OJALGO
rem set SOLVER=SSC
set SOLVER=CPLEX
echo SOLVER=%SOLVER%

@echo off
cd %~dp0
call .\run
pause