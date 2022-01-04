# coding: utf-8

from __future__ import absolute_import

from flask import json
from six import BytesIO

from pds_api_server.models.capabilities import Capabilities  # noqa: E501
from pds_api_server.test import BaseTestCase


class TestCapablitiesController(BaseTestCase):
    """CapablitiesController integration test stubs"""

    def test_capabilities(self):
        """Test case for capabilities

        capabilities api entry point, list the resources provided by the current API end-point.
        """
        response = self.client.open(
            '/PDS_APIs/pds_federated_api/0.1.0/capabilities',
            method='GET')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
