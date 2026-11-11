set CLASS_NAME=org.jcp.jsr331.samples.WhoKilledAgatha
rem set SOLVER=CONSTRAINER
rem set SOLVER=CLP
rem set SOLVER=CHOCO
rem set SOLVER=JSETL
rem set SOLVER=sugar
set SOLVER=CPOPTIMIZER
echo SOLVER=%SOLVER%
@echo off
cd %~dp0
call .\run
pause