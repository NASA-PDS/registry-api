# PDS-API-BASE

Contains the swagger.yml reference files and the procedures to generate the client and server stubs  in JAVA


# Prerequisite

- JDDK 1.8
- MAVEN


# Procedure

    mvn clean
    mvn generate-sources
    mvn install
    mvn spring-boot:run -Dserver.port=8080
    

# Test server stub


   http://localhost:8080/
