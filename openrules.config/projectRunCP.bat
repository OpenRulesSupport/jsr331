@echo off
if "%CLASS_NAME%" == "" set CLASS_NAME=com.openrules.ruleengine.DecisionTest
set FILE_PROTOCOL=file:
if "%URL_FILE%" == "none" set FILE_PROTOCOL= 
set OR=../openrules.config/lib
set ORLP=../openrules.config/lib.opt
set ORLIB=%OR%/openrules.all.jar;%OR%/com.openrules.tools.jar
set POILIB=%OR%/poi-3.10-FINAL-20140208.jar;%OR%/poi-ooxml-3.10-FINAL-20140208.jar;%OR%/poi-ooxml-schemas-3.10-FINAL-20140208.jar;%OR%/xmlbeans-2.3.0.jar;%OR%/dom4j-1.6.1.jar
set APACHELIB=%OR%/commons-logging-1.1.jar;%OR%/commons-logging-api-1.1.jar;%OR%/log4j-1.2.15.jar;%OR%/commons-lang-2.3.jar;%OR%/commons-beanutils.jar
rem set CPLIB=%OR%.opt/jsr331.jar;%OR%.opt/jsr331.constrainer.jar;%OR%.opt/constrainer.light.jar;%OR%.opt/com.openrules.opt.jar
set CPLIB=%ORLP%/jsr331.jar;%ORLP%/jsr331.linear.jar;%ORLP%/jsr331.coin.jar;%ORLP%/com.openrules.opt.jar
set ALL=./bin;%APACHELIB%;%ORLIB%;%POILIB%;%CPLIB%
java -Xmx2048m -classpath "%ALL%" %CLASS_NAME% %FILE_PROTOCOL%%FILE_NAME% %DECISION_NAME%
echo done
