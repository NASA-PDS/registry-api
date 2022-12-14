# Registry API

This repository implements the [search API v1.0.0-SNAPSHOT](https://nasa-pds.github.io/pds-api/specifications.html) for the PDS registry.


It is composed with the following subcomponents:
- lexer: parse the API request queries (q parameter), based on antlr4 grammar
- model: library end-point controller definition and response objects generated from the openAPI specification (see https://github.com/NASA-PDS/pds-api/)
- api: the API service, a spring-boot application


## Prerequisites

For the API to work, you also need ElasticSearch/OpenSearch with some test data loaded in it.

Based on `docker` you can easily start all the prerequisites as configured in the `registry` repository. This repository is also useful to run the [integration tests](#Tests):


    git clone https://github.com/NASA-PDS/registry.git
    

Start the prerequisites by following the [Quick Start Guide](https://github.com/NASA-PDS/registry/tree/main/docker#-quick-start-guide---with-default-configurations)


## Start the application from a released package

Get the latest stable release https://github.com/NASA-PDS/registry-api/releases

Download the zip or tar.gz 'registry-api-service-1.0.0-bin' file.

Follow instructions in README.txt in the decompressed folder    


## Developers

### Prerequisites

To build and run the application you need:

- jdk 11
- maven

### Build

Builds the application:

    mvn clean install
    
    
## Start the application


    cd service
    mvn spring-boot:run
    
## Build a development docker image

Your local docker image will be used in the integration deployment described below.

    mvn spring-boot:build-image
    

## View Swagger UI

Go to http://localhost:8080


## Integration deployment

You can deploy the registry-api together with all other components of the registry (harvest, opensearch, ...) and reference datasets.

Clone the `registry` repository, and launch the docker compose script as described in https://github.com/NASA-PDS/registry/tree/main/docker

For example, launch:

    docker compose --profile int-registry-batch-loader up

The integration tests will be automatically applied. Check the results, update/complete them as necessary


## Tests

**Important note:** As a developer you are asked to complete the postman test suite according to the new feature you are developing. Do a pull request in the `registry` project to submit the updates.

Integration test are maintained in postman.

### Edit/Run of the integration tests in postman GUI

Install the postman desktop, from https://www.postman.com/downloads/

Download and open the test suite found in https://github.com/NASA-PDS/registry/tree/main/docker/postman

### Run the integration tests in command line

In the `registry` project.

Launch the test in command line:

    npm install newman
    newman run docker/postman/postman_collection.json --env-var baseUrl=http://localhost:8080
    
    


