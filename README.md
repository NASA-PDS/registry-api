# PDS-API-SERVICE

## Overview

This project is a template implementation of the PDS federated API using the standard libraries automatically generated from the swagger definition of the API (see repository https://github.com/nasa-pds/pds-api-core/).

It implements a very simple collections end-point complying with the specification (see https://app.swaggerhub.com/organizations/PDS_APIs)


## Deployment

If needed change port in `src/main/resources/applications.properties`


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

Install postman desktop application https://www.postman.com/downloads/

Download the latest request collection from TBD

Then you import the collection file, on the top-left: Import > File

You also need to set an environment variable setting the base url of the demo API:

<screenshots>


You can browse the collection and run the requests one by one or run the full collection at once.

    

