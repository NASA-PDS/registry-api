# Registry API

[![DOI](https://zenodo.org/badge/444555977.svg)](https://zenodo.org/badge/latestdoi/444555977) [![🤪 Unstable integration & delivery](https://github.com/NASA-PDS/registry-api/actions/workflows/unstable-cicd.yaml/badge.svg)](https://github.com/NASA-PDS/registry-api/actions/workflows/unstable-cicd.yaml) [![😌 Stable integration & delivery](https://github.com/NASA-PDS/registry-api/actions/workflows/stable-cicd.yaml/badge.svg)](https://github.com/NASA-PDS/registry-api/actions/workflows/stable-cicd.yaml)

This repository implements the [search API v1.0.0-SNAPSHOT](https://nasa-pds.github.io/pds-api/specifications.html) for the PDS registry.


It is composed with the following subcomponents:
- lexer: parse the API request queries (q parameter), based on antlr4 grammar
- model: library end-point controller definition and response objects generated from the openAPI specification (see https://github.com/NASA-PDS/pds-api/)
- service: the API service, a spring-boot application


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

### Running the API

#### Prerequisites

To build and run the application you need:

- jdk 17
- maven

Additionally, harvested data will only be picked up correctly by the API if all of the following are true:
 - the data has been given a status of "archived" using registry-mgr
 - the registry-sweepers have been executed to update required metadata, see https://github.com/NASA-PDS/registry-sweepers

There are two approaches to running a local development instance of the API

#### [Option 1] Non-Containerized (useful for breakpoint debugging)

1. [Deploy an instance of the registry docker-compose](https://github.com/NASA-PDS/registry/tree/main/docker#readme)
2. Kill the existing API container
      
       docker kill docker-registry-api-1

3. Temporarily disable certificate verification by making the following modification to [application.properties](./service/src/main/resources/application.properties)

       openSearch.sslCertificateCNVerification=false
       
4. If using the docker-compose setup (step 1), only one discipline node tenant is configured, it is 'geo', check this line:


       openSearch.disciplineNodes=geo


5. Build the application

       mvn clean install

6. Start the application 

       cd service
       mvn spring-boot:run
       

The API will now be accessible on (by default) https://localhost:8080
       
7. Specific configuration profile: if you run the application in a specific environment you can define a dedicated `application.properties`, for example `application-dev.properties` that does not need to be commited on git. Launch it as follow:


       mvn -Dspring-boot.run.profiles=dev spring-boot:run
     

    
#### [Option 2] Build a development docker image

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

### Testing Requirements

**IMPORTANT:** As a developer, you are **required** to add integration tests to the Postman test suite for:
- Each new feature or requirement
- Each bug fix
- Any changes to existing API behavior

### Integration Testing Guide

Integration tests are maintained in the `registry` repository as Postman collections. For complete instructions on creating, running, and submitting integration tests, see:

**[Integration Testing Guide](https://nasa-pds.github.io/registry/developer/integration-testing.html)**

All test updates must be submitted as pull requests to the [`registry` repository](https://github.com/NASA-PDS/registry).
    
    
