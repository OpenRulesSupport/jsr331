set CLASS_NAME=org.jcp.jsr331.linear.samples.InsideOutsideProduction

rem set SOLVER=Scip
set SOLVER=GLPK
rem set SOLVER=Coin
rem set SOLVER=lpsolve
rem set SOLVER=SSC

@echo off
cd %~dp0
call .\run
pause