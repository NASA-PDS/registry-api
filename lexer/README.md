# PDS API Search Query Lexer
This repo is contains the resources to create a java jar library to parse the PDS's API search queries.

it is based on ANTLR4 parser generator.

It is for example used in the [pds-api-service](https://github.com/NASA-PDS/pds-api-service), elasticsearch branch.

## Pre-requisites

- jdk 11
- apache-maven 3

# To build and deploy the lexer java library

    mvn clean antlr4:antlr4 deploy
    
    
# To update

The grammar definition is in the file: 

    /api-search-query-lexer/src/main/antlr4/gov/nasa/pds/api/engineering/lexer/Search.g4



    

