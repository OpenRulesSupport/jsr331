@echo off
call "ant" -f "%~dp0\build.xml" deploy
pause