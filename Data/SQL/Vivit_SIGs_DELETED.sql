/*DELETED*/
SELECT 'SIG' AS [Type], 'Deleted' AS [Action], * FROM
(
	SELECT [Group], [Name]
	FROM [tblVivit_GroupSIGS_Previous]
	WHERE [Web_Site_Member_ID] != 'SIGS'
	EXCEPT
	SELECT [Group], [Name]
	FROM [tblVivit_GroupSIGS_Current]
	WHERE [Web_Site_Member_ID] != 'SIGS'
)
ORDER BY [Group], [Name]