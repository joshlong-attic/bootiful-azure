#!/usr/bin/env bash

mvn clean package
az spring-cloud app deploy -n greetings-service --jar-path target/greetings-service-0.0.1-SNAPSHOT.jar
