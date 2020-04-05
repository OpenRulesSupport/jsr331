if "%CLASS_NAME%" == "" set CLASS_NAME=org.jcp.jsr331.constrainer.tests.WhoKilledAgatha
if "%SOLVER%" == "" set SOLVER=Constrainer
call mvn -P %SOLVER% compile exec:java -Dexec.mainClass=%CLASS_NAME% -Dexec.classpathScope="test" -q
