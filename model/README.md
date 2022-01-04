# PDS-API-BASE

1. Stores the swagger.yml reference file for the PDS federated API (see https://app.swaggerhub.com/apis/PDS_APIs where the specification is edited by the PDS team)
2. provide the procedure and configuration to generate JAVA libraries used by API servers and clients implementation.


# Prerequisites

- JDK 1.8
- MAVEN

To deploy the generated java code on the maven artifcatory you need a configuration in ~/.m2/Settings.xml

    <server>
       <id>ossrh</id>
       <username>...</username>
       <password>...</password>
    </server>



# Procedures

Generate and deploy java standard API library:

    mvn clean install deploy


The code is deployed on maven artifactory: https://oss.sonatype.org

    
# Usage

Use the library in a project:

In a maven pom.xml:

    	<dependency>
		  <groupId>gov.nasa.pds</groupId>
		  <artifactId>api</artifactId>
		  <version>0.0.1-SNAPSHOT</version>
	</dependency>

See full example on https://github.com/nasa-pds/pds-api-service




