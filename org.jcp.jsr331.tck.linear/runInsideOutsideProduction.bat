set CLASS_NAME=org.jcp.jsr331.linear.samples.InsideOutsideProduction

rem set SOLVER=Scip
rem set SOLVER=GLPK
rem set SOLVER=CLP
rem set SOLVER=Coin
rem set SOLVER=lpsolve
set SOLVER=SSC
rem set SOLVER=ojAlgo
@echo off
cd %~dp0
call .\run
pause