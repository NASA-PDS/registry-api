#! /usr/bin/env python3
'''verify the acceptance criteria for issue 4

*requires* requests
'''

import requests

TEST_N_CRITERIA=[
    ('http://localhost:8080/products/urn:nasa:pds:izenberg_pdart14_meap:document::1.0',200),
    ('http://localhost:8080/products/urn:nasa:pds:izenberg_pdart14_meap:document',200),
    ('http://localhost:8080/collections/urn:nasa:pds:izenberg_pdart14_meap:data_imagecube::1.0',200),
    ('http://localhost:8080/collections/urn:nasa:pds:izenberg_pdart14_meap:data_imagecube',200),
    ('http://localhost:8080/bundles/urn:nasa:pds:izenberg_pdart14_meap::1.0',200),
    ('http://localhost:8080/bundles/urn:nasa:pds:izenberg_pdart14_meap',200),
    ('http://localhost:8080/collections/urn:nasa:pds:izenberg_pdart14_meap:data_imagecube::1.0/products',200),
    ('http://localhost:8080/collections/urn:nasa:pds:izenberg_pdart14_meap:data_imagecube/products',200),
    ('http://localhost:8080/bundles/urn:nasa:pds:izenberg_pdart14_meap::1.0/collections',200),
    ('http://localhost:8080/bundles/urn:nasa:pds:izenberg_pdart14_meap/collections',200),
    ('http://localhost:8080/collections/urn:nasa:pds:izenberg_pdart14_meap:data_imagecube::1.',404),
    ('http://localhost:8080/collections/urn:nasa:pds:izenberg_pdart14_meap:data_imagecube::',404),
    ('http://localhost:8080/collections/urn:nasa:pds:izenberg_pdart14_meap:data_imagecube:',404),
    ('http://localhost:8080/collections/urn:nasa:pds:izenberg_pdart14_meap:data_imagecub',404),
    ('http://localhost:8080/collections/urn:nasa:pds:izenberg_pdart14_meap:data_imagecube::1./products',404),
    ('http://localhost:8080/collections/urn:nasa:pds:izenberg_pdart14_meap:data_imagecube::1/products',404),
    ('http://localhost:8080/collections/urn:nasa:pds:izenberg_pdart14_meap:data_imagecube::/products',404),
    ('http://localhost:8080/collections/urn:nasa:pds:izenberg_pdart14_meap:data_imagecube:/products',404),
    ]

for url,expectation in TEST_N_CRITERIA:
    result = requests.get(url)

    if result.status_code == expectation:
        print ('success', result.status_code, url)
    else: print ('failed', expectation, '!=', result.status_code, url)
    pass
