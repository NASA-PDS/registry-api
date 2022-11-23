#! /usr/bin/env python3

import argparse
import collections
import datetime
import json
import logging; log = logging.getLogger('update_latest')
import os
import requests
import sys
import urllib.parse

requests.packages.urllib3.disable_warnings()

HOST = collections.namedtuple ('HOST', ['password', 'url', 'username'])

def _vid_as_tuple_of_int (lidvid:str):
    M,m = lidvid.split('::')[1].split('.')
    return (int(M),int(m))

def _datetime (input:str)->datetime.datetime:
    return datetime.datetime.fromisoformat (input)

def _log_level (input:str)->int:
    try: result = int(input)
    except ValueError: result = getattr (logging, input)
    return result

def add_doc (reg, doc, id):
    response = requests.put ('https://localhost:9200/' + reg + '/_doc/' + id,
                             auth=('admin', 'admin'), json=doc, verify=False)
    print (id, response)
    return

def cli():
    ap = argparse.ArgumentParser(description='''Update latest index from registry index

The program sweeps through the registry index to find all of "ops:Harvest_Info/ops:harvest_date_time" records between the begin time (-tb) and end time (-te) inclusive specified on the command line.''',
                                 epilog='''EXAMPLES:

- command for opensearch running in a container with the sockets published at 9200 for data ingested for full day March 11, 2020:
                                 
  update_latest.py -b https://localhost:9200 -p admin -u admin -tb 2020-03-011 -te 2020-03-12

or with full date time (both work the same)

  update_latest.py -b https://localhost:9200 -p admin -u admin -tb 2020-03-011T00:00:00 -te 2020-03-12T00:00:00


- getting more help on availables arguments and what is expected:

  update_latest.py --help''',
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument ('-b', '--base-URL', required=True, type=str)
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
    args = ap.parse_args()
    logging.basicConfig(filename=args.log_file, level=args.log_level,
                        format='%(asctime)s::%(levelname)s::%(message)s')
    log.info ('starting CLI processing')
    host = HOST(args.password, args.base_URL, args.username)
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

    for lidvid in troll_registry (host, args.time_begin, args.time_end):
        log.info ('processing %s', lidvid)
        latest = get_latest (host, lidvid.split('::')[0])

        if is_newer (latest, lidvid): update (host, latest, lidvid)
        pass
    return

def create_latest_index (host:HOST, config:{}):
    result = {}
    response = requests.put (urllib.parse.urljoin (host.url, 'latest'),
                             auth=(host.username, host.password),
                             json=config, verify=False)

    if response.status_code == 200: result.update (get_index (host, 'latest'))

    return result

def del_index (host:HOST, name:str):
    response = requests.delete (urllib.parse.urljoin (host.url, name),
                                auth=(host.username, host.password),
                                verify=False)

    if response.status_code != 200:
        log.error ('could not delete index %s because of error (%d): %s',
                   name, response.status_code, response.reason)
    return

def get_index (host:HOST, name:str)->{}:
    result = {}
    response = requests.get (urllib.parse.urljoin (host.url, name),
                             auth=(host.username, host.password), verify=False)

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

def get_latest (host:HOST, lid:str)->str:
    query = {'query':{},
             '_source':{'includes':['lidvid']}}
    response = requests.get (urllib.parse.urljoin (host.url, 'latest/_search'),
                             auth=(host.username, host.password), verify=False,
                             json=query)

    if response.status_code == 200:
        data = response.json()
        result = data['hits']['hits'][0]['_source']['lidvid']

        if 1 < data['hits']['total']['value']:
            log.error ('latest index contains more lidvids than the latest')
            sys.exit (-40)
            pass
    else: result = None

    return result

def is_newer (a:str, b:str)->bool:
    return True if not a else _vid_as_tuple_of_int(a) < _vid_as_tuple_of_int(b)

def troll_registry (host:HOST, begin:datetime.datetime, end:datetime.datetime):
    one = datetime.timedelta(seconds=1)
    more_data = True
    path = 'registry/_search?scroll=10m'
    query = { 'query':{'range':{'ops:Harvest_Info/ops:harvest_date_time':
                                {'gte':(begin).isoformat(sep=' ',
                                                         timespec='seconds'),
                                 'lte':(end+one).isoformat(sep=' ',
                                                           timespec='seconds'),
                                 'format':'yyyy-MM-dd HH:mm:ss'}
                      }},
              '_source':{'includes':['lidvid']},
              'size':500 }
    total = 0
    while more_data:
        resp = requests.get (urllib.parse.urljoin (host.url, path),
                             auth=(host.username, host.password),
                             verify=False, json=query)

        if resp.status_code == 200:
            data = resp.json()
            path = '_search/scroll'
            query = {'scroll':'10m','scroll_id':data['_scroll_id']}
            lidvids = {hit['_source']['lidvid'] for hit in data['hits']['hits']}
            lids = {lidvid.split('::')[0] for lidvid in lidvids}
            total += len(data['hits']['hits'])
            for lidvid in unique_lidvids (lids,lidvids): yield lidvid
            more_data = total < data['hits']['total']['value']
        else:
            more_data = False
            log.error ('Bad response code (%d): %s',
                       resp.status_code, resp.reason)
            pass
        pass

    if 'scroll_id' in query:
        path = '_search/scroll/' + query['scroll_id']
        requests.delete (urllib.parse.urljoin (host.url, path),
                         auth=(host.username, host.password), verify=False)
        pass
    return

def unique_lidvids (lids:{str}, lidvids:{str})->{str}:
    if len(lids) == len(lidvids): return lidvids

    result = []
    for lid in lids:
        subset = {lidvid for lidvid in filter(lambda lv,l=lid:lv.startswith(l),
                                              lidvids)}
        ordered = sorted(subset, key=_vid_as_tuple_of_int)
        result.append (ordered[-1])
        pass
    return result

def update (host:HOST, latest:str, lidvid:str):
    log.info ('updating latest index to %s', lidvid)
    if latest:
        path = 'latest/_doc/' + latest
        requests.delete (urllib.parse.urljoin (host.url, path),
                         auth=(host.username, host.password), verify=False)

        if response.status_code != 200:
            log.error ('could not delete %s because of error (%d): %s',
                       latest, response.status_code, response.reason)
            pass
        pass

    path = 'registry/_doc/' + lidvid
    response = requests.get (urllib.parse.urljoin (host.url, path),
                             auth=(host.username, host.password), verify=False)

    if response.status_code != 200:
        log.error ('could not collect %s because of error (%d): %s',
                   lidvid, response.status_code, response.reason)
    else:
        doc = response.json()
        path = 'latest/_doc/' + lidvid
        response = requests.put (urllib.parse.urljoin (host.url, path),
                                 auth=(host.username, host.password),
                                 verify=False, json=response.json()['_source'])

        if response.status_code not in (200,201):
            log.error ('could not add %s because of error (%d): %s',
                       lidvid, response.status_code, response.reason)
        pass
    return

if __name__ == '__main__': cli()
