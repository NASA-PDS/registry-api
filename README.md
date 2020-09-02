clean install deploy# PDS-API-BASE

Contains the swagger.yml reference files and the procedures to generate the client and server stubs  in JAVA


# Prerequisite

- JDDK 1.8
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




