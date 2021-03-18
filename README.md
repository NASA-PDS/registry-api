# PDS-API-SERVICE

## Overview

This is the PDS API implementation which provides access to the PDS registries (see https://github.com/NASA-PDS/pds-registry-app). When operational the API service will be part of the registry application. 

It implements a very simple collections and product search end-point complying with the specification (see https://app.swaggerhub.com/organizations/PDS_APIs)


## Deployment

If needed change server port and elasticSearch parameters in `src/main/resources/applications.properties`.
Note, the registry index in elasticSearch is hard-coded. It need to be `registry`.

    mvn clean
    mvn install
    mvn spring-boot:run
    
    
## Usage

Go to rest api documentation:

    http://localhost:8080/
    
    
Test the simple collection end-point:

    http://localhost:8080/collections
    
## Demo server

A demo server is deployed on https://pds-gamma.jpl.nasa.gov/api/swagger-ui.html

You can browse self documented server or you can use postman to test it.

## Use postman

Postman is a tool which enable to manage collection of HTTP API requests, share and run them.

1. Install postman desktop application https://www.postman.com/downloads/

2. Download the latest request collection from [postman collection](https://raw.githubusercontent.com/NASA-PDS/registry-api-service/master/src/test/resources/postman_collection.json)

3. Then you import the collection file, on the top-left: Import > File

4. You also need to set an environment variable the base url of the demo API:

    baseUrl = https://pds-gamma.jpl.nasa.gov/api

See guidelines on https://learning.postman.com/docs/sending-requests/variables/


5. You can browse the collection and run the requests one by one or run the full collection at once.

    
# Docker

## Build

### Local git version

```
docker build --build-arg version=$(git rev-parse HEAD) \
             --file Dockerfile.local \
             --tag registry-api-service:$(git rev-parse HEAD) \
             .
```

## Run

```
docker run --name registry-api-service \
           --network pds \
           --publish 8080:8080 \
           --rm \
           --volume /absolute/path/to/my/properties.file:/usr/local/registry-api-service-$(git rev-parse HEAD)/src/main/resources/application.properties \
           registry-api-service:$(git rev-parse HEAD)
```

### Develop

1. build local git version
1. run image as below and restart when code has changed and want to test again  
```
docker run --interactive \
           --name registry-api-service \
           --network pds \
           --publish 8080:8080 \
           --rm \
           --tty \
           --user $UID \
           --volume $(realpath ${PWD}):/usr/local/registry-api-service-$(git rev-parse HEAD) \
           registry-api-service:$(git rev-parse HEAD) bash
```
1. Run maven as desired such as `mvn spring-boot:run` to run your local copy or `mvn install` to build it. Rinse and repeat as needed.
