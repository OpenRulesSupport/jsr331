@echo off
cd %~dp0
if not "%1" == "" goto defined
set PROGRAM=org.jcp.jsr331.samples.SendMoreMoney
goto run
:defined
set PROGRAM=%1
:run
echo Run %PROGRAM% ...
set LIB=../org.jcp.jsr331.tck/lib
set LOGLIBS=%LIB%/logging/commons-logging-1.1.jar;%LIB%/logging/commons-logging-api-1.1.jar;%LIB%/logging/log4j-1.2.15.jar

rem set SOLVER=%LIB%/constrainer/jsr331.constrainer.jar;%LIB%/constrainer/constrainer.light.jar
rem set SOLVER=%LIB%/choco/jsr331.choco.jar;%LIB%/choco/choco-solver-2.1.5-20120603-with-sources.jar
rem set SOLVER=%LIB%/jacop/jsr331.jacop.jar;%LIB%/jacop/jacop-3.0.jar
rem set SOLVER=%LIB%/jsetl/jsr331.jsetl.jar;%LIB%/jsetl/jsetl.jar

set LPCOMMON=%LIB%/linear/jsr331.linear.jar
set LINEAR=%LPCOMMON%;%LIB%/linear
set OPTIONS=
rem set SOLVER=%LINEAR%/jsr331.glpk.jar
rem set SOLVER=%LIB%/linear/jsr331.cplex.jar;%LPCOMMON%
rem set SOLVER=%LIB%/linear/jsr331.lpsolve.jar;%LPCOMMON%
set SOLVER=%LIB%/linear/jsr331.coin.jar;%LPCOMMON%
rem set SOLVER=%LIB%/linear/ojalgo.jar;%LPCOMMON%

rem set OPTIONS=-DLP_SOLVER_OPTIONS="set limits time 12000 set limits stallnodes 1000 set limits gap 1.05 set heuristics emphasis aggressive"
rem set SOLVER=%LINEAR%/jsr331.scip.jar

rem set OPTIONS=-DLP_SOLVER_OPTIONS="Threads=1 Cuts=2 timelimit=15000"
rem set SOLVER=%LIB%/linear/jsr331.gurobi.jar;%LPCOMMON%

set LIBS=./bin;%LIB%/jsr331.jar;%SOLVER%;%LOGLIBS%
java -Xms1024m -Xmx1024m %OPTIONS% -classpath "%LIBS%" %PROGRAM%
echo done
pause

  


  
