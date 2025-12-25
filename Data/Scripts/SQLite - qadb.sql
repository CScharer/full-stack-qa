-- Initialize Vivit Database
DROP TABLE IF EXISTS [tblVivit_Groups_Previous];
CREATE TABLE [tblVivit_Groups_Previous] AS SELECT * FROM [tblVivit_Groups_Current];
DROP TABLE IF EXISTS [tblVivit_Groups_Current];
CREATE TABLE [tblVivit_Groups_Current] AS SELECT * FROM [tblVivit_Groups_Previous] WHERE ROWID=0;
DROP TABLE IF EXISTS [tblVivit_Leaders_Previous];
CREATE TABLE [tblVivit_Leaders_Previous] AS SELECT * FROM [tblVivit_Leaders_Current];
DROP TABLE IF EXISTS [tblVivit_Leaders_Current];
CREATE TABLE [tblVivit_Leaders_Current] AS SELECT * FROM [tblVivit_Leaders_Previous] WHERE ROWID=0;
DROP TABLE IF EXISTS [tblVivit_GroupLeaderLink_Previous];
CREATE TABLE [tblVivit_GroupLeaderLink_Previous] AS SELECT * FROM [tblVivit_GroupLeaderLink_Current];
DROP TABLE IF EXISTS [tblVivit_GroupLeaderLink_Current];
CREATE TABLE [tblVivit_GroupLeaderLink_Current] AS SELECT * FROM [tblVivit_GroupLeaderLink_Previous] WHERE ROWID=0;
DROP TABLE IF EXISTS [tblVivit_Blogs_Previous];
CREATE TABLE [tblVivit_Blogs_Previous] AS SELECT * FROM [tblVivit_Blogs_Current];
DROP TABLE IF EXISTS [tblVivit_Blogs_Current];
CREATE TABLE [tblVivit_Blogs_Current] AS SELECT * FROM [tblVivit_Blogs_Previous] WHERE ROWID=0;
-- Initialize Vivit Database

DROP TABLE IF EXISTS [TEMP];

DELETE FROM
[tblVivit_GroupLeaderLink_Current];

CREATE TABLE
[TEMP] AS
SELECT *
FROM [tblVivit_Reports];

DROP TABLE
[TEMP];

SELECT *
FROM [sqlite_master]
ORDER BY [Type];

SELECT SUBSTR([Group],1,4) AS [Type], [Group], CAST(REPLACE([t].[Timing],"ms","")AS INTEGER) AS [Timing (milliseconds)], [WallPosts], [ChapterURL]
FROM [tblVivit_GroupPage_Timings] [t]
ORDER BY [Timing (milliseconds)] DESC;

SELECT SUBSTR([Group],1,4) AS [PageType],[Href] AS [PageID],[Web_Site_Member_ID],[Position]
FROM [tblVivit_Groups_Current]
WHERE [Href] != '';

SELECT [g].*
FROM [tblVivit_Groups_Current] [g]
ORDER BY [Group],[Position];

SELECT [g].*, [gl].[PageID]
FROM [tblVivit_Groups_Current] [g]
INNER JOIN [tblVivit_GroupLeaderLink_Current] [gl]
ON [g].[ChapterURL]=[gl].[PageID]
AND [g].[Web_Site_Member_ID]=[gl].[Web_Site_Member_ID]
ORDER BY [Group],[Position];

SELECT DISTINCT [g].*, [gl].[Web_Site_Member_ID],[gl].[PageID]
FROM [tblVivit_Groups_Current] [g]
INNER JOIN [tblVivit_GroupLeaderLink_Current] [gl]
ON [g].[ChapterURL]=[gl].[PageID]
AND [g].[Web_Site_Member_ID]=[gl].[Web_Site_Member_ID]
ORDER BY [Group],[Position];

SELECT DISTINCT [g].[Group],[l].[Name],[gl].[Position]
FROM [tblVivit_Leaders_Current] [l]
INNER JOIN [tblVivit_GroupLeaderLink_Current] [gl]
ON [l].[Web_Site_Member_ID]=[gl].[Web_Site_Member_ID]
INNER JOIN [tblVivit_Groups_Current] [g]
ON [g].[Web_Site_Member_ID]=[gl].[Web_Site_Member_ID]
WHERE [gl].[Position] LIKE 'SIG%'
ORDER BY [g].[Group],[l].[Name];

SELECT [g].[]
INNER JOIN [tblVivit_Groups_Current] [g]
ON [g].[]=[gl].[];

SELECT  DISTINCT [g].[Group],[g].[Name],[gl].[Position],[gl].[PageID]
FROM [tblVivit_Groups_Current] [g]
INNER JOIN [tblVivit_GroupLeaderLink_Current] [gl]
ON [g].[ChapterURL]=[gl].[PageID]
AND [g].[Web_Site_Member_ID]=[gl].[Web_Site_Member_ID]
--WHERE [g].[Name] LIKE 'Christopher%'
ORDER BY [g].[Group],[gl].[Position];

SELECT DISTINCT [Group],[ChapterURL],[Name], Count([Name]) AS [LeaderCount]
FROM [tblVivit_Leaders_Current]
WHERE [Name]='Mark Herbert'
GROUP BY [Group],[ChapterURL],[Name];

SELECT [Group],[ChapterURL],[Position], Count([Name]) AS [LeaderCount]
FROM [tblVivit_Groups_Current]
GROUP BY [Group],[ChapterURL],[Position]
ORDER BY [Group],[LeaderCount] DESC;

SELECT *
FROM [tblVivit_Groups_Current]
/*GROUP BY [ChapterURL],[Group]*/;