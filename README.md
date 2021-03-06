# Talkdesk Challenge
A service to manage calls from Call service.

## Description
The main objective of this challenge is to implement a service to manage a specific resource: Calls. The Call resource represents a phone call between two numbers with the following attributes:

- Caller Number: the phone number of the caller.
- Callee Number: the phone number of the callee.
- Start Timestamp: start timestamp of the call.
- End Timestamp: end timestamp of the call.
- Type: Inbound or Outbound.
- Status: OnCall or EndedCall

### RestApi (Service)
This Rest API should be able to manage and persist the Call resource, providing the following operations::

* Create Calls (one or more).
* Delete Call.
* Get all Calls using pagination and be able to filter by Type.
* Get statistics (the response to this operation should have the values aggregate by day, returning all days with calls):
    * Total call duration by type.
    * Total number of calls.
    * Number of calls by Caller Number.
    * Number of calls by Callee Number.
    * Total call cost using the following rules:
        * Outbound calls cost 0.05 per minute after the first 5 minutes. The first 5 minutes cost 0.10.
        * Inbound calls are free.

### Client

The Client should allow the reviewer to call all operations of the rest api without having to handle the connection by himself. There are no restrictions on the programming language for this component.

## The application

### RestAPI 

The restApi was developed with [SpringBoot] and [PostgreSQL] database.
Additionally, this application uses [Flyway] to create a database version control. 

### Client

The client implementation is done through [Postman] requests.
This client consists of the following requests:
* **Get Calls:** Displays all calls using pagination and is possible to filter by Type.
* **Create Call:** Creates one call.
* **Create Calls:** Creates more than one call.
* **End Call:** Ends the call.
* **Delete Call:** Deletes the call information in the database.
* **Get Statistics:** Displays all call information aggregated by day such as total call duration by type,
 total number of calls, number of calls by caller number, number of calls by callee number and total call cost.
 * **Change Log Level:** Change the application logging level


### Prerequisites
* [Docker]
* [Docker-compose]
* [Postman]


## Run the application

1. Run the application
```sh
docker-compose up -d --build
```

2. Stop and removing the application
```sh
docker-compose down
```


## Interact with application

You can use [Postman] to make requests to the application by importing the file: **talkdesk_challenge.postman_collection.json**

Also, this application have an integration with [OpenApi] and [SwaggerUi]. Because of that is possible 
to see the documentation by the endpoint:

```sh
http://localhost:8080/v1/api-docs/
```

And interact with the application with the endpoint:

```sh
http://localhost:8080/calls-ui.html
```



#### Application call-service Cheat Sheet:

```shell
# Get the logs of call-service
docker-compose logs -f call-service

# Change logging level
curl -X "POST" "http://localhost:8080/actuator/loggers/talkdesk.mafalda.calls" \
     -H "Content-Type: application/json; charset=utf-8" \
     -d $'{
  "configuredLevel": "DEBUG"
}'  
```


[SpringBoot]: <https://spring.io/projects/spring-boot/>
[PostgreSQL]: https://www.postgresql.org/
[Flyway]: <https://flywaydb.org/documentation/getstarted/>
[Docker]: <https://www.docker.com/get-started>
[Docker-compose]: <https://docs.docker.com/compose/install/>
[Postman]: <https://learning.postman.com/docs/getting-started/introduction/>
[OpenApi]: https://swagger.io/specification/
[SwaggerUi]: https://swagger.io/tools/swagger-ui/


