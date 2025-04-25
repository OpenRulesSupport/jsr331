set CLASS_NAME=org.jcp.jsr331.linear.samples.Knapsack

rem set SOLVER=Scip
rem set SOLVER=GLPK
rem set SOLVER=Coin
rem set SOLVER=CLP
rem set SOLVER=lpsolve
set SOLVER=SSC

@echo off
cd %~dp0
call .\run
pause