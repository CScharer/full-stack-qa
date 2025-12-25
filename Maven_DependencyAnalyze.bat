echo off
set BAT_TITLE=Maven Dependency Analyzer
title=%BAT_TITLE%
rem Get the current directory.
set PROJECT_DIRECTORY=%cd%
c:
cd\
cls
cd %PROJECT_DIRECTORY%

rem Set the variables.
set COMMAND=mvn dependency:analyze

rem Display the variables before running.
echo COMMAND=[%COMMAND%]

echo ***Current JPS Processes Running***
rem Run jps command to see if any surefirebooter*918434971013995292*.jar processes are running
rem taskkill /pid 0 /f
jps
pause
rem Run the command.
start "Maven Running (%BAT_TITLE%) [%COMMAND%]" cmd /c %COMMAND%
exit