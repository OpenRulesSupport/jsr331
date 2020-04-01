set CLASS_NAME=org.jcp.jsr331.linear.samples.Knapsack
rem set SOLVER=Scip
rem set SOLVER=GLPK
set SOLVER=Coin
@echo off
cd %~dp0
call .\run
pause