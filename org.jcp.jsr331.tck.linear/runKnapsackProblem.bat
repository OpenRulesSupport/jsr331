set CLASS_NAME=org.jcp.jsr331.linear.samples.KnapsackProblem
rem set SOLVER=Constrainer
rem set SOLVER=CLP
rem set SOLVER=Scip
set SOLVER=GLPK
cd %~dp0
call run
pause