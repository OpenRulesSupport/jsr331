set CLASS_NAME=org.jcp.jsr331.samples.CarSequence
@echo off
cd %~dp0
rem set SOLVER=Constrainer
rem set SOLVER=Choco
set SOLVER=MaxiCP
rem set SOLVER=JSetL
rem set SOLVER=Sugar
call .\run
pause