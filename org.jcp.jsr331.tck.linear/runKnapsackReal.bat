set CLASS_NAME=org.jcp.jsr331.linear.samples.KnapsackReal

rem set SOLVER=Scip
rem set SOLVER=GLPK
rem set SOLVER=CLP
set SOLVER=Coin
rem set SOLVER=lpsolve

@echo off
cd %~dp0
call .\run
pause