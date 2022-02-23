# Registry API

This repository implements the [search API v1.0.0-SNAPSHOT](https://nasa-pds.github.io/pds-api/specifications.html) for the PDS registry.


It is composed with the following subcomponents:
- lexer: parse the API request queries (q parameter), based on antlr4 grammar
- model: library end-point controller definition and response objects generated from the openAPI specification (see https://github.com/NASA-PDS/pds-api/)
- api: the API service, a spring-boot application

## Prerequisites:

### Build

To build and run the application you need:

- jdk 11
- maven


### Run and Test

For the API to work, you also need ElasticSearch/OpenSearch with some test data loaded in it.

Based on `docker` you can easily start all the prerequisites as configured in the `registry` repository. This repository is also useful to run the [integration tests](#Tests):


    git clone https://github.com/NASA-PDS/registry.git
    

Start the prerequisites:

    docker compose --profile=dev-api up
    
    

## Developers

Builds the application:

    mvn clean install
    
    
## Start the application


    cd service
    mvn sprint-boot:run


## Tests

Integration test are maintained in postman.

In the `registry` project:

    npm install newman
    newman run docker/postman/postman_collection.json --env-var baseUrl=http://localhost:8080
    
 Important note: As a developer you are asked to complete the postman test suite according to the new feature you are developing. Do a pull request in the `registry` project to submit the updates.
    


