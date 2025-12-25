SELECT SUBSTR([Group],1,4) AS [Type], [Group], CAST(REPLACE(t.[Timing],"ms","")AS INTEGER) AS [Timing (milliseconds)], [WallPosts], [Chapter]
FROM [tblVivit_GroupPage_Timings] t
ORDER BY [Timing (milliseconds)] DESC