@echo off
cd %~dp0
call define
if "%CLASS_NAME%" == "" set CLASS_NAME=com.openrules.ruleengine.DecisionTest
call ant -f "%~dp0\build.xml" run
pause