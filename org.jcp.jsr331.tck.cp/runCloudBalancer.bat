set CLASS_NAME=cloud.balancing.CloudBalancer
set SOLVER=CONSTRAINER
cd %~dp0
rem set SOLVER=CONSTRAINER
set SOLVER=CHOCO
rem set SOLVER=JSETL
call run
pause