import connexion
import six

from pds_api_server.models.capabilities import Capabilities  # noqa: E501
from pds_api_server import util


def capabilities():  # noqa: E501
    """capabilities api entry point, list the resources provided by the current API end-point.

     # noqa: E501


    :rtype: Capabilities
    """
    return 'do some magic!'
