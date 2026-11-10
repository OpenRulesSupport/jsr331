set CLASS_NAME=org.jcp.jsr331.linear.samples.InsideOutsideProduction

rem set SOLVER=SCIP
rem set SOLVER=GLPK
rem set SOLVER=CLP
rem set SOLVER=COIN
rem set SOLVER=LPSOLVE
rem set SOLVER=SSC
rem set SOLVER=OJALGO
set SOLVER=HIGHS
@echo off
cd %~dp0
call .\run
pause