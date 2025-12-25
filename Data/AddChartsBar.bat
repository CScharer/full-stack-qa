rem AddChartsBar.bat "C:\Workspace\Data\Vivit\AddChartsBar.vbs" "C:\Workspace\Data\Vivit\AddChartsBar.xlsx"
set FILE_VBS=%1%
set FILE_PATH_NAME=%2%
cls
cscript //Job:CJS //Logo //U "%FILE_VBS%" "%FILE_PATH_NAME%"
REM "%FILE_VBS%" "%FILE_PATH_NAME%"
exit