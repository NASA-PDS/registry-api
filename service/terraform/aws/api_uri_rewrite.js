/*
Cloudfront Function which performs URL rewrite to direct requests of the form:
    /api/<service>{-node}/<version>/<request>
to:
    /<request>
    + the http header 'x-request-node:<service>{-node}/<version>'
*/

// The function is in response to the 'viewer request' event
function handler(event) {
    let request = event.request;
    let incomingUri = request.uri;
 
    // split the uri
    const uriParts = incomingUri.split("/");
 
    // continue with the rewrite only if "/api/" is first in the URI
    if (uriParts[1].toLowerCase() == "api") {
 
        // at a minimum service and version are required, otherwise fall through w/o changes
        if (uriParts.length >= 4) {
            // extract service and version which are placed in the HTTP header
            const resultHeader = uriParts[2] + "/" + uriParts[3];
 
            // copy the rest of the request path to the new URI
            let newUri = "";
            for(let i = 4; i < uriParts.length; i++) {
                newUri += "/" + uriParts[i];
            }
 
            // write the updated URI and the new HTTP header to the request only
            // if things check out to this point
            request.uri = newUri;
            request.headers['x-request-node'] = resultHeader;
        }
 
    }
 
    return request;
}
