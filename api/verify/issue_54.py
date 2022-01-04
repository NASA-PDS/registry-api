#! /usr/bin/env python3
'''verify the acceptance criteria for issue 54

*requires* requests
'''

import requests

TEST_N_CRITERIA=[
    ('http://localhost:8080/products?q=lid eq *pdart14_me*',200,
     17,'id', 'pdart14_me'),
    ('http://localhost:8080/products?q=( lid eq *pdart14_me* and lid eq *_orbit_* )',200,
     3,'id', '_orbit_'),
    ]

for url,expectation,length,kwd,contains in TEST_N_CRITERIA:
    result = requests.get(url)

    if result.status_code == expectation:
        print ('success', result.status_code, url)
        response = result.json()

        if 'data' in response:
            if all([len (response['data']) == length] +
                    [contains in d[kwd] for d in response['data']]):
                print ('  success all data parameters match')
            else: print ('  failed to match all data parameters')
            pass
    else: print ('failed', expectation, '!=', result.status_code, url)
    pass
