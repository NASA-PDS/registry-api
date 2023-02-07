# Copyright © 2023, California Institute of Technology ("Caltech").
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
# Dockerfile for PDS Registry Provenance
# ======================================
#
# Docker command is to run the provenance_driver.py script which drives the
# provenance process for scheduled execution via AWS Event Bridge and AWS
# ECS/Fargate. Refer to the script for its run-time assumptions under
# which is it executed in the AWS environment.
#
# Basis
# -----
#
# We use a slim python-enabled base image.
FROM python:3.10-slim-bullseye

ARG GIT_BRANCH=main

RUN apt-get update \
 && apt-get install -y git \
 && cd /tmp \
 && git clone --single-branch --branch $GIT_BRANCH --depth 1 https://github.com/NASA-PDS/registry-api \
 && cp registry-api/support/provenance/provenance.py /usr/local/bin \
 && cp registry-api/support/provenance/provenance_driver.py /usr/local/bin \
 && chmod -R u+x registry-api \
 && rm -r registry-api

CMD ["/usr/local/bin/provenance_driver.py"]
