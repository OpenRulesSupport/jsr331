set CLASS_NAME=org.jcp.jsr331.samples.Zoo
rem set SOLVER=Constrainer
rem set SOLVER=Choco
rem set SOLVER=JSetL
set SOLVER=sugar
@echo off
cd %~dp0
call .\run
pause