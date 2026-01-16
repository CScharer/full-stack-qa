CREATE VIEW "VIEW_GROUPS_DELETED" AS    /*DELETED*/
SELECT SUBSTR([Group],1,4) AS [Type], 'Deleted' AS [Action], * FROM
(
	SELECT *
	FROM [tblVivit_Groups_Previous]
	/*WHERE [Web_Site_Member_ID] != 'LUGS'
	AND [Web_Site_Member_ID] != 'SIGS'*/
	EXCEPT
	SELECT *
	FROM [tblVivit_Groups_Current]
	/*WHERE [Web_Site_Member_ID] != 'LUGS'
	AND [Web_Site_Member_ID] != 'SIGS'*/
)
ORDER BY [Type], [Group], [Name]