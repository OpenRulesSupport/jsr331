set CLASS_NAME=org.jcp.jsr331.samples.Warehouse
set SOLVER=CONSTRAINER
set SOLVER=MAXICP
set SOLVER=CPOPTIMIZER
echo SOLVER=%SOLVER%
cd %~dp0
call run
pause