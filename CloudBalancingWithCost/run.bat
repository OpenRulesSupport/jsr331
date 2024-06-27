@echo off
cd %~dp0
if not "%1" == "" goto defined
set PROGRAM=cloud.balancing.CloudBalancer
goto run
:defined
set PROGRAM=org.jcp.jsr331.samples.%1
:run
echo Run %PROGRAM% ...
set LIB=../org.jcp.jsr331.tck/lib
set LOGLIBS=%LIB%/logging/commons-logging-1.1.jar;%LIB%/logging/commons-logging-api-1.1.jar;%LIB%/logging/log4j-1.2.15.jar

set SOLVER=%LIB%/constrainer/jsr331.constrainer.jar;%LIB%/constrainer/constrainer.light.jar
rem SOLVER=%LIB%/choco/jsr331.choco.jar;%LIB%/choco/choco-solver-2.1.5-20120603-with-sources.jar

set LIBS=./bin;%LIB%/jsr331.jar;%SOLVER%;%LOGLIBS%;../org.jcp.jsr331.scheduler/lib/scheduler.jar
java -Xms1024m -Xmx1024m -classpath "%LIBS%" %PROGRAM% 
echo done
pause