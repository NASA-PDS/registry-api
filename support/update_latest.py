#! /usr/bin/env python3

import argparse
import collections
import datetime
import json
import logging; log = logging.getLogger('update_latest')
import os
import pickle
import requests
import sys
import urllib.parse

requests.packages.urllib3.disable_warnings()

HOST = collections.namedtuple ('HOST', ['nodes', 'password', 'url', 'username',
                                        'verify'])

def _vid_as_tuple_of_int (lidvid:str):
    M,m = lidvid.split('::')[1].split('.')
    return (int(M),int(m))

def _datetime (input:str)->datetime.datetime:
    return datetime.datetime.fromisoformat (input)

def _log_level (input:str)->int:
    try: result = int(input)
    except ValueError: result = getattr (logging, input)
    return result

def cli():
    ap = argparse.ArgumentParser(description='''Update latest index from registry index

The program sweeps through the registry index to find all of "ops:Harvest_Info/ops:harvest_date_time" records between the begin time (-tb) and end time (-te) inclusive specified on the command line.''',
                                 epilog='''EXAMPLES:

- command for opensearch running in a container with the sockets published at 9200 for data ingested for full day March 11, 2020:
                                 
  update_latest.py -b https://localhost:9200 -p admin -u admin -tb 2020-03-011 -te 2020-03-12

or with full date time (both work the same)

  update_latest.py -b https://localhost:9200 -p admin -u admin -tb 2020-03-011T00:00:00 -te 2020-03-12T00:00:00


- getting more help on availables arguments and what is expected:

  update_latest.py --help

- command for opensearch running in a cluster

  update_latest.py -b https://search-en-prod-di7dor7quy7qwv3husi2wt5tde.us-west-2.es.amazonaws.com -c naif-prod-ccs rms-prod sbnumd-prod-ccs geo-prod-ccs atm-prod-ccs sbnpsi-prod-ccs img-prod-ccs -u admin -p admin -tb 2022-02-08 -te 2022-11-29
''',
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument ('-b', '--base-URL', required=True, type=str)
    ap.add_argument ('-c', '--cluster-nodes', default=[], nargs='*',
                     help='names of opensearch cluster nodes that will be parsed by opensearch')
    ap.add_argument ('-d', '--delete', action='store_true', default=False,
                     required=False, help='delete the latest index prior to processing registry index')
    ap.add_argument ('-l', '--log-file', default='/dev/stdout', required=False,
                     help='file to write the log messages [%(default)s]')
    ap.add_argument ('-L', '--log-level', default='ERROR', required=False,
                     type=_log_level, help='Python logging level as an int or string like INFO for logging.INFO [%(default)s]')
    ap.add_argument ('-p', '--password', default=None, required=False,
                     help='password to login to opensearch leaving it blank if opensearch does not require login')
    ap.add_argument ('-tb', '--time-begin', required=True, type=_datetime,
                     help='inclusive start datetime in the ISO format yyyy-mm-ddThh:mm:ss like 2020-12-25T14:37:41')
    ap.add_argument ('-te', '--time-end', required=True, type=_datetime,
                     help='inclusive end datetime in the ISO format yyyy-mm-ddThh:mm:ss like 2020-12-25T14:37:41')
    ap.add_argument ('-u', '--username', default=None, required=False,
                     help='username to login to opensearch leaving it blank if opensearch does not require login')
    ap.add_argument ('-v', '--verify', action='store_true', default=False,
                     help='verify the host certificates')
    args = ap.parse_args()
    logging.basicConfig(filename=args.log_file, level=args.log_level,
                        format='%(asctime)s::%(levelname)s::%(message)s')
    log.info ('starting CLI processing')
    host = HOST(args.cluster_nodes, args.password, args.base_URL, args.username,
                args.verify)
    latest = get_index(host, 'latest')
    registry = get_index(host, 'registry')

    if not registry:
        log.error ('registry index could not be found')
        sys.exit (-10)
        pass

    if latest and args.delete: latest = del_index (host, 'latest')
    if not latest: latest = create_latest_index(host, registry)

    if latest != registry:
        log.error ('registry index does not match the latest index')
        sys.exit (-20)
        pass

    lidvids = troll_registry (host, args.time_begin, args.time_end)
    log.info ('reduce lidvids to unique lids')
    unique_lids = sorted({lidvid.split('::')[0] for lidvid in lidvids})
    lidvids = unique_lidvids (unique_lids, lidvids)[0]
    with open ('/home/niessner/Scratch/ingested.pkl','bw') as file: pickle.dump ((lids, lidvids), file)
    latest_lidvids = get_latest (host, unique_lids)
    latest_lidvids,discard = unique_lidvids (unique_lids, latest_lidvids)

    if discard:
        log.warning ('latest index contained more versions than latest')
        clean_latest (host, discard)
        pass

    reindex_lidvids,discard = merge_indices (lidvids, latest_lidvids)
    clean_latest (host, discard)
    reindex (host, reindex_lidvids)
    return

def clean_latest (host:HOST, lidvids:[str]):
    if lidvids:
        log.info ('cleaning the latest index')
        bulk = '\n'.join ([json.dumps({'delete':{'_id':lidvid}}) for lidvid in lidvids]) + '\n'
        headers = {'Content-Type': 'application/x-ndjson'}
        response = requests.put (urllib.parse.urljoin (host.url,'latest/_bulk'),
                                 auth=(host.username, host.password),
                                 data=bulk, headers=headers,
                                 verify=host.verify)

        if response.status_code != 200:
            log.error ('could not clean latest index because of error (%d): %s',
                       response.status_code, response,reason)
        else:
            response = response.json()
            if response['errors']:
                for item in response['items']:
                    if 'error' in item:
                        log.error('deletion error (%d): %s', item['status'],
                                  str(item['error']))
                        pass
                    pass
            else: log,info ('cleaned latest index')
        pass
    return

def create_latest_index (host:HOST, config:{}):
    result = {}
    response = requests.put (urllib.parse.urljoin (host.url, 'latest'),
                             auth=(host.username, host.password),
                             json=config, verify=host.verify)

    if response.status_code == 200: result.update (get_index (host, 'latest'))

    return result

def del_index (host:HOST, name:str):
    response = requests.delete (urllib.parse.urljoin (host.url, name),
                                auth=(host.username, host.password),
                                verify=host.verify)

    if response.status_code != 200:
        log.error ('could not delete index %s because of error (%d): %s',
                   name, response.status_code, response.reason)
    return

def get_index (host:HOST, name:str)->{}:
    result = {}
    response = requests.get (urllib.parse.urljoin (host.url, name),
                             auth=(host.username, host.password),
                             verify=host.verify)

    if response.status_code == 200:
        data = response.json()

        if name in data:
            index = data[name]
            del index['settings']['index']['creation_date']
            del index['settings']['index']['provided_name']
            del index['settings']['index']['uuid']
            del index['settings']['index']['version']
            result.update (index)
        else: log.error ('%s not in the returned index configuration', name)
    else: log.error ('could not find the index %s because: %s',
                     name, response.content)
    return result

def get_latest (host:HOST, lids:[str])->[str]:
    log.info ('finding lidvids for %d lids', len(lids))
    lidvids = []
    more_data = True
    path = 'latest/_search?scroll=10m'
    query = {'query':{'terms':{'lid':lids}},
             '_source':{'includes':['lidvid']},
             'size':10000}

    while more_data:
        response = requests.get (urllib.parse.urljoin (host.url, path),
                                 auth=(host.username, host.password),
                                 json=query, verify=host.verify)

        if response.status_code == 200:
            data = response.json()
            path = '_search/scroll'
            query = {'scroll':'10m','scroll_id':data['_scroll_id']}
            lidvids.extend ([hit['_source']['lidvid']
                             for hit in data['hits']['hits']])
            more_data = len(lidvids) < data['hits']['total']['value']
        else:
            more_data = False
            log.error ('Bad response code (%d): %s',
                       resp.status_code, resp.reason)
            sys.exit(-40)

        if lidvids:
            log.info ('   progress: %d of %d (%d%%)', len(lidvids),
                      data['hits']['total']['value'],
                      int(round(len(lidvids)/data['hits']['total']['value']*100)))
        else: more_data = False
        pass

    if 'scroll_id' in query:
        path = '_search/scroll/' + query['scroll_id']
        requests.delete (urllib.parse.urljoin (host.url, path),
                         auth=(host.username, host.password),
                         verify=host.verify)
        pass

    log.info ('found %d lidvids for %d lids', len(lidvids), len(lids))
    return lidvids

def merge_indices (new:{str:str}, latest:{str,str})->([str],[str]):
    log.info ('starting merge process')
    discard,lidvids = [],[]
    for lid,lidvid in new.items():
        known = latest[lid]
        if known:
            if _vid_as_tuple_of_int(known) <= _vid_as_tuple_of_int(lidvid):
                discard.append (known)
                lidvid.append (lidvid)
                pass
            pass
        else: lidvids.append (lidvid)
    log.info ('completed merge with %d new lidvids and %d updated',
              len(lidvids) - len(discard), len(discard))
    return lidvids,discard

def reindex (host:HOST, lidvids:[str]):
    log.info ('reindex with %d lidvids', len(lidvids))
    cluster = [node + ":registry" for node in host.nodes]
    path = ','.join (cluster) + '/_reindex'
    query = {'dest':{'index':'latest'},
             'source':{'index':'registry','query':{'terms':{'lidvid':lidvids}}}}

    response = requests.post (urllib.parse.urljoin (host.url, path),
                              auth=(host.username, host.password),
                              verify=host.verify, json=query)

    if response.status_code != 200:
        log.error ('could not reindex because of error (%d): %s',
                   response.status_code, response.reason)
        pass
    log.info ('reindex of latest is complete')
    return

def troll_registry (host:HOST, begin:datetime.datetime, end:datetime.datetime):
    log.info ('start trolling')
    one = datetime.timedelta(seconds=1)
    cluster = [node + ":registry" for node in host.nodes]
    lidvids = []
    more_data = True
    path = ','.join (['registry'] + cluster) + '/_search?scroll=10m'
    query = { 'query':{'range':{'ops:Harvest_Info/ops:harvest_date_time':
                                {'gte':(begin).isoformat(sep=' ',
                                                         timespec='seconds'),
                                 'lte':(end+one).isoformat(sep=' ',
                                                           timespec='seconds'),
                                 'format':'yyyy-MM-dd HH:mm:ss'}
                      }},
              '_source':{'includes':['lidvid']},
              'size':10000 }
    while more_data:
        resp = requests.get (urllib.parse.urljoin (host.url, path),
                             auth=(host.username, host.password),
                             verify=host.verify, json=query)

        if resp.status_code == 200:
            data = resp.json()
            path = '_search/scroll'
            query = {'scroll':'10m','scroll_id':data['_scroll_id']}
            lidvids.extend({hit['_source']['lidvid']
                            for hit in data['hits']['hits']})
            more_data = len(lidvids) < data['hits']['total']['value']
        else:
            more_data = False
            log.error ('Bad response code (%d): %s',
                       resp.status_code, resp.reason)
            sys.exit(-50)
        log.info ('   progress: %d of %d (%d%%)', len(lidvids),
                  data['hits']['total']['value'],
                  int(round(len(lidvids)/data['hits']['total']['value']*100)))
        pass

    if 'scroll_id' in query:
        path = '_search/scroll/' + query['scroll_id']
        requests.delete (urllib.parse.urljoin (host.url, path),
                         auth=(host.username, host.password),
                         verify=host.verify)
        pass
    log.info ('finished trolling')
    return lidvids

def unique_lidvids (lids:{str}, lidvids:{str})->{str}:
    log.info ('latest version lidvid given unique lids (%d) from %d lidvids',
              len(lids), len(lidvids))
    aggregates = {lid:[] for lid in lids}
    discard = []
    result = {}
    for lidvid in lidvids:
        lid = lidvid.split('::')[0]
        aggregates[lid].append (lidvid)
        pass
    for lid,subset in aggregates.items():
        ordered = sorted(subset, key=_vid_as_tuple_of_int, reverse=True)
        discard.extend (ordered[1:])
        result[lid] = ordered[0] if ordered else None
        pass
    log.info ('ignoring (not latest version) %d lidvids', len(discard))
    return result,discard

if __name__ == '__main__': cli()
