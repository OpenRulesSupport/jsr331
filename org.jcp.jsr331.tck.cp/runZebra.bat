set CLASS_NAME=org.jcp.jsr331.samples.Zebra
rem set SOLVER=Constrainer
rem set SOLVER=Choco
rem set SOLVER=JSetL
rem set SOLVER=sugar
rem set SOLVER=MaxiCP
set SOLVER=JaCoP
@echo off
cd %~dp0
call .\run
pause