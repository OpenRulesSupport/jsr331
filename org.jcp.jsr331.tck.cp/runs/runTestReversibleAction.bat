@echo off
cd %~dp0
set PROGRAM=org.jcp.jsr331.samples.TestReversibleAction
echo Run %PROGRAM% ...
echo Start Search with Try-Fail Animation
set LOGLIBS=./lib/commons-logging-1.1.jar;./lib/commons-logging-api-1.1.jar;./lib/log4j-1.2.15.jar
set LIBS=bin;./lib/jsr331.jar;./lib/jsr331.constrainer.jar;./lib/constrainer.jar;%LOGLIBS%
java -Xmx512m -classpath "%LIBS%" %PROGRAM% 
echo done
pause

  
