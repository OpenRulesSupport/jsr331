cd %~dp0
call mvn install:install-file -DgroupId=com.javasolver -DartifactId=jsr331-sugar-213 -Dversion=2.1.3 -Dpackaging=jar -Dfile=sugar-v2-1-3.jar
call mvn install:install-file -DgroupId=com.javasolver -DartifactId=jsr331-sat4j -Dversion=2.1.3 -Dpackaging=jar -Dfile=org.sat4j.core.jar
pause