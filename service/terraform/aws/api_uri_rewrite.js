/*
Cloudfront Function which performs URL rewrite to direct requests of the form:
    /api/<service>{-node}/<version>/<request>
to:
    /<request>
    + the http header json : { "x-request-node" : { "value" : "<service>{-node}/<version>" }
    
This version also fufills the transition from major.minor to major (only) 
version specs in the API request.
*/

var SVC_IDX = 2
var VER_IDX = 3
var CMD_IDX = 4

var TARGET_URI_PREFIX = "/api/search"

// The function is in response to the 'viewer request' event
function handler(event) {
    var request = event.request;
    var incomingUri = request.uri;

    // Strip out any leading multiple slashies - registry-api#208
    while (incomingUri.indexOf("//") >= 0) {
        incomingUri = incomingUri.replace("//","/");
    }
    
    // Put the URI back into the request now in case the rewrite doesn't happen
    request.uri = incomingUri
            
    // continue with the rewrite only if "/api/search*" is first in the URI
    if (incomingUri.startsWith(TARGET_URI_PREFIX)) {

        // split the uri
        var uriParts = incomingUri.split("/");

        // at a minimum service, version and command are required, otherwise fall through w/o changes
        if (uriParts.length > CMD_IDX && uriParts[CMD_IDX].trim() != "") {
            // ensure that the API version is major version only
            var reqVer = uriParts[VER_IDX];
            var majorVer = reqVer.split(".")[0];
            
            // extract service and version which are placed in the HTTP header
            var resultHeader = uriParts[SVC_IDX] + "/" + majorVer;
            
            // copy the rest of the request path to the new URI
            var newUri = "";
            for(var i = CMD_IDX; i < uriParts.length; i++) {
                newUri += "/" + uriParts[i];
            }

            // set updated URI into request and set request node header
            request.uri = newUri;
            request.headers['x-request-node'] = { "value" : resultHeader };
            console.log("incoming URI [" + incomingUri + "] rewritten to [" + newUri 
                + "] & x-request-node [" + resultHeader + "]")
        } else {
            console.log("incoming URI [" + incomingUri + "] : no rewrite (no command)")
        }
    } else {
        console.log("incoming URI [" + incomingUri + "] : no rewrite (no prefix match)")
    }

    return request;
}
