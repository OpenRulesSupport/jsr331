@echo off
if "%CLASS_NAME%" == "" set CLASS_NAME=org.jcp.jsr331.linear.samples.Knapsack
if "%SOLVER%" == "" set SOLVER=Coin
call mvn -P %SOLVER% compile exec:java -Dexec.mainClass=%CLASS_NAME% -Dexec.classpathScope="test" -q -e
