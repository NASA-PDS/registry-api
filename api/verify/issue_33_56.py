#! /usr/bin/env python3
'''verify the acceptance criteria for issue 4

*requires* requests
'''

import requests

TEST_N_CRITERIA=[
    ('http://localhost:8080/bundles/urn:nasa:pds:izenberg_pdart14_meap::1.0/collections',200,4),  #57
    ('http://localhost:8080/bundles/urn:nasa:pds:izenberg_pdart13_meap::1.0/collections',404,0),
    ('http://localhost:8080//bundles/urn:nasa:pds:izenberg_pdart14_meap::1.0/products',200,12),  #59
    ('http://localhost:8080//bundles/urn:nasa:pds:izenberg_pdart13_meap::1.0/products',404,0),
    ('http://localhost:8080/collections/urn:nasa:pds:izenberg_pdart14_meap:data_eetable::1.0/bundles',200,1),  #62
    ('http://localhost:8080/collections/urn:nasa:pds:izenberg_pdart13_meap:data_eetable::1.0/bundles',404,0),
    ('http://localhost:8080/collections/urn:nasa:pds:izenberg_pdart14_meap:data_eetable::1.0/products',200,3),  #58
    ('http://localhost:8080/collections/urn:nasa:pds:izenberg_pdart13_meap:data_eetable::1.0/products',404,0),
    ('http://localhost:8080/products/urn:nasa:pds:izenberg_pdart14_meap:data_eetable:ele_evt_8hr_orbit_2012-2013::1.0/bundles',200,1),  #60
    ('http://localhost:8080/products/urn:nasa:pds:izenberg_pdart13_meap:data_eetable:ele_evt_8hr_orbit_2012-2013::1.0/bundles',404,0),
    ('http://localhost:8080/products/urn:nasa:pds:izenberg_pdart14_meap:data_eetable:ele_evt_8hr_orbit_2012-2013::1.0/collections',200,1),  #61
    ('http://localhost:8080/products/urn:nasa:pds:izenberg_pdart13_meap:data_eetable:ele_evt_8hr_orbit_2012-2013::1.0/collections',404,0),
    ]

for url,expectation,length in TEST_N_CRITERIA:
    result = requests.get(url)

    if result.status_code == expectation:
        if result.status_code == 200:
            this_length = len (result.json()['data']) if 'data' in result.json() else 0
            if this_length == length:
                print ('success', result.status_code, url)
            else: print ('failed', length, '!=', this_length, url)
        else: print ('success correct error code', result.status_code) 
    else: print ('failed', expectation, '!=', result.status_code, url)
    pass
