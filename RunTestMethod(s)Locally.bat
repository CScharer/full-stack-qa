echo off
cd\
cd Workspace\Github\cjs-app\cjs-app-gui\
rem mvn clean test -DargLine="-Xms64m -Xmx128m" -DfailIfNoTests=false -Dtest=com.cjs.qa.junit.tests.Scenarios#Google -Dtag.ids="@"
cls
set PREFIX=mvn clean test -DargLine="-Xms64m -Xmx128m" -DfailIfNoTests=false
set NAME_CLASS=-Dtest=com.cjs.qa.junit.tests.Scenarios
set NAME_METHOD=Google
set TAG_IDS=-Dtag.ids="@"
set TEST_DEFAULT=%PREFIX% %NAME_CLASS%#NAME_METHOD %TAG_IDS%
set FILE_RESULTS=results.log

echo Current Directory:[%CD%\]
echo Please select one of the options below: 
echo ****************************************
echo 0 - Cancel
echo 1 - Google
echo 2 - Jenkins
echo 3 - Microsoft
echo 4 - Marlboro
echo 5 - Vivit
echo 6 - Wellmark
echo 7 - All
echo ****************************************
set /p TEST_NAME= "Please Select one of the options above: "
echo SELECTION:[%TEST_NAME%]

if errorlevel 1 (
	echo Invalid Option
	goto CANCEL
)

set /p LOG_RESULTS= "Would you like to log the results to a file (Y/N): "
echo SELECTION:[%LOG_RESULTS%]

if [%TEST_NAME%]==[] goto ERROR
if %TEST_NAME%==0 goto CANCEL
if %TEST_NAME%==1 goto TEST_GOOGLE
if %TEST_NAME%==2 goto TEST_JENKINS
if %TEST_NAME%==3 goto TEST_MICROSOFT
if %TEST_NAME%==4 goto TEST_MARLBORO
if %TEST_NAME%==5 goto TEST_VIVIT
if %TEST_NAME%==6 goto TEST_WELLLMARK
if %TEST_NAME%==7 goto TEST_ALL

:TEST_GOOGLE
set COMMAND=%TEST_DEFAULT:NAME_METHOD=Google%
goto RUN_SINGLE_TEST
:TEST_JENKINS
set COMMAND=%TEST_DEFAULT:NAME_METHOD=Jenkins%
goto RUN_SINGLE_TEST
:TEST_MICROSOFT
set COMMAND=%TEST_DEFAULT:NAME_METHOD=Microsoft%
goto RUN_SINGLE_TEST
:TEST_MARLBORO
set COMMAND=%TEST_DEFAULT:NAME_METHOD=Marlboro%
goto RUN_SINGLE_TEST
:TEST_VIVIT
set COMMAND=%TEST_DEFAULT:NAME_METHOD=Vivit%
goto RUN_SINGLE_TEST
:TEST_WELLLMARK
set COMMAND=%TEST_DEFAULT:NAME_METHOD=Wellmark%
goto RUN_SINGLE_TEST
:TEST_ALL
set COMMAND=%TEST_DEFAULT:NAME_METHOD=Google%
echo %COMMAND%
%COMMAND%
set COMMAND=%TEST_DEFAULT:NAME_METHOD=Jenkins%
echo %COMMAND%
%COMMAND%
set COMMAND=%TEST_DEFAULT:NAME_METHOD=Microsoft%
echo %COMMAND%
%COMMAND%
set COMMAND=%TEST_DEFAULT:NAME_METHOD=Marlboro%
echo %COMMAND%
%COMMAND%
set COMMAND=%TEST_DEFAULT:NAME_METHOD=Vivit%
echo %COMMAND%
%COMMAND%
set COMMAND=%TEST_DEFAULT:NAME_METHOD=Wellmark%
echo %COMMAND%
%COMMAND%
goto COMPLETE

:RUN_SINGLE_TEST
echo %COMMAND%
if %LOG_RESULTS%=="Y" goto RUN_WITH_LOG
echo %COMMAND%
%COMMAND%
goto COMPLETE

:RUN_WITH_LOG
echo %COMMAND%>%FILE_RESULTS%
%COMMAND%>>%FILE_RESULTS%
goto COMPLETE

:COMPLETE
echo Processing Complete!
goto END
:ERROR
echo Invalid Option!
:CANCEL
echo Cancelling!

:END
pause
cls
exit