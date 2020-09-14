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