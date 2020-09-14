# Bootiful Azure

## Azure CosmosDB 
When you connect you should add `&retrywrites=false` to `spring.data.mongodb.uri`.

## Azure Object Storage
If you see the following error, `The account being accessed does not support http`: [see this](https://www.itexperience.net/the-account-being-accessed-does-not-support-http-with-blob-sas-url-in-azure/).
 
## SQL Server 
If you see the following error, `com.microsoft.sqlserver.jdbc.SQLServerException: ...Client with IP address '*.*.*.*' is not allowed to access the server...`, 
you [can check out this link](https://stackoverflow.com/questions/34760223/client-with-ip-address-is-not-allowed-to-access-the-server-azure-sql-database). 
The TL;DR is that you need to find the SQL Server instance, go to the `Firewalls and virtual networks` section, and then add a rule under Client IP Address 
for your IP address for both the stop and start range. The Azure page even shows you your IP address.


