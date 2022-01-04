import connexion
import six

from pds_api_server.models.routes import Routes  # noqa: E501
from pds_api_server import util


def all_routes():  # noqa: E501
    """all_routes

     # noqa: E501


    :rtype: Routes
    """
    return 'do some magic!'


def routes(resource, version=None, query=None):  # noqa: E501
    """routes

     # noqa: E501

    :param resource: requested action as a request path, e.g. &#x27;/record&#x27;
    :type resource: str
    :param version: 
    :type version: str
    :param query: query parameter on the action, e.g. lidvid urn for a &#x27;/records&#x27; action
    :type query: str

    :rtype: None
    """
    return 'do some magic!'
