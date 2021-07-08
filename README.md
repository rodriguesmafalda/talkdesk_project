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

##### RestApi (Service)
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

##### Client

The Client should allow the reviewer to call all operations of the rest api without having to handle the connection by himself. There are no restrictions on the programming language for this component.

## The application

#### RestAPI 

The restApi was developed with [springBoot] and PostgreSQL database.
I use [Flyway] ind order to have version control of my database.


### Prerequisites
* [Docker]
* [Docker-compose]
* [Postman]




## Run the application

1. Clone the Github repository to your local machine
```sh
git clone https://github.com/rodriguesmafalda/talkdesk_project.git
```

2. Create a common network and run the application
```sh
docker network create call-service
docker-compose up -d
```


### Application call-service Cheat Sheet:

```shell
# Get the logs of call-service
docker-compose logs -f call-service
```


#### db hacks
docker exec -it <pod_name> psql -U calls

###### show databases
SELECT datname FROM pg_database;

##dump db
docker exec -it postgres pg_dump -U calls > backup.sql






[Docker]: <https://www.docker.com/get-started>
[Docker-compose]: <https://docs.docker.com/compose/install/>
[springBoot]: <https://spring.io/projects/spring-boot/>
[Postman]: <https://learning.postman.com/docs/getting-started/introduction/>
[Flyway]: <https://flywaydb.org/documentation/getstarted/>

