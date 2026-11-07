set CLASS_NAME=org.jcp.jsr331.samples.CarSequence
@echo off
cd %~dp0
rem set SOLVER=CONSTRAINER
rem set SOLVER=CHOCO
set SOLVER=MAXICP
rem set SOLVER=JSETL
rem set SOLVER=SUGAR
call .\run
pause