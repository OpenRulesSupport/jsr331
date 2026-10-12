set CLASS_NAME=cloud.balancing.CloudBalancer
set SOLVER=Constrainer
cd %~dp0
rem set SOLVER=Constrainer
set SOLVER=Choco
rem set SOLVER=JSetL
call run
pause