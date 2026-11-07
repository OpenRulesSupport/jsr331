set CLASS_NAME=org.jcp.jsr331.scheduler.samples.ScheduleAlternativeResourcesOptimal
rem set SOLVER=CONSTRAINER
rem set SOLVER=SUGAR
rem set SOLVER=JSETL
set SOLVER=MAXICP
cd %~dp0
call run
pause