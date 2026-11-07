set CLASS_NAME=org.jcp.jsr331.samples.Bins
rem set SOLVER=CONSTRAINER
rem set SOLVER=CHOCO
set SOLVER=MAXICP
rem set SOLVER=JSETL
rem set SOLVER=SUGAR
@echo off
cd %~dp0
call .\run
pause