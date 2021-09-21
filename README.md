# REGISTRY-API-SERVICE

## Overview

This is the PDS API implementation which provides access to the PDS registries (see https://github.com/NASA-PDS/pds-registry-app). When operational the API service will be part of the registry application. It implements a very simple collections and product search end-point complying with the specification (see https://app.swaggerhub.com/organizations/PDS_APIs).

For more information, please visit https://nasa-pds.github.io/registry-api-service/


## Prerequisites

This software requires open jdk 11.

## Administrator

Get the latest stable release https://github.com/NASA-PDS/registry-api-service/releases

Download the zip or tar.gz file.

Follow instructions in README.txt in the decompressed folder


## Developers

### Deployment

Get a development release by cloning the current repository.

If needed change server port and elasticSearch parameters in `src/main/resources/application.properties`.
Note, the registry index in elasticSearch is hard-coded. It need to be `registry`.

    mvn clean
    mvn install
    mvn spring-boot:run
    
    
### Usage

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

2. Download the latest request collection from [postman collection](https://raw.githubusercontent.com/NASA-PDS/registry-api-service/main/src/test/resources/postman_collection.json)

3. Then you import the collection file, on the top-left: Import > File

4. You also need to set an environment variable the base url of the demo API:

    baseUrl = https://pds-gamma.jpl.nasa.gov/api

See guidelines on https://learning.postman.com/docs/sending-requests/variables/


5. You can browse the collection and run the requests one by one or run the full collection at once.

    
# Docker

## Prerequisite

Have a registry deployed, for example with docker as described in https://github.com/NASA-PDS/pds-registry-app/blob/main/README.md#docker 

## Build

### Local git version

```
docker image build --build-arg version=$(git rev-parse HEAD) \
             --file Dockerfile.local \
             --tag registry-api-service:$(git rev-parse HEAD) \
             .
```

## Run


The `/absolute/path/to/my/properties.file` should be configured to access your registry's elasticsearch instance with: `es:9200`.


```
docker container run --name registry-api-service \
           --network pds \
           --publish 8080:8080 \
           --rm \
           --volume /absolute/path/to/my/properties.file:/usr/local/registry-api-service-$(git rev-parse HEAD)/src/main/resources/application.properties \
           registry-api-service:$(git rev-parse HEAD)
```

### Develop

1. build local git version
2. run image as below and restart when code has changed and want to test again  
```
docker container run --interactive \
           --name registry-api-service \
           --network pds \
           --publish 8080:8080 \
           --rm \
           --tty \
           --user $UID \
           --volume $(realpath ${PWD}):/usr/local/registry-api-service-$(git rev-parse HEAD) \
           registry-api-service:$(git rev-parse HEAD) bash
```
3. Run maven as desired such as `mvn spring-boot:run` to run your local copy or `mvn install` to build it. Rinse and repeat as needed.
