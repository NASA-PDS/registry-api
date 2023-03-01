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

    // See if detailed logging is enabled
    var debug = false
    if ("cf_debug" in request.querystring) {
         debug = true
         delete request.querystring["cf_debug"]
    }

    // Strip out any leading multiple slashies - registry-api#208
    while (incomingUri.indexOf("//") >= 0) {
        incomingUri = incomingUri.replace("//","/");
    }
    
    // Put the URI back into the request now in case the rewrite doesn't happen
    request.uri = incomingUri
            
    if (debug) console.log("incoming URI [" + incomingUri + "]")

    // continue with the rewrite only if "/api/search*" is first in the URI
    if (incomingUri.startsWith(TARGET_URI_PREFIX)) {

        if (debug) console.log("incoming URI matches prefix [" + TARGET_URI_PREFIX + "]")

        // split the uri - note that w/ a initial '/' the first element in the resulting
        // array will be empty, so be sure to consider this when computing indexes of
        // each token
        var uriParts = incomingUri.split("/");

        // at a minimum service, version and command are required (i.e. there are non-empty components in the URI after
        // "/api/search/<version>") in order consider a rewrite of the URI, otherwise fall through w/o changes
        if (uriParts.length > CMD_IDX && uriParts[CMD_IDX].trim() != "") {
            if (debug) console.log("incoming URI includes a command")

            // ensure that the API version is major version only
            var reqVer = uriParts[VER_IDX];
            var majorVer = reqVer.split(".")[0];
            if (debug) console.log("revised request version [" + reqVer + "] to [" + majorVer + "]")
            
            // extract service and version which are placed in the HTTP header
            var resultHeader = uriParts[SVC_IDX] + "/" + majorVer;
            if (debug) console.log("x-request-node header value [" + resultHeader + "]")
            
            // copy the rest of the request path to the new URI
            var newUri = "";
            for(var i = CMD_IDX; i < uriParts.length; i++) {
                newUri += "/" + uriParts[i];
                if (debug) console.log("appending to URI path [" + uriParts[i] + "]")
            }

            // set updated URI into request and set request node header
            request.uri = newUri;
            request.headers['x-request-node'] = { "value" : resultHeader };
            if (debug) {
                console.log("incoming URI [" + incomingUri + "] rewritten to [" + newUri 
                    + "] & x-request-node [" + resultHeader + "]")
            }
        } else {
            if (debug) console.log("incoming URI [" + incomingUri + "] : no rewrite (no command)")
        }
    } else {
        if (debug) console.log("incoming URI [" + incomingUri + "] : no rewrite (no prefix match)")
    }

    return request;
}
