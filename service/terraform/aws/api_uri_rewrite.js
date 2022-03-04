/*
Cloudfront Function which performs URL rewrite to direct requests of the form:
    /api/<service>{-node}/<version>/<request>
to:
    /<request>
    + the http header json : { "x-request-node" : { "value" : "<service>{-node}/<version>" }
*/

// The function is in response to the 'viewer request' event
function handler(event) {
    var request = event.request;
    var incomingUri = request.uri;

    // split the uri
    var uriParts = incomingUri.split("/");

    // continue with the rewrite only if "/api/search" is first in the URI
    if (uriParts[1].toLowerCase() == "api" && uriParts[2].toLowerCase() == "search" ) {

        // at a minimum service and version are required, otherwise fall through w/o changes
        if (uriParts.length >= 4) {
            // extract service and version which are placed in the HTTP header
            var resultHeader = uriParts[2] + "/" + uriParts[3];

            // copy the rest of the request path to the new URI
            var newUri = "";
            for(var i = 4; i < uriParts.length; i++) {
                newUri += "/" + uriParts[i];
            }

            // write the updated URI and the new HTTP header to the request only
            // if things check out to this point
            request.uri = newUri;
            request.headers['x-request-node'] = { "value" : resultHeader };
        }

    }

    return request;
}
