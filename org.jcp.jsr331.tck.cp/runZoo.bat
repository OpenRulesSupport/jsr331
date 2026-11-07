set CLASS_NAME=org.jcp.jsr331.samples.Zoo
rem set SOLVER=CONSTRAINER
rem set SOLVER=CLP
rem set SOLVER=CHOCO
rem set SOLVER=JSETL
rem set SOLVER=sugar
set SOLVER=MAXICP
@echo off
cd %~dp0
call .\run
pause