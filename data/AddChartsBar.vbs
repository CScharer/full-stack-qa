Option Explicit

Public Const msoFalse = 0
Public Const msoScaleFromTopLeft = 0
Public Const msoTrue = -1

Public Const xlBarClustered = 57
Public Const xlDash = -4115
Public Const xlDashDot = 4
Public Const xlDot = -4118
Public Const xlLastCell = 11
Public Const xlMaximized = -4137
Public Const xlSolid = 1
Public Const xlValue = 2

Public Function addChartsBar(ByVal sFilePathName)
    Dim oExcel
    Dim oWorkbook
    Dim sFilePathNameNew
    
    oWScript.StdOut.Write ("addChartsBar"): oWScript.StdOut.Write (vbNewLine)
    addChartsBar = 1
'    Set oWorkbook = ActiveWorkbook
    Set oExcel = CreateObject("Excel.Application")
    'Workbooks.Open fileName, updateLinks, readOnly
    Set oWorkbook = oExcel.Workbooks.Open(sFilePathName, False, False)
    oExcel.DisplayAlerts = False
    'oExcel.Visible = True
    'oExcel.WindowState = xlMaximized
    Call deleteChartsAll(oWorkbook)
    Call createChartsAll(oWorkbook)
    oWScript.StdOut.Write ("Saving file [" & sFilePathName & "]"): oWScript.StdOut.Write (vbNewLine)
    oWorkbook.Save
    'sFilePathNameNew = Replace(sFilePathName, "StatisticsBase", "StatisticsBase_New")
    'oWScript.StdOut.Write("Saving file [" & sFilePathName & "] as [" & sFilePathNameNew & "]"):oWScript.StdOut.Write(vbNewLine)
    'sFilePathName = sFilePathNameNew
    oWScript.StdOut.Write ("Saving file [" & sFilePathName & "] as [" & sFilePathName & "]"): oWScript.StdOut.Write (vbNewLine)
    oWorkbook.SaveAs sFilePathName
    oWorkbook.Close
    oExcel.DisplayAlerts = True
    oExcel.Quit
    Set oWorkbook = Nothing
    Set oExcel = Nothing
    addChartsBar = 0
End Function

Sub createChartsAll(ByVal oWorkbook)
    Dim iSheet
    Dim iSheets
    Dim oSheet
    Dim sSheetName
    
    oWScript.StdOut.Write ("createChartsAll"): oWScript.StdOut.Write (vbNewLine)
    For iSheet = 2 To oWorkbook.sheets.Count
        Set oSheet = oWorkbook.sheets(iSheet)
        sSheetName = oSheet.Name
        If InStr(sSheetName, "Vivit_") = 0 Then
            oWScript.StdOut.Write ("createChartsAll(" & sSheetName & ")"): oWScript.StdOut.Write (vbNewLine)
            Call createChartBar(oSheet)
        Else
            Call addFilter(oSheet, "A2")
        End If
    Next 'iSheet
    Set oSheet = Nothing
End Sub

Public Function addFilter(ByVal oSheet, ByVal sRangeStart)
    Dim oSelection1
    Dim oSelection2
    
    Set oSelection1 = oSheet.Range(sRangeStart)
    Set oSelection2 = oSheet.Range(oSelection1, oSelection1.SpecialCells(xlLastCell))
    oSelection2.AutoFilter
    Set oSelection2 = Nothing
    Set oSelection1 = Nothing
End Function

Public Function createChartBar(ByVal oSheet)
    Dim iLeft
    Dim iColumn
    Dim iColumns
    Dim iRow
    Dim iRows
    Dim oChart
    Dim oChartAdded
    Dim oRange
    Dim sSheetName
    
    sSheetName = oSheet.Name
    oWScript.StdOut.Write ("createChartBar(" & sSheetName & ")"): oWScript.StdOut.Write (vbNewLine)
    iColumns = oSheet.UsedRange.columns.Count
    iRows = oSheet.UsedRange.rows.Count
    For iColumn = 1 To (iColumns - 1)
        For iRow = 1 To (iRows - 1)
            oSheet.Range("A1").Offset(iRow, iColumn).Value = oSheet.Range("A1").Offset(iRow, iColumn).Value
            'oSheet.Range("B1").Offset(iRow, 1).Value = oSheet.Range("B1").Offset(iRow, 1).Value
        Next 'iRow
    Next 'iColumn
