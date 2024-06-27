===============================================================================
                   J A V A  C O M M U N I T Y  P R O C E S S
       J S R 3 3 1 "C O N S T R A I N T  P R O G R A M M I N G  A P I"
                      P R E L I M I N A R Y  R E L E A S E 
                               V. 0.7.1
===============================================================================  

                            
This is a preliminary release 0.7.1 of the JSR-331 "Constraint Programming API"
http://jcp.org/en/jsr/detail?id=331

This project includes:

1) Sample constraint satisfaction and optimization problems - see 
folder src/org/jcp/jsr331/samples

2) Libraries - see folder "lib":
   jsr331.jar - solver independent JSR-331 files
   lib/logging/*.jar - Apache Logging jars
   lib/choco/*.jar - jars for Choco Solver
   lib/jacop/*.jar - jars for JaCoP Solver
   lib/constrainer/*.jar - jars for Constrainer Solver
   
   The provided libraries include all sources and are available under the terms of 
   the proper open source licenses included in the proper folders.


WORKING WITH ECLIPSE IDE
========================
To use the JSR-331 with Eclipse IDE, simply import the folder "jsr331" into your workspace. 
You may run all samples directly from Eclipse by selecting their sources with a right-click 
and then "Run as Java Application". To switch between underlying solvers, just select the 
Project Properties, and simply change Libraries inside Java Build Path.

WORKING WITH A FILE MANAGER
===========================
You may used the provided batch files (or write your own) if you prefer to work
without any IDE. You may run different examples by running the proper batch file, 
e.g. "runBins.bat" will execute the example "Bins" whose source code is located at
jsr331/src/org/jcp/jsr331/samples/Bins.java.

All batch files are based on the file "run.bat":

------------------
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
rem set SOLVER=./lib/choco/jsr331.choco.jar;./lib/choco/choco-solver-2.1.1-20100709.142532-2.jar
rem set SOLVER=./lib/jacop/jsr331.jacop.jar;./lib/jacop/jacop-3.0.jar

set LIBS=./bin;./lib/jsr331.jar;%SOLVER%;%LOGLIBS%
java -Xmx512m -classpath "%LIBS%" %PROGRAM% 
echo done
pause
------------------

To switch between CP solvers you need to modify the file run.bat. For example, the above text
defines SOLVER as "constrainer". To switch to "choco" put "rem " in front of 
"set SOLVER=./lib/constrainer/..." and remove "rem " in front of "set SOLVER=./lib/choco/...".

If you work with UNIX/LINUX you need to replace *.bat files with similar *.sh files. 

Send your comments and suggestions to j.feldman@4c.ucc.ie.

