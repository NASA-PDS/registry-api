

Build the docker image:

    docker build -t nasapds/registry-sweepers .

Push it to docker hub (for integration tests):

    docker push nasapds/registry-sweepers

Run it:

    docker run -e PROV_ENDPOINT='https://elasticsearch:9200/' -e PROV_CREDENTIALS='{"admin": "admin"}' nasapds/registry-sweepers

With:
- PROV_ENDPOINT: the URL to the OpenSearch web server
- PROV_CREDENTIALS: the credentials for the OpenSearch connection as a JSO string, for example `{"username1": "secret_password"}`
