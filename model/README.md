# PDS-API-JAVALIB

Visit the software's website at https://nasa-pds.github.io/pds-api-javalib/

1. Stores the swagger.yml reference file for the PDS federated API (see https://app.swaggerhub.com/apis/PDS_APIs where the specification is edited by the PDS team)
2. provide the procedure and configuration to generate JAVA libraries used by API servers and clients implementation.


# Prerequisites

- JDK 11
- MAVEN

Note that GitHub Actions is used in this repository to automate testing, packaging, and deployment of releases to GitHub and the Sonatype Central Repostiory, OSSRH.

To deploy the generated java code on the maven artifcatory manually, you need a configuration in `~/.m2/settings.xml`:

```xml
    <server>
       <id>ossrh</id>
       <username>...</username>
       <password>...</password>
    </server>
```



# Procedures

Generate and deploy java standard API library:

    mvn clean install deploy


The code is deployed on maven artifactory: https://oss.sonatype.org

    
# Usage

Use the library in a project:

In a maven pom.xml:

```xml
<dependency>
    <groupId>gov.nasa.pds</groupId>
    <artifactId>api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

```

See full example on https://github.com/nasa-pds/pds-api-service
