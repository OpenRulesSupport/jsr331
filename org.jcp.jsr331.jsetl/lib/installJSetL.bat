cd %~dp0
call mvn install:install-file -DgroupId=com.javasolver -DartifactId=jsr331-jsetl2 -Dversion=2.3.0 -Dpackaging=jar -Dfile=jsetl.jar
pause