# Copyright © 2022, California Institute of Technology ("Caltech").
# U.S. Government sponsorship acknowledged.
#
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# • Redistributions of source code must retain the above copyright notice,
#   this list of conditions and the following disclaimer.
# • Redistributions must reproduce the above copyright notice, this list of
#   conditions and the following disclaimer in the documentation and/or other
#   materials provided with the distribution.
# • Neither the name of Caltech nor its operating division, the Jet Propulsion
#   Laboratory, nor the names of its contributors may be used to endorse or
#   promote products derived from this software without specific prior written
#   permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
#
# Dockerfile for the Registry API
# ===============================
#
# Depending on the build arguments, you could get a Dockerfile using local
# assets or one with built and released assets.
#
#
# Basis
# -----
#
# Normally we'd prefer Alpine Linux, but JDK 11 isn't available with it, so
# we go with a slim Debian. Debian's good too.

#FROM openjdk:17-slim
FROM tomcat:9.0.58-jdk17-openjdk-slim

# API JAR file
# ------------
#
# Provide a `--build-arg` to tell the image builder where the Registry API
# `.jar` file may found. This can be a file on the filesystem (sent as part
# of the build context) or a URL to a remote file.

ARG api_jar


# Layering
# --------
#
# Add the API JAR file and the libtcnative-1 package—which is needed for
# some reason.

ADD $api_jar /usr/local/registry-api-service/registry-api-service.jar

RUN : &&\
    apt-get update --quiet --yes &&\
    apt-get install --quiet --yes libtcnative-1 &&\
    apt-get install curl -y &&\
    apt-get autoclean --quiet --yes &&\
    rm --recursive --force /var/lib/apt/lists/* &&\
    :


# Image Morphology
# ----------------
#
# External context.

ARG SERVER_PORT=80
ARG SPRING_BOOT_APP_ARGS=" "
ARG JAVA_OPTS="-Xmx6144m -Xms512Mb"

WORKDIR /usr/local/registry-api-service
EXPOSE  ${SERVER_PORT}
CMD java $JAVA_OPT -jar /usr/local/registry-api-service/registry-api-service.jar \
    gov.nasa.pds.api.registry.SpringBootMain --server.port=${SERVER_PORT} ${SPRING_BOOT_APP_ARGS}


# Labels
# ------
#
# `org.label-schema` is deprecated, but no one can figure out whatever
# replaced it, so we'll use it here.

LABEL "org.label-schema.name" = "PDS Registry API"
LABEL "org.label-schema.description" = "Planetary Data System's Application Programmer's Interface for the Registry"
LABEL "org.label-schema.url" = "https://github.com/NASA-PDS/registry-api"
