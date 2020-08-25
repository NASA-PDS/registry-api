# coding: utf-8

from __future__ import absolute_import

from flask import json
from six import BytesIO

from pds_api_server.models.pds4_label import PDS4Label  # noqa: E501
from pds_api_server.test import BaseTestCase


class TestRecordsController(BaseTestCase):
    """RecordsController integration test stubs"""

    def test_record(self):
        """Test case for record

        URN resolver for lidvid
        """
        response = self.client.open(
            '/PDS_APIs/pds_federated_api/0.1.0/records/{lidvid}'.format(lidvid='lidvid_example'),
            method='GET')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
