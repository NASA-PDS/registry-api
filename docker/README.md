# 🚢 Dockerfile

There's one Dockerfile here that can be used to make images for development and production.

👉 **Note:** It's a security risk to include private keys in images. As a result, we've removed the old `Dockerfile.https` and `Dockerfile.https.dev`. We've also removed all the other `Dockerfile.*` files to eliminate confusion, except for `Dockerfile.local`; see below.


## Build the image 

To build an image, run:


    cd ./service
    docker image build --build-arg api_jar=URL --tag [OWNER/]registry-api-service:TAG .
    

Replace `URL` with the URL (or relative file path) to a `registry-api-service.jar` and `TAG` with the desired version tag. You can add `OWNER/` to tag the image for a specific owner, such as `nasapds/`.

The GitHub Actions configured in this repository automatically make images after each `stable-cicd.yaml` workflow (with a `:X.Y.Z` tag) and `unstable-cicd.yaml` workflow (with a `:latest` tag) and publishes them to the Docker Hub.


## 🧱 Examples

Building a local image:
```console
$ git clone https://github.com/NASA-PDS/registry-api.git
$ cd registry-api
$ mvn package
$ docker image build --build-arg api_jar=service/target/registry-api-service-*.jar --tag registry-api-service:latest --file docker/Dockerfile .
```

Building an image from a released jar file:
```console
$ docker image build --build-arg api_jar=https://github.com/NASA-PDS/registry-api/releases/download/v1.0.0/registry-api-service-1.0.0.jar --tag nasapds/registry-api-service:1.0.0 --file docker/Dockerfile .
```

## Run the image


For a local deployment simply do:

docker run -t -i -e SERVER_PORT=8082 -p 8082:8082 [OWNER/]registry-api-service:TAG 


The same image can be used to run in multiple environments by passing arguments at the service start up, through environment variables. The provided options can override the `application.properties` configuration of the application. The server port needs to define a specific environment variable and match -p option of the docker run.

For example on AWS, with OpenSearch serverless as a back-end:

    SPRING_BOOT_APP_ARGS=--openSearch.host=<your opensearch serverless URL> --openSearch.CCSEnabled=true --openSearch.username="" --openSearch.disciplineNodes=atm-delta,en-delta --registry.service.version=1.5.0-SNAPSHOT
    SERVER_PORT=80






## 📍 Dockerfile.local

You can ignore `Dockerfile.local` unless you're @al-niessner.
