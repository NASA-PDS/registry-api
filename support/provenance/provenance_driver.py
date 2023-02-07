#! /usr/bin/env python3

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
# Python driver for provenance
# ============================
#
# This script is provided to support the scheduled execution of PDS Registry 
# Provenance, typically in AWS via Event Bridge and ECS/Fargate.
#
# This script makes the following assumptions for its run-time:
#
# - The EN (i.e. primary) OpenSearch endpoint is provided in the environment
#   variable OPENSEARCH_ENDPOINT
# - The username/password is provided as a JSON key/value in the environment
#   variable PROV_CREDENTIALS
# - The remotes available through cross cluster search to be processed are 
#   provided as a list of JSON lists - each list used in a single
#   execution of provenance - in the environment variable PROV_REMOTES.
#   If this variable is empty or not defined, provenance is run without
#   speciyfing remotes and only the OPENSEARCH_ENDPOINT is processed.
# - The directory containing the provenance.py file is in PATH and is 
#   directly executable.
#
#

import os
import json

opensearch_endpoint = json.loads(os.environ.get("OPENSEARCH_ENDPOINT"))

username = None
pass = None
provCredentials = json.loads(os.environ.get("PROV_CREDENTIALS"))
if provCredentials is not None:
    username = provCredentials.keys(()[0]
    pass = provCredentials[username]

remotesLists = None
remotesStr = os.environ.get("PROV_ENDPOINTS")
if remotesStr is not None:
    remotesLists = json.loads(remotesStr)

command = f'provenance.py -b {opensearch_endpoint} -l provenance.log -L DEBUG '
if username is not None:
    command += f'-u {username} -p {pass} '

result = 0
lastCommand = ""
if remotesList is None:
    lastCommand = command
    result = os.system(command)
else:
    for remoteList in remotesList:
        lastCommand = command + f'-c {remoteList.join(",")}'
        result = os.sytem(lastCommanA)d
        if result != 0: break

if result != 0:
     print(f'Execution failure on command {lastCommand}')
else:
     print('Execution completed successfully.')
