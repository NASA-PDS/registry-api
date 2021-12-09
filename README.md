# PDS API Search Query Lexer

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.5750532.svg)](https://doi.org/10.5281/zenodo.5750532)

This repository contains the resources to create a Java JAR library to parse the [Planetary Data System](https://pds.nasa.gov/)'s [API](https://github.com/NASA-PDS/pds-api) search queries.

It's based on the ANTLR4 parser generator. It is, for example, used in the [pds-api-service](https://github.com/NASA-PDS/pds-api-service)'s Elasticsearch branch.


## 🖥 Prerequisites

- JDK 11
- Apache Maven 3


## 👷‍♀️ Building and Deploying

Just run:

    mvn clean antlr4:antlr4 deploy
    
    
## 🆙 Updating

The grammar definition is in the file: 

    src/main/antlr4/gov/nasa/pds/api/engineering/lexer/Search.g4


## 👥 Contributing

Within the NASA Planetary Data System, we value the health of our community as much as the code. Towards that end, we ask that you read and practice what's described in these documents:

-   Our [contributor's guide](https://github.com/NASA-PDS/.github/blob/main/CONTRIBUTING.md) delineates the kinds of contributions we accept.
-   Our [code of conduct](https://github.com/NASA-PDS/.github/blob/main/CODE_OF_CONDUCT.md) outlines the standards of behavior we practice and expect by everyone who participates with our software.


## 📃 License

The project is licensed under the [Apache version 2](LICENSE.md) license.
