set CLASS_NAME=org.jcp.jsr331.linear.samples.KnapsackReal

set SOLVER=SCIP
rem set SOLVER=GLPK
rem set SOLVER=CLP
rem set SOLVER=COIN
rem set SOLVER=LPSOLVE

@echo off
cd %~dp0
call .\run
pause