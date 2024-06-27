@echo off
if "%CLASS_NAME%" == "" set CLASS_NAME=com.openrules.ruleengine.DecisionTest
set FILE_PROTOCOL=file:
if "%URL_FILE%" == "none" set FILE_PROTOCOL= 
set OR=../openrules.config/lib
set ORLIB=%OR%/openrules.all.jar;%OR%/com.openrules.tools.jar
set POILIB=%OR%/poi-3.10-FINAL-20140208.jar;%OR%/poi-ooxml-3.10-FINAL-20140208.jar;%OR%/poi-ooxml-schemas-3.10-FINAL-20140208.jar;%OR%/xmlbeans-2.3.0.jar;%OR%/dom4j-1.6.1.jar
set APACHELIB=%OR%/commons-logging-1.1.jar;%OR%/commons-logging-api-1.1.jar;%OR%/log4j-1.2.15.jar;%OR%/commons-lang-2.3.jar;%OR%/commons-beanutils.jar
set ALL=./bin;%APACHELIB%;%ORLIB%;%POILIB%
java -Xmx512m -classpath "%ALL%" %CLASS_NAME% %FILE_PROTOCOL%%FILE_NAME% %DECISION_NAME%
echo done