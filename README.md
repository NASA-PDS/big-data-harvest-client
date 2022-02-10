# Harvest CLI

Command-line-interface client application for submitting asynchronous jobs (commands) to the 
cluster of Crawler and Harvest servers.
This application is different from the standalone Harvest Tool.
It has to be used with other components, such as RabbitMQ message broker, Harvest Server and Harvest Client 
to enable performant ingestion of large data sets into PDS Registry.

## Build
This is a Java application. You need Java 11 JDK and Maven to build it.
To create a binary distribution (ZIP and TGZ archives) run the following maven command:

```
mvn package
``` 

## Documentation

* The latest documentation, including architecture overview, installation, and operation of the software
is available in https://github.com/NASA-PDS/registry-harvest-service project.

* For more information about running all PDS Registry components in Docker see
https://github.com/NASA-PDS/registry
