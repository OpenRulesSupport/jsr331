@echo off
if "%CLASS_NAME%" == "" set CLASS_NAME=org.jcp.jsr331.samples.SendMoreMoney
if "%SOLVER%" == "" set SOLVER=Constrainer
call mvn -Dsolver=%SOLVER% compile exec:java -Dexec.mainClass=%CLASS_NAME% -q