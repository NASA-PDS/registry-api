/*
Cloudfront Function which performs URL rewrite to direct requests of the form:
    /api/<service>/<version>/<request>
to:
    /<request>
    + the http header json : { "x-request-version" : { "value" : "<version>" }
    
This version also fufills the transition from major.minor to major (only) 
version specs in the API request.
*/

var SVC_IDX = 2
var VER_IDX = 3
var CMD_IDX = 4

// add trailing '/' so any node-specific search request result in 404
var TARGET_URI_PREFIX = "/api/search/"
var DEBUG_PARAM       = "cf_debug" 

// The function is in response to the 'viewer request' event
function handler(event) {
    var request = event.request;
    var incomingUri = request.uri;

    // See if detailed logging is enabled
    var debug = false
    if (DEBUG_PARAM in request.querystring) {
         debug = true
         delete request.querystring[DEBUG_PARAM]
    }

    // Strip out any leading multiple slashies - registry-api#208
    while (incomingUri.indexOf("//") >= 0) {
        incomingUri = incomingUri.replace("//","/");
    }
    
    // Put the URI back into the request now in case the rewrite doesn't happen
    request.uri = incomingUri
            
    if (debug) console.log("incoming URI [" + incomingUri + "]")

    // continue with the rewrite only if "/api/search/" is first in the URI
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
            
            // copy the rest of the request path to the new URI
            var newUri = "";
            for(var i = CMD_IDX; i < uriParts.length; i++) {
                newUri += "/" + uriParts[i];
                if (debug) console.log("appending to URI path [" + uriParts[i] + "]")
            }

            // set updated URI into request and set request node header
            request.uri = newUri;
            request.headers['x-request-version'] = { "value" : majorVer };
            if (debug) {
                console.log("incoming URI [" + incomingUri + "] rewritten to [" + newUri 
                    + "] & x-request-version [" + majorVer + "]")
            }
        } else {
            if (debug) console.log("incoming URI [" + incomingUri + "] : no rewrite (no command)")
        }
    } else {
        if (debug) console.log("incoming URI [" + incomingUri + "] : no rewrite (no prefix match)")
    }

    return request;
}
