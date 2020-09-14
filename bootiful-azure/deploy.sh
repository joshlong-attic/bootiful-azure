#!/usr/bin/env bash

mvn -DskipTests=true clean package
az spring-cloud app deploy \
	--verbose \
	--resource-group bootiful-resource-group \
	-n simple-microservice  \
	--jar-path target/bootiful-azure-0.0.1-SNAPSHOT.jar


