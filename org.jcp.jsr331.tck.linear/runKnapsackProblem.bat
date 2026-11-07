set CLASS_NAME=org.jcp.jsr331.linear.samples.KnapsackProblem
rem set SOLVER=CLP
set SOLVER=SCIP
set SOLVER=GLPK
cd %~dp0
call run
pause