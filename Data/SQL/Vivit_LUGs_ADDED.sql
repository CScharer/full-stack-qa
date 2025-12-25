/*ADDED*/
SELECT 'LUG' AS [Type], 'Added' AS [Action], * FROM
(
	SELECT [Group], [Name]
	FROM [tblVivit_GroupLUGS_Current]
	WHERE [Web_Site_Member_ID] != 'LUGS'
	EXCEPT
	SELECT [Group], [Name]
	FROM [tblVivit_GroupLUGS_Previous]
	WHERE [Web_Site_Member_ID] != 'LUGS'
)
ORDER BY [Group], [Name]