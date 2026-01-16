/*Update Events*/
/*DELETE FROM [t_Vivit_Events_Previous];
INSERT INTO [t_Vivit_Events_Previous] SELECT * FROM [t_Vivit_Events_Current];
DELETE FROM [t_Vivit_Events_Current];*/
/*Update Event Attendees*/
/*DELETE FROM [t_Vivit_Event_Attendees_Previous];
INSERT INTO [t_Vivit_Event_Attendees_Previous] SELECT * FROM [t_Vivit_Event_Attendees_Current];
DELETE FROM [t_Vivit_Event_Attendees_Current];*/
/*Update Groups*/
/*DELETE FROM [t_Vivit_Groups_Previous];
INSERT INTO [t_Vivit_Groups_Previous] SELECT * FROM [t_Vivit_Groups_Current];
DELETE FROM [t_Vivit_Groups_Current];*/
/*Update Leaders*/
/*DELETE FROM [t_Vivit_Leaders_Previous];
INSERT INTO [t_Vivit_Leaders_Previous] SELECT * FROM [t_Vivit_Leaders_Current];
DELETE FROM [t_Vivit_Leaders_Current];*/
/*Update Group/Leader Links*/
/*DELETE FROM [t_Vivit_GroupLeaderLink_Previous];
INSERT INTO [t_Vivit_GroupLeaderLink_Previous] SELECT * FROM [t_Vivit_GroupLeaderLink_Current];
DELETE FROM [t_Vivit_GroupLeaderLink_Current];*/
/*Update Blogs*/
/*DELETE FROM [t_Vivit_Blogs_Previous];
INSERT INTO [t_Vivit_Blogs_Previous] SELECT * FROM [t_Vivit_Blogs_Current];
DELETE FROM [t_Vivit_Blogs_Current];*/
/*Update Calendars*/
/*DELETE FROM [t_Vivit_Calendars_Current];*/
/*Update Forums*/
/*DELETE FROM [t_Vivit_Forums_Previous];
INSERT INTO [t_Vivit_Forums_Previous] SELECT * FROM [t_Vivit_Forums_Current];
DELETE FROM [t_Vivit_Forums_Current];*/
/*Update Hours Contractor*/
INSERT INTO [t_Vivit_Hours_Contractor] 
SELECT [Year_Month],[Contractor],0 AS [Hours] 
FROM
(
	SELECT STRFTIME('%Y%m') AS [Year_Month],[Contractor]
	FROM [v_Vivit_Contractors_Active]
EXCEPT 
	SELECT [Year_Month],[Contractor]
	FROM [t_Vivit_Hours_Contractor] 
	WHERE[Year_Month]=STRFTIME('%Y%m') 
);
/*Update Reports Treasurer*/
/*DELETE FROM [t_Vivit_Reports_Treasurer];
INSERT INTO [t_Vivit_Reports_Treasurer] 
SELECT [tbl_name],ROUND(0) 
FROM [sqlite_master] 
WHERE [type]='view' 
AND [name] LIKE 'v_Vivit_Contractor_Hours%';
*/