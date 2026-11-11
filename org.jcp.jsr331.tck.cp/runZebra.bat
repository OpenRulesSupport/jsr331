set CLASS_NAME=org.jcp.jsr331.samples.Zebra

rem set SOLVER=CONSTRAINER
rem set SOLVER=CHOCO
rem set SOLVER=JSETL
rem set SOLVER=sugar
rem set SOLVER=MAXICP
rem set SOLVER=JaCoP
rem set SOLVER=ORTOOLS
set SOLVER=CPOPTIMIZER
echo SOLVER=%SOLVER%

@echo off
cd %~dp0
call .\run
pause