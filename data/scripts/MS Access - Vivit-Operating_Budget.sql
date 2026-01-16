SELECT [h].[Year_Month],[h].[Contractor],[h].[Hours],[d].[Rate],([h].[Hours]*[d].[Rate]) AS [Invoice_Amount] FROM [Hours_Contractor] h
INNER JOIN [DOM_Vivit Contractor] d 
ON [d].[Contractor] = [h].[Contractor]
/*
WHERE [h].[Hours]>0
AND [Invoice_Amount]>100
AND ([h].[Hours]*[d].[Rate])>0
AND [h].[Year_Month]>'201401'
*/  
ORDER BY [h].[Year_Month],[h].[Contractor];