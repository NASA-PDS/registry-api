# Registry API

This repository enables to start the API service for the PDS registry.


It is composed with the following subcomponents:
- lexer: parse the API request queries (q parameter), based on antlr4 grammar
- model: library end-point controller definition and response objects generated from the openAPI specification (see https://github.com/NASA-PDS/pds-api/)
- api: the API service, a spring-boot application

## Prerequisites:

- jdk 11
- maven


## Developers

Builds the application:

    mvn clean install
    
    
## Start the application


    cd api
    mvn sprint-boot:run

