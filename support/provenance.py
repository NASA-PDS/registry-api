#! /usr/bin/env python3

import argparse
import collections
import json
import logging
from typing import Union

log = logging.getLogger('provenance')
import os
import requests
import sys
import urllib.parse

requests.packages.urllib3.disable_warnings()

HOST = collections.namedtuple('HOST', ['nodes', 'password', 'url', 'username',
                                       'verify'])


def _log_level(input: str) -> int:
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


def run(args: argparse.Namespace):
    configure_logging(filepath=args.log_file, log_level=args.log_level)

    log.info('starting CLI processing')
    host = HOST(args.cluster_nodes, args.password, args.base_URL, args.username,
                args.verify)
    provenance = troll_registry(host)
    updates = get_historic(provenance, args.reset)
    if updates: update_docs(host, updates)
    log.info('completed CLI processing')
    return


def get_historic(provenance: {str: str}, reset: bool) -> {str: str}:
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
            if reset or not provenance[lidvid]: history[lidvid] = lidvids[index]
            pass
        pass
    log.info('found %d products needing update of a %d full history of %d total products', len(history), count,
             len(provenance))
    return history


def troll_registry(host: HOST) -> {str: str}:
    log.info('start trolling')
    cluster = [node + ":registry" for node in host.nodes]
    key = 'ops:Provenance/ops:superseded_by'
    more_data = True
    path = ','.join(['registry'] + cluster) + '/_search?scroll=10m'
    provenance = {}
    query = {'query': {'bool': {'must_not': [
        {'term': {'ops:Tracking_Meta/ops:archive_status': 'staged'}}]}},
        '_source': {'includes': ['lidvid', key]},
        'size': 10000}
    while more_data:
        resp = requests.get(urllib.parse.urljoin(host.url, path),
                            auth=(host.username, host.password),
                            verify=host.verify, json=query)

        if resp.status_code == 200:
            data = resp.json()
            path = '_search/scroll'
            query = {'scroll': '10m', 'scroll_id': data['_scroll_id']}
            provenance.update({hit['_source']['lidvid']: hit['_source'].get(key, None) for hit in data['hits']['hits']})
            more_data = len(provenance) < data['hits']['total']['value']
        else:
            more_data = False
            log.error('Bad response code (%d): %s',
                      resp.status_code, resp.reason)
            sys.exit(-50)
        log.info('   progress: %d of %d (%d%%)', len(provenance),
                 data['hits']['total']['value'],
                 int(round(len(provenance) / data['hits']['total']['value'] * 100)))
        pass

    if 'scroll_id' in query:
        path = '_search/scroll/' + query['scroll_id']
        requests.delete(urllib.parse.urljoin(host.url, path),
                        auth=(host.username, host.password),
                        verify=host.verify)
        pass
    log.info('finished trolling')
    return provenance


def update_docs(host: HOST, history: {str: str}):
    log.info('Bulk update %d documents', len(history))
    bulk = []
    cluster = [node + ":registry" for node in host.nodes]
    headers = {'Content-Type': 'application/x-ndjson'}
    path = ','.join(['registry'] + cluster) + '/_bulk'
    for lidvid, supersede in history.items():
        bulk.append(json.dumps({'update': {'_id': lidvid}}))
        bulk.append(json.dumps({'doc': {'ops:Provenance/ops:superseded_by': supersede}}))
        pass
    bulk = '\n'.join(bulk) + '\n'
    response = requests.put(urllib.parse.urljoin(host.url, path),
                            auth=(host.username, host.password),
                            data=bulk, headers=headers, verify=host.verify)

    if response.status_code != 200:
        log.error('Bulk bad response code (%d): %s',
                  response.status_code, response.reason)
    else:
        response = response.json()
        if response['errors']:
            for item in response['items']:
                if 'error' in item:
                    log.error('update error (%d): %s', item['status'],
                              str(item['error']))
                    pass
                pass
        else:
            log.info('bulk update were successful')
    return


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

      provenance.py -b https://search-en-prod-di7dor7quy7qwv3husi2wt5tde.us-west-2.es.amazonaws.com -c naif-prod-ccs rms-prod sbnumd-prod-ccs geo-prod-ccs atm-prod-ccs sbnpsi-prod-ccs img-prod-ccs -u admin -p admin
    ''',
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument('-b', '--base-URL', required=True, type=str)
    ap.add_argument('-c', '--cluster-nodes', default=[], nargs='*',
                    help='names of opensearch cluster nodes that will be parsed by opensearch')
    ap.add_argument('-l', '--log-file', default=None, required=False,
                    help='file to write the log messages')
    ap.add_argument('-L', '--log-level', default='ERROR', required=False,
                    type=_log_level,
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

    run(args)