'    With oSheet
'       .Range("A1").Offset(iRows, 0).Value = "Member Data Provided"
'       .Range("B1").Offset(iRows, 0).FormulaR1C1 = "=SUM(R[-" & (iRows - 3) & "]C:R[-1]C)"
'       .Range("C1").Offset(iRows, 0).FormulaR1C1 = "=SUM(RC[-1]/R[-" & (iRows - 2) & "]C[-1])"
'       .Range("C1").Offset(iRows, 0).NumberFormat = "0.00%"
'       .Columns("A:A").EntireColumn.AutoFit
'   End With
    iRows = oSheet.UsedRange.rows.Count
    Set oChartAdded = oSheet.Shapes.AddChart2(216, xlBarClustered)
    With oChartAdded
        '.IncrementTop -65.25
        .Top = 0
        '.IncrementLeft 28.5
        iLeft = iLeft + oSheet.columns("A:A").ColumnWidth
        iLeft = iLeft + oSheet.columns("B:B").ColumnWidth
        iLeft = iLeft + oSheet.columns("C:C").ColumnWidth
        iLeft = Int(iLeft * 6)
        .left = iLeft
        .ScaleWidth 2, msoFalse, msoScaleFromTopLeft
        .ScaleHeight 1.5, msoFalse, msoScaleFromTopLeft
        Set oChart = .chart
    End With
    Set oRange = oSheet.Range(sSheetName & "!$A$3:$B$" & iRows)
    With oChart
        Call .SetSourceData(oRange)
        With .ChartTitle
            .Text = sSheetName
            .Format.TextFrame2.TextRange.Characters.Text = Replace(sSheetName, "_", " ")
        End With
        .Legend.Delete
        With .Axes(xlValue)
            .HasMajorGridlines = True
            With .MajorGridlines
                .Border.LineStyle = xlSolid
                With .Format.Line
                    .Weight = 1
                    .ForeColor.RGB = vbBlack
                    .Transparency = 0.5
                    .Visible = True
                    '.Visible = msoTrue
                End With
            End With
            .HasMinorGridlines = True
            .Format.Line.Visible = msoTrue
            With .MinorGridlines
                .Border.LineStyle = xlDot
                With .Format.Line
                    .Weight = 0.5
                    .ForeColor.RGB = vbBlack
                    .Transparency = 0.75
                    .Visible = True
                    '.Visible = msoTrue
                End With
            End With
        End With
    End With
    Call formatSeries(oChart)
    Set oChart = Nothing
    Set oChartAdded = Nothing
    Set oRange = Nothing
End Function

Public Function getColor(ByVal sColor)
    oWScript.StdOut.Write ("getColor(" & sColor & ")"): oWScript.StdOut.Write (vbNewLine)
    getColor = RGB(16, 58, 113)
    Select Case (LCase(sColor))
        Case "blue"
            getColor = RGB(16, 58, 113)
        Case "gold"
            getColor = RGB(254, 195, 64)
        Case "light blue"
            getColor = RGB(81, 139, 201)
        Case "purple"
            getColor = RGB(65, 57, 95)
    End Select
End Function

Function formatSeries(ByVal oChart)
    oWScript.StdOut.Write ("formatSeries"): oWScript.StdOut.Write (vbNewLine)
    'oChart.FullSeriesCollection(1).Select
    'Application.CommandBars("Format Object").Visible = False
    With oChart.FullSeriesCollection(1).Format.Fill
        .Visible = msoTrue
        .ForeColor.RGB = getColor("light blue")
        .Transparency = 0
        .Solid
    End With
End Function

Function deleteChartsAll(ByVal oWorkbook)
    Dim oChart 'As ChartObject
    Dim oSheet
    
    oWScript.StdOut.Write ("deleteChartsAll"): oWScript.StdOut.Write (vbNewLine)
    For Each oSheet In oWorkbook.sheets
        For Each oChart In oSheet.ChartObjects
            oChart.Delete
        Next 'oChart
    Next 'oSheet
End Function

Dim args
Dim sFileName
Dim pathCurrent
Dim oFSO
Dim oFolder
Dim oSubFolders
Dim oFile
Dim oWScript

Set oWScript = WScript
Set args = WScript.Arguments
'"C:\Workspace\Github\cjs-app\cjs-app-gui\Data\Vivit\Vivit_Daily_Automation_Report_StatisticsBase_20170827_113753.535.xls"
sFileName = args.Item(0)  'full path and file name gets passed in.
oWScript.StdOut.Write ("Processing file [" & sFileName & "]"): oWScript.StdOut.Write (vbNewLine)
'MsgBox sFileName
Call addChartsBar(sFileName)
Set oFSO = CreateObject("Scripting.FileSystemObject")
pathCurrent = "C:\Workspace\Github\cjs-app\cjs-app-gui\Data\Vivit\"
pathCurrent = oFSO.GetAbsolutePathName(".") & "\"
oWScript.StdOut.Write (pathCurrent)
Set oFSO = CreateObject("Scripting.FileSystemObject")
Set oFolder = oFSO.getFolder(pathCurrent)
For Each oFile In oFolder.Files
    If InStr(oFile.Name, "Vivit_Daily_Automation_Report_StatisticsBase") > 0 Then
        oWScript.StdOut.Write (oFile.Name)
'       sFileName = pathCurrent & oFile.Name
        oWScript.StdOut.Write (sFileName)
'       addChartsBar sFileName
        oWScript.StdOut.Write ("Done!")
    End If
Next
Set oFolder = Nothing
Set oFSO = Nothing