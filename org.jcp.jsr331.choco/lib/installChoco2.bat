cd %~dp0
call mvn install:install-file -DgroupId=com.javasolver -DartifactId=jsr331-choco2 -Dversion=2.1.5 -Dpackaging=jar -Dfile=choco-solver-2.1.5-20120603-with-sources.jar
pause