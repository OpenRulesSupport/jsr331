@echo off
cd %~dp0
if not "%1" == "" goto defined
set PROGRAM=org.jcp.jsr331.samples.SendMoreMoney
goto run
:defined
set PROGRAM=org.jcp.jsr331.samples.%1
:run
echo Run %PROGRAM% ...
set LOGLIBS=./lib/logging/commons-logging-1.1.jar;./lib/logging/commons-logging-api-1.1.jar;./lib/logging/log4j-1.2.15.jar

set SOLVER=./lib/constrainer/jsr331.constrainer.jar;./lib/constrainer/constrainer.light.jar
rem set SOLVER=./lib/choco/jsr331.choco.jar;./lib/choco/choco-solver-2.1.1-20110622-with-sources.jar
rem set SOLVER=./lib/jacop/jsr331.jacop.jar;./lib/jacop/jacop-3.0.jar

set LIBS=./bin;./lib/jsr331.jar;%SOLVER%;%LOGLIBS%
java -Xmx512m -classpath "%LIBS%" %PROGRAM% 
echo done
pause

  
