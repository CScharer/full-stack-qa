echo off
set BAT_TITLE=Jenkins
title=%BAT_TITLE%
rem Get the current directory.
set PROJECT_DIRECTORY=%cd%
c:
cd\
cls
cd %PROJECT_DIRECTORY%

rem mvn clean test -Dtest=%CLASS_TEST#%CLASS_METHOD% -Dtags="@" -MAVEN_OPTS -XX:MaxPermSize=-Xms256m -Xmx512m -DfailIfNoTests=false

rem Set the variables.
set CLASS_TEST=com.cjs.qa.junit.tests.Scenarios
set CLASS_METHOD=Jenkins
set CLASS_EXT=
set TAG_IDS="@"
set CLASS_TEST_METHOD_EXT=%CLASS_TEST%#%CLASS_METHOD%%CLASS_EXT%
set COMMAND=mvn clean test -Dtest=%CLASS_TEST_METHOD_EXT% -Dtags=%TAG_IDS% -DfailIfNoTests=false

rem Display the variables before running.
echo PROJECT_DIRECTORY=[%PROJECT_DIRECTORY%]
echo CLASS_TEST=[%CLASS_TEST%]
echo CLASS_METHOD=[%CLASS_METHOD%]
echo CLASS_EXT=[%CLASS_EXT%]
echo COMMAND=[%COMMAND%]

echo ***Current JPS Processes Running***
rem Run jps command to see if any surefirebooter*918434971013995292*.jar processes are running
rem taskkill /pid 0 /f
jps
pause
rem Run the command.
start "Maven Running (%BAT_TITLE%) [%CLASS_TEST%]#[%CLASS_METHOD%]-[%CLASS_EXT%]" cmd /c %COMMAND%
exit