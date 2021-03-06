
import os
import requests

def fetch_kvp_json (url:str):
    url += '?fields=lidvid'
    print ('url:', url)
    result = requests.get(url, headers={'Accept':'application/kvp+json'})
    return result.status_code,result.json()

def make_url (endpoint:str)->str:
    return os.environ.get ('REG_APO_UT_TYPE', 'http') + '://' + \
           os.environ.get ('REG_API_UT_HOSTNAME', 'localhost') + ':' + \
           os.environ.get ('REG_API_UT_PORT', '8080') + endpoint
