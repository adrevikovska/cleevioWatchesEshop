[![CircleCI](https://circleci.com/gh/adrevikovska/cleevioWatchesEshop.svg?style=shield&circle-token=135bbaab6690e0bacea43c96efdfd00be88c225c)](https://circleci.com/gh/adrevikovska/cleevioWatchesEshop)

# Cleevio Watches Eshop
Hello! Welcome to my implementation of the Cleevio backend interview task for Watches Eshop.

The project is built with Spring Boot 2 framework using Java 11. Gradle is used as a build tool and data are stored in
the PostgreSQL database via JPA and Hibernate.

All CRUD operations are exposed through REST API implementation including HATEOAS and Swagger OpenAPI 3.0 documentation.
REST API also includes JSON Merge patch implementation of RFC 7386 standard. REST API accepts both JSON and XML requests
configurable through Content-Type and Accept headers.

Code is partly covered with JUnit 5 tests. Lombok is also included in some parts of the code.
There is also Google checkstyle coverage incorporated into the build of the project.

It's possible to run the application together with the PostgreSQL database in a docker container.

# How to run the application

1. Inside the root directory of the project run the `./gradlew dockerBuildImage` command.
2. Then run command `docker-compose up`
     * Application should now be exposed on `localhost:8080`
     * DB should be running on `localhost:5432`
       with credentials set to username: `postgres` and password set to: `welcome1`

The running application should also have Swagger documentation available on `localhost:8080/api-docs` endpoint.

Postman collection `cleevio-test-requests.http` is included in the root directory for testing purposes.

Circle CI is added is hooked to the project for CI purposes.

Thank you and enjoy :slightly_smiling_face:!
