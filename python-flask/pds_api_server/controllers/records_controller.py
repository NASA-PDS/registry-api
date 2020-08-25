import connexion
import six

from pds_api_server.models.pds4_label import PDS4Label  # noqa: E501
from pds_api_server import util


def record(lidvid):  # noqa: E501
    """URN resolver for lidvid

     # noqa: E501

    :param lidvid: lidvid (urn)
    :type lidvid: str

    :rtype: PDS4Label
    """
    return 'do some magic!'
