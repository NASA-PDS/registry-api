/*
Cloudfront Function which performs URL rewrite to direct requests of the form:
    /api/<service>{-node}/<version>/<request>
to:
    /<request>
    + the http header 'x-request-node:<service>{-node}/<version>'
*/

// The function is in response to the 'viewer request' event
function handler(event) {
    var request = event.request;
    var incoming_uri = request.uri;
 
    // split the uri
    const uri_parts = incoming_uri.split("/");
 
    // continue with the rewrite only if "/api/" is first in the URI
    if (uri_parts[1].toLowerCase() == "api") {
 
        // at a minimum service and version are required, otherwise fall through w/o changes
        if (uri_parts.length >= 4) {
            // extract service and version which are placed in the HTTP header
            const result_header = uri_parts[2] + "/" + uri_parts[3];
 
            // copy the rest of the request path to the new URI
            var new_uri = "";
            for(let i = 4; i < uri_parts.length; i++) {
                new_uri += "/" + uri_parts[i];
            }
 
            // write the updated URI and the new HTTP header to the request only
            // if things check out to this point
            request.uri = new_uri;
            request.headers['x-request-node'] = result_header;
        }
 
    }
 
    return request;
}
