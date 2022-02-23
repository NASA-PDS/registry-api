/*
Cloudfront Function which performs URL rewrite to direct requests of the form:
    https://<host>/api/<service>{-node}/<version>/<request>
to:
    https://<host>/<request>
    + the http header 'x-request-node:<service>{-node}/<version>'
*/

// The function is in response to the 'viewer request' event
function handler(event) {
    var request = event.request;
    var incoming_uri = request.uri;
 
    // split the uri on "/api/" - only api invocations are rewritten by this function
    const uri_parts = incoming_uri.split("/api/");
 
    // continue with the rewrite only if "/api/" is in the URI
    if (uri_parts.length > 1) {
        // save off uri prefix - typically protocol and host
        var new_uri = uri_parts[0];
 
        // decompose the URI path
        const path_parts = uri_parts[1].split("/");
 
        // at a minimum service and version are required, otherwise fall through w/o changes
        if (path_parts.length >= 2) {
            // extract service and version which are placed in the HTTP header
            const result_header = path_parts[0] + "/" + path_parts[1];
 
            // copy the rest of the request path to the new URI
            for(let i = 2; i < path_parts.length; i++) {
                new_uri += "/" + path_parts[i];
            }
 
            // write the updated URI and the new HTTP header to the request only
            // if things check out to this point
            request.uri = new_uri;
            request.headers['x-request-node'] = result_header;
        }
 
    }
 
    return request;
}
