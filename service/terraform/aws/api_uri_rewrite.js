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

    // continue with the rewrite only if "/api/search*" is first in the URI
    if (incomingUri.startsWith("/api/search")) {

        // split the uri
        var uriParts = incomingUri.split("/");

        // at a minimum service, version and command are required, otherwise fall through w/o changes
        if (uriParts.length > 4 && uriParts[4].trim() != "") {
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
