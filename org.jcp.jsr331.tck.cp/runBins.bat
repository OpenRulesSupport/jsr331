set CLASS_NAME=org.jcp.jsr331.samples.Bins
rem set SOLVER=Constrainer
rem set SOLVER=Choco
set SOLVER=JSetL
rem set SOLVER=Sugar
@echo off
cd %~dp0
call .\run
pause