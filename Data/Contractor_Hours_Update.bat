exit
set YEAR_MONTH=201804
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 0 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Annemarie Stuiver';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 0 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Felicia Ford';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 0 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Jill Stefan';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 0 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Julanne Rutten (T1)';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 0 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Julanne Rutten (T2)';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 0 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Michelle Pivonka';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 0 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Rhonda Quaranta';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 0 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Rocky Pisto';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 0 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Stephanie Konkoy (ACL)';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 0 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Stephanie Konkoy (ED)';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 0 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Susan Russel';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 0 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Teri Sarallo';
SELECT * FROM [t_Vivit_Hours_Contractor] ORDER BY Contractor;
exit
/*
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 87.5 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Annemarie Stuiver';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 7.25 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Felicia Ford';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 148.75 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Jill Stefan';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 4 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Julanne Rutten (T1)';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 22.5 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Julanne Rutten (T2)';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 22.5 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Michelle Pivonka';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 90 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Rhonda Quaranta';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 80 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Rocky Pisto';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 20.92 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Stephanie Konkoy (ACL)';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 124.8 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Stephanie Konkoy (ED)';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 0 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Susan Russel';
UPDATE [t_Vivit_Hours_Contractor] SET Hours = 140 WHERE [Year_Month]='%YEAR_MONTH%' AND [Contractor]='Teri Sarallo';
*/
SELECT * FROM [t_Vivit_Hours_Contractor] WHERE [Year_Month]='%YEAR_MONTH%' ORDER BY Contractor
exit
Map<String, String> map = new HashMap<>();
map.put("Annemarie Stuiver", "0");
map.put("Felicia Ford", "0");
map.put("Jill Stefan", "0");
map.put("Julanne Rutten (T1)", "0");
map.put("Julanne Rutten (T2)", "0");
map.put("Michelle Pivonka", "0");
map.put("Rhonda Quaranta", "0");
map.put("Rocky Pisto", "0");
map.put("Stephanie Konkoy (ACL)", "0");
map.put("Stephanie Konkoy (ED)", "0");
map.put("Susan Russel", "0");
map.put("Teri Sarallo", "0");