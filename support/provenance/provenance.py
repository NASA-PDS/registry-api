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
# provenance
# ==========
#
# Determines if a particular document has been superseded by a more
# recent version, if upon which it has, sets the field 
# ops:Provenance/ops:superseded_by to the id of the superseding document.
#
# It is important to note that the document is updated, not any dependent
# index.
#

import argparse
import collections
import json
import logging
from typing import Union, List

log = logging.getLogger('provenance')
import requests
import urllib.parse

requests.packages.urllib3.disable_warnings()

HOST = collections.namedtuple('HOST', ['nodes', 'password', 'url', 'username',
                                       'verify'])


def parse_log_level(input: str) -> int:
    """Given a numeric or uppercase descriptive log level, return the associated int"""
    try:
        result = int(input)
    except ValueError:
        result = getattr(logging, input)
    return result


def _vid_as_tuple_of_int(lidvid: str):
    M, m = lidvid.split('::')[1].split('.')
    return (int(M), int(m))


def configure_logging(filepath: Union[str, None], log_level: int):
    logging.root.handlers = []
    handlers = [
        logging.StreamHandler()
    ]

    if filepath:
        handlers.append(logging.FileHandler(filepath))

    logging.basicConfig(
        level=log_level,
        format='%(asctime)s::%(levelname)s::%(message)s',
        handlers=handlers
    )


def run(
        cluster_nodes: List[str],  # TODO: confirm type
        base_url: str,
        username: str,
        password: str,
        verify_host_certs: bool = False,
        reset: bool = False,
        log_filepath: Union[str, None] = None,
        log_level: int = logging.INFO):
    configure_logging(filepath=log_filepath, log_level=log_level)

    log.info('starting CLI processing')

    host = HOST(cluster_nodes, password, base_url, username, verify_host_certs)

    provenance = trawl_registry(host)
    updates = get_historic(provenance, reset)

    if updates:
        update_docs(host, updates)

    log.info('completed CLI processing')


def get_historic(provenance: {str: str}, reset: bool) -> {str: str}:  # TODO: populate comment and rename for clarity
    log.info('starting search for history')

    log.info('   reduce lidvids to unique lids')
    lids = sorted({lidvid.split('::')[0] for lidvid in provenance})

    log.info('   aggregate lidvids into lid buckets')
    aggregates = {lid: [] for lid in lids}
    for lidvid in provenance: aggregates[lidvid.split('::')[0]].append(lidvid)

    log.info('   process those with history')
    count = 0
    history = {}
    for lidvids in filter(lambda l: 1 < len(l), aggregates.values()):
        count += len(lidvids)
        lidvids.sort(key=_vid_as_tuple_of_int, reverse=True)
        for index, lidvid in enumerate(lidvids[1:]):
            if reset or not provenance[lidvid]:
                history[lidvid] = lidvids[index]

    log.info(
        f'found {len(history)} products needing update of a {count} full history of {len(provenance)} total products')
    if log.isEnabledFor(logging.DEBUG):
        for lidvid in history.keys():
            log.debug(f'{lidvid}')

    return history


def trawl_registry(host: HOST) -> {str: str}:  # TODO: populate comment and rename for clarity
    log.info('start trawling')

    cluster = [node + ":registry" for node in host.nodes]
    key = 'ops:Provenance/ops:superseded_by'
    path = ','.join(['registry'] + cluster) + '/_search?scroll=10m'
    provenance = {}
    query = {'query': {'bool': {'must_not': [
        {'term': {'ops:Tracking_Meta/ops:archive_status': 'staged'}}]}},
        '_source': {'includes': ['lidvid', key]},
        'size': 10000}

    more_data_exists = True
    while more_data_exists:
        resp = requests.get(urllib.parse.urljoin(host.url, path),
                            auth=(host.username, host.password),
                            verify=host.verify, json=query)
        resp.raise_for_status()

        data = resp.json()
        path = '_search/scroll'
        query = {'scroll': '10m', 'scroll_id': data['_scroll_id']}
        provenance.update({hit['_source']['lidvid']: hit['_source'].get(key, None) for hit in data['hits']['hits']})
        more_data_exists = len(provenance) < data['hits']['total']['value']

        hits = data['hits']['total']['value']
        percent_hit = int(round(len(provenance) / hits * 100))
        log.info(f'   progress: {len(provenance)} of {hits} ({percent_hit}%)')

    if 'scroll_id' in query:
        path = '_search/scroll/' + query['scroll_id']
        requests.delete(urllib.parse.urljoin(host.url, path),
                        auth=(host.username, host.password),
                        verify=host.verify)

    log.info('finished trawling')

    return provenance


