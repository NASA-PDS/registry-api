import connexion
import six

from pds_api_server.models.collections import Collections  # noqa: E501
from pds_api_server.models.error_message import ErrorMessage  # noqa: E501
from pds_api_server import util


def get_collection(start=None, limit=None, q=None, fields=None, sort_by=None):  # noqa: E501
    """request PDS collections

     # noqa: E501

    :param start: offset in matching result list, for pagination
    :type start: int
    :param limit: maximum number of matching results returned, for pagination
    :type limit: int
    :param q: search query
    :type q: str
    :param fields: returned fields, syntax field0,field1
    :type fields: List[str]
    :param sort_by: sort results, syntax asc(field0),desc(field1)
    :type sort_by: List[str]

    :rtype: Collections
    """
    return 'do some magic!'
