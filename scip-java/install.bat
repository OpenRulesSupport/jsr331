@echo off

cd %~dp0

call mvn install:install-file -Dfile=scip-java-1.0.0-full.jar -DpomFile=pom.xml

echo.
echo scip-java    INSTALLATION COMPLETED
pause
