if "%CLASS_NAME%" == "" set CLASS_NAME=org.jcp.jsr331.samples.SendMoreMoney
if "%SOLVER%" == "" set SOLVER=Constrainer
call mvn -P %SOLVER% compile exec:java -Dexec.mainClass=%CLASS_NAME% -Dexec.classpathScope="test" -e
