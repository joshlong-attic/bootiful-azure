# Bootiful Azure 


* welcome folks thanks for coming sit down lets get to it 
* we don't have a ton of time 
* I wish we did to ponder this that and everything but we simply dont
* 20 minutes isnt enough time to make a baked Alaska (or some other famously quick thing - todo google food recipes)
* but it IS enough time to introduce the Spring and Azure technologies and the opportunities that they provide us and see those technologies deployed to production (something like that) 
* start.spring.io create a new projpect using java 8 called greetings-service
* SQL server is one of the most powerful databases in the world; tons of features and -best of all - Microsoft will run it for you 
* support s reactive variant as well 
* now of course you could run your own SQ server the Real power comes in something like cosmosdb 
* bytes are data too! Azure objet storage is awesome 
* and of course sometimes I wanna tell other folks in the system about my data. This is what messaging is for. Martin fowler wrote a nice blog introducing four types of messaging 
* and now that weve got a singing dancing Spring app in less than 160 lines of code, lets get this thing to production! 
* Azure portal and go! 

<!-- application.properties -->

##
## Azure Service Bus && Spring Cloud Stream
service-bus.connection-string=${AZURE_SERVICE_BUS_CONNECTION_STRING}
spring.cloud.azure.servicebus.connection-string=${service-bus.connection-string}
spring.cloud.stream.bindings.input.destination=messages-queue
spring.cloud.stream.bindings.output.destination=messages-queue
##
## Azure Object Storage
azure.storage.account-name=bootifulstorage
azure.storage.container-name=cats
azure.storage.account-key=${AZURE_OBJECT_STORAGE_ACCOUNT_KEY}
##
## Sql Server
spring.datasource.password=${AZURE_SQL_SERVER_DB_PASSWORD}
spring.datasource.url=${AZURE_SQL_SERVER_DB_URL}
spring.datasource.username=bootiful-admin
##
## CosmosDB
spring.data.mongodb.database=bootiful-cosmosdb
spring.data.mongodb.uri=${AZURE_COSMOS_DB_MONGO_URL}

 
 <!-- schema.sql -->
 -- drop table CUSTOMERS ;
if not exists(
        select name
        from sys.tables
        where LOWER(name) = 'customers'
    )
CREATE TABLE CUSTOMERS
(
    id   int IDENTITY (1,1) PRIMARY KEY,
    name varchar(255) not null
);
go


<!-- deploy.sh -->



mvn clean package
az spring-cloud app deploy -n greetings-service --jar-path target/greetings-service-0.0.1-SNAPSHOT.jar 
