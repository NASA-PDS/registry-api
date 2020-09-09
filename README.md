# PDS-API-SERVICE

## Overview

This project is a template implementation of the PDS federated API using the standard libraries automatically generated from the swagger definition of the API.

It implements a very simple collections end-point complying with the specification (see https://app.swaggerhub.com/organizations/PDS_APIs)


## Deployment



    mvn clean
    mvn install
    mvn spring-boot:run -Dserver.port=8080
    
    
## Usage

Go to rest api documentation:

    http://localhost:8080/
    
    
Test the simple collection end-point:

    http://localhost:8080/collections
    

