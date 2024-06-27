@echo off
cd %~dp0
echo Run Miss Manners ...
set LIBS=bin;./lib/openrules.all.jar;./lib/com.openrules.tools.jar;./lib/commons-lang-2.3.jar;./lib/commons-logging-1.1.jar;./lib/log4j-1.2.13.jar;./lib/poi-3.2-FINAL-20081019.jar;./lib/javax.cp.jar;./lib/cpinside.jar;./lib/cpinside.constrainer.jar;./lib/constrainer.jar
set XLS_PATH=file:../dth_nx_domains_dfo_openrules_Repository/rules/main
java -Xmx512m -classpath "%LIBS%" openrules.manners.MannersSolver 16 
echo done
cd %~dp0
pause

  
