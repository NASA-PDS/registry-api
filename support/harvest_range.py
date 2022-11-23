#! /usr/bin/env python3

import argparse
import collections
import datetime
import json
import logging; log = logging.getLogger('harvest_range')
import os
import requests
import sys
import urllib.parse

requests.packages.urllib3.disable_warnings()

HOST = collections.namedtuple ('HOST', ['password', 'url', 'username'])

def _log_level (input:str)->int:
    try: result = int(input)
    except ValueError: result = getattr (logging, input)
    return result

def cli():
    ap = argparse.ArgumentParser(description='''Scan the registry index to find the first and last data ingestion date

The program sweeps through the registry index to find the earliest and latest "ops:Harvest_Info/ops:harvest_date_time".''',
                                 epilog='''EXAMPLES:

- command for opensearch running in a container with the sockets published at 9200 for data ingested for full day March 11, 2020:
                                 
  harvest_range.py -b https://localhost:9200 -p admin -u admin

- getting more help on availables arguments and what is expected:

  harvest_range.py --help''',
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument ('-b', '--base-URL', required=True, type=str)
    ap.add_argument ('-l', '--log-file', default='/dev/stdout', required=False,
                     help='file to write the log messages [%(default)s]')
    ap.add_argument ('-L', '--log-level', default='ERROR', required=False,
                     type=_log_level, help='Python logging level as an int or string like INFO for logging.INFO [%(default)s]')
    ap.add_argument ('-p', '--password', default=None, required=False,
                     help='password to login to opensearch leaving it blank if opensearch does not require login')
    ap.add_argument ('-u', '--username', default=None, required=False,
                     help='username to login to opensearch leaving it blank if opensearch does not require login')
    args = ap.parse_args()
    logging.basicConfig(filename=args.log_file, level=args.log_level,
                        format='%(asctime)s::%(levelname)s::%(message)s')
    log.info ('starting CLI processing')
    host = HOST(args.password, args.base_URL, args.username)
    b,e = datetime.datetime.now(),datetime.datetime(1000,1,1)
    for date in troll_registry (host):
        log.info ('processing %s', date.isoformat())
        e = max(date, e)
        b = min(date, b)
        pass
    log.critical ('Earliest harvest: %s', b.isoformat(sep=' ',
                                                      timespec='seconds'))
    log.critical ('Latest harvest:   %s', e.isoformat(sep=' ',
                                                      timespec='seconds'))
    return

def troll_registry (host:HOST):
    more_data = True
    path = 'registry/_search?scroll=10m'
    query = {'query':{'match_all':{}},
             '_source':{'includes':['ops:Harvest_Info/ops:harvest_date_time']},
             'size':5 }
    total = 0
    while more_data:
        resp = requests.get (urllib.parse.urljoin (host.url, path),
                             auth=(host.username, host.password),
                             verify=False, json=query)

        if resp.status_code == 200:
            data = resp.json()
            path = '_search/scroll'
            query = {'scroll':'10m','scroll_id':data['_scroll_id']}
            total += len(data['hits']['hits'])
            for hit in data['hits']['hits']:
                yield datetime.datetime.fromisoformat(hit['_source']['ops:Harvest_Info/ops:harvest_date_time'][:-1])
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

if __name__ == '__main__': cli()
