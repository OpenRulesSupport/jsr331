set CLASS_NAME=org.jcp.jsr331.linear.samples.Knapsack

rem set SOLVER=Scip
set SOLVER=GLPK
rem set SOLVER=Coin
rem set SOLVER=lpsolve

@echo off
cd %~dp0
call .\run
pause