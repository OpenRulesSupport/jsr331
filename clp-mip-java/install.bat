@echo off

cd %~dp0

call mvn install:install-file -Dfile=clp-java-1.16.15-mip-full.jar -DpomFile=pom.xml

echo.
echo clp-mip-java    INSTALLATION COMPLETED
pause
