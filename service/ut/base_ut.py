
import helpers
import unittest

def test_bad_group():
    ep = '/classes/notreal'
    status,data = helpers.fetch_kvp_json (helpers.make_url (ep))
    assert 406 == status
    assert 'message' in data
    assert 'request' in data
    assert data['message'].startswith ("Unknown group 'notreal'. All known groups:")
    assert data['request'] == ep
    return

def test_bad_lidvid():
    ep = '/products/notreal'
    status,data = helpers.fetch_kvp_json (helpers.make_url (ep))
    assert 404 == status
    assert 'message' in data
    assert 'request' in data
    assert 'The lidvid notreal was not found' == data['message']
    assert data['request'] == ep
    return

class TestAny(unittest.TestCase):
    def test_products(self):
        status,resp = helpers.fetch_kvp_json (helpers.make_url ('/classes/any'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (25, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        return resp['data'][-1]['lidvid']

    def test_lidvid(self):
        lidvid = self.test_products()
        status,resp = helpers.fetch_kvp_json (helpers.make_url
                                              (f'/products/{lidvid}'))
        self.assertEqual (200, status)
        self.assertIn ('lidvid', resp)
        self.assertEqual (lidvid, resp['lidvid'])
        return

    def test_lidvid_latest(self):
        lidvid = self.test_products()
        status,resp = helpers.fetch_kvp_json(helpers.make_url
                                             (f'/products/{lidvid}/latest'))
        self.assertEqual (200, status)
        self.assertIn ('lidvid', resp)
        self.assertEqual (lidvid, resp['lidvid'])
        return

    def test_lidvid_all(self):
        lidvid = self.test_products()
        status,resp = helpers.fetch_kvp_json (helpers.make_url
                                              (f'/products/{lidvid}/all'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (1, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        self.assertEqual (lidvid, resp['data'][0]['lidvid'])
        return

    def test_collections(self):
        lidvid = self.test_products()
        status,resp = helpers.fetch_kvp_json (helpers.make_url
                                              (f'/products/{lidvid}/member-of'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (1, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        return

    def test_bundles(self):
        lidvid = self.test_products()
        status,resp = helpers.fetch_kvp_json (helpers.make_url
                                              (f'/products/{lidvid}/member-of/member-of'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (1, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        return
    pass

class TestBundles(unittest.TestCase):
    def test_bundles(self):
        status,resp = helpers.fetch_kvp_json (helpers.make_url ('/classes/bundles'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (1, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        return resp['data'][0]['lidvid']

    def test_lidvid(self):
        lidvid = self.test_bundles()
        status,resp = helpers.fetch_kvp_json (helpers.make_url
                                              (f'/products/{lidvid}'))
        self.assertEqual (200, status)
        self.assertIn ('lidvid', resp)
        self.assertEqual (lidvid, resp['lidvid'])
        return

    def test_lidvid_latest(self):
        lidvid = self.test_bundles()
        status,resp = helpers.fetch_kvp_json(helpers.make_url
                                             (f'/products/{lidvid}/latest'))
        self.assertEqual (200, status)
        self.assertIn ('lidvid', resp)
        self.assertEqual (lidvid, resp['lidvid'])
        return

    def test_lidvid_all(self):
        lidvid = self.test_bundles()
        status,resp = helpers.fetch_kvp_json (helpers.make_url
                                              (f'/products/{lidvid}/all'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (1, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        self.assertEqual (lidvid, resp['data'][0]['lidvid'])
        return

    def test_collections(self):
        lidvid = self.test_bundles()
        status,resp = helpers.fetch_kvp_json \
                   (helpers.make_url (f'/classes/bundles/{lidvid}/members'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (3, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        return

    def test_collections_latest(self):
        lidvid = self.test_bundles()
        status,resp = helpers.fetch_kvp_json \
            (helpers.make_url (f'/classes/bundles/{lidvid}/members/latest'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (3, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        return

    def test_collections_all(self):
        lidvid = self.test_bundles()
        status,resp = helpers.fetch_kvp_json \
               (helpers.make_url (f'/classes/bundles/{lidvid}/members/all'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (3, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        return

    def test_products(self):
        lidvid = self.test_bundles()
        status,resp = helpers.fetch_kvp_json\
                      (helpers.make_url (f'/classes/bundles/{lidvid}/members/members'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (21, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        return
    pass

class TestCollections(unittest.TestCase):
    def test_bundles(self):
        lidvid = self.test_collections()
        status,resp = helpers.fetch_kvp_json \
                      (helpers.make_url (f'/classes/collections/{lidvid}/member-of'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (1, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        return

    def test_collections(self):
        status,resp = helpers.fetch_kvp_json \
                      (helpers.make_url ('/classes/collections'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (3, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        return resp['data'][0]['lidvid']

    def test_lidvid(self):
        lidvid = self.test_collections()
        status,resp = helpers.fetch_kvp_json (helpers.make_url
                                              (f'/products/{lidvid}'))
        self.assertEqual (200, status)
        self.assertIn ('lidvid', resp)
        self.assertEqual (lidvid, resp['lidvid'])
        return

    def test_lidvid_latest(self):
        lidvid = self.test_collections()
        status,resp = helpers.fetch_kvp_json(helpers.make_url
                                             (f'/products/{lidvid}/latest'))
        self.assertEqual (200, status)
        self.assertIn ('lidvid', resp)
        self.assertEqual (lidvid, resp['lidvid'])
        return

    def test_lidvid_all(self):
        lidvid = self.test_collections()
        status,resp = helpers.fetch_kvp_json (helpers.make_url
                                              (f'/products/{lidvid}/all'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (1, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        self.assertEqual (lidvid, resp['data'][0]['lidvid'])
        return

    def test_products(self):
        lidvid = self.test_collections()
        status,resp = helpers.fetch_kvp_json (helpers.make_url
                                              (f'/classes/collections/{lidvid}/members'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (7, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        return

    def test_products_latest(self):
        lidvid = self.test_collections()
        status,resp = helpers.fetch_kvp_json (helpers.make_url
                                              (f'/classes/collections/{lidvid}/members/latest'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (7, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        return

    def test_products_all(self):
        lidvid = self.test_collections()
        status,resp = helpers.fetch_kvp_json (helpers.make_url
                                              (f'/classes/collections/{lidvid}/members/all'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (7, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        return
    pass

class TestProducts(unittest.TestCase):
    def test_products(self):
        status,resp = helpers.fetch_kvp_json (helpers.make_url ('/classes/products'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (21, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        return resp['data'][-1]['lidvid']

    def test_lidvid(self):
        lidvid = self.test_products()
        status,resp = helpers.fetch_kvp_json (helpers.make_url
                                              (f'/products/{lidvid}'))
        self.assertEqual (200, status)
        self.assertIn ('lidvid', resp)
        self.assertEqual (lidvid, resp['lidvid'])
        return

    def test_lidvid_latest(self):
        lidvid = self.test_products()
        status,resp = helpers.fetch_kvp_json(helpers.make_url
                                             (f'/products/{lidvid}/latest'))
        self.assertEqual (200, status)
        self.assertIn ('lidvid', resp)
        self.assertEqual (lidvid, resp['lidvid'])
        return

    def test_lidvid_all(self):
        lidvid = self.test_products()
        status,resp = helpers.fetch_kvp_json (helpers.make_url
                                              (f'/products/{lidvid}/all'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (1, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        self.assertEqual (lidvid, resp['data'][0]['lidvid'])
        return

    def test_collections(self):
        lidvid = self.test_products()
        status,resp = helpers.fetch_kvp_json (helpers.make_url
                                              (f'/products/{lidvid}/member-of'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (1, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        return

    def test_bundles(self):
        lidvid = self.test_products()
        status,resp = helpers.fetch_kvp_json (helpers.make_url
                                              (f'/products/{lidvid}/member-of/member-of'))
        self.assertEqual (200, status)
        self.assertIn ('summary', resp)
        self.assertIn ('hits', resp['summary'])
        self.assertEqual (1, resp['summary']['hits'])
        self.assertIn ('data', resp)
        self.assertEqual (resp['summary']['hits'], len(resp['data']))
        self.assertIn ('lidvid', resp['data'][0])
        return
    pass
