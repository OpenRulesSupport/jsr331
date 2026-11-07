set CLASS_NAME=org.jcp.jsr331.linear.samples.InsideOutsideProductionInteger

rem set SOLVER=SCIP
rem set SOLVER=GLPK
set SOLVER=CLP
rem set SOLVER=COIN
rem set SOLVER=LPSOLVE
rem set SOLVER=SSC

@echo off
cd %~dp0
call .\run
pause