def update_docs(host: HOST, history: {str: str}):
    """Write provenance history updates to documents in db"""
    log.info('Bulk update %d documents', len(history))

    bulk_updates = []
    cluster = [node + ":registry" for node in host.nodes]
    headers = {'Content-Type': 'application/x-ndjson'}
    path = ','.join(['registry'] + cluster) + '/_bulk'

    for lidvid, supersede in history.items():
        bulk_updates.append(json.dumps({'update': {'_id': lidvid}}))
        bulk_updates.append(json.dumps({'doc': {'ops:Provenance/ops:superseded_by': supersede}}))

    bulk_data = '\n'.join(bulk_updates) + '\n'

    log.info(f'writing bulk update for {len(bulk_updates)} products...')
    response = requests.put(urllib.parse.urljoin(host.url, path),
                            auth=(host.username, host.password),
                            data=bulk_data, headers=headers, verify=host.verify)
    response.raise_for_status()

    response = response.json()
    if response['errors']:
        for item in response['items']:
            if 'error' in item:
                log.error('update error (%d): %s', item['status'],
                          str(item['error']))
    else:
        log.info('bulk updates were successful')


if __name__ == '__main__':
    ap = argparse.ArgumentParser(description='''Update the provenance of products with more than one VID

    The program sweeps through the registry index to find all the lidvids and existing provenance. It then builds up the updates necessary to mark the newly superseded products accordingly.
    ''',
                                 epilog='''EXAMPLES:

    - command for opensearch running in a container with the sockets published at 9200 for data ingested for full day March 11, 2020:

      provenance.py -b https://localhost:9200 -p admin -u admin

    - getting more help on availables arguments and what is expected:

      provenance.py --help

    - command for opensearch running in a cluster

      provenance.py -b https://search.us-west-2.es.amazonaws.com -c remote1 remote2 remote3 remote4 -u admin -p admin
    ''',
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument('-b', '--base-URL', required=True, type=str)
    ap.add_argument('-c', '--cluster-nodes', default=[], nargs='*',
                    help='names of opensearch cluster nodes that will be parsed by opensearch')
    ap.add_argument('-l', '--log-file', default=None, required=False,
                    help='file to write the log messages')
    ap.add_argument('-L', '--log-level', default='ERROR', required=False,
                    type=parse_log_level,
                    help='Python logging level as an int or string like INFO for logging.INFO [%(default)s]')
    ap.add_argument('-p', '--password', default=None, required=False,
                    help='password to login to opensearch leaving it blank if opensearch does not require login')
    ap.add_argument('-r', '--reset', action='store_true', default=False,
                    help='ignore existing provenance building it from scratch')
    ap.add_argument('-u', '--username', default=None, required=False,
                    help='username to login to opensearch leaving it blank if opensearch does not require login')
    ap.add_argument('-v', '--verify', action='store_true', default=False,
                    help='verify the host certificates')
    args = ap.parse_args()

    run(
        cluster_nodes=args.cluster_nodes,
        base_url=args.base_URL,
        username=args.username,
        password=args.password,
        verify_host_certs=args.verify,
        reset=args.reset,
        log_level=args.log_level,
        log_filepath=args.log_file)
