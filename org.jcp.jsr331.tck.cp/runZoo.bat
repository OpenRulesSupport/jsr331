set CLASS_NAME=org.jcp.jsr331.samples.Zoo
rem set SOLVER=Constrainer
rem set SOLVER=CLP
rem set SOLVER=Choco
rem set SOLVER=JSetL
rem set SOLVER=sugar
set SOLVER=MaxiCP
@echo off
cd %~dp0
call .\run
pause