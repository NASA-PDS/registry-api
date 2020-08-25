# coding: utf-8

from __future__ import absolute_import

from flask import json
from six import BytesIO

from pds_api_server.models.routes import Routes  # noqa: E501
from pds_api_server.test import BaseTestCase


class TestRoutesController(BaseTestCase):
    """RoutesController integration test stubs"""

    def test_all_routes(self):
        """Test case for all_routes

        
        """
        response = self.client.open(
            '/PDS_APIs/pds_federated_api/0.1.0/routes',
            method='GET')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_routes(self):
        """Test case for routes

        
        """
        query_string = [('version', 'version_example'),
                        ('query', 'query_example')]
        response = self.client.open(
            '/PDS_APIs/pds_federated_api/0.1.0/routes/{resource}'.format(resource='resource_example'),
            method='GET',
            query_string=query_string)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
