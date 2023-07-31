# 🚢 Dockerfile

There's one Dockerfile here that can be used to make images for development and production.

👉 **Note:** It's a security risk to include private keys in images. As a result, we've removed the old `Dockerfile.https` and `Dockerfile.https.dev`. We've also removed all the other `Dockerfile.*` files to eliminate confusion, except for `Dockerfile.local`; see below.

To build an image, run:

    docker image build --build-arg api_jar=URL --tag [OWNER/]registry-api-service:TAG .

Replace `URL` with the URL (or relative file path) to a `registry-api-service.jar` and `TAG` with the desired version tag. You can add `OWNER/` to tag the image for a specific owner, such as `nasapds/`.

The GitHub Actions configured in this repository automatically make images after each `stable-cicd.yaml` workflow (with a `:X.Y.Z` tag) and `unstable-cicd.yaml` workflow (with a `:latest` tag) and publishes them to the Docker Hub.


## 🧱 Example Builds

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

## 📍 Dockerfile.aws

This Dockerfile is used to make images for AWS deployments of the registry manager. It copies in an AWS-specific application.properties file which has been setup to inform the service to obtain certain properties as environment variable values that have been injected by the Elastic Container Service (ECS) runtime. An example of this is the Opensearch credentials from the Secrets Manager.

## 📍 Dockerfile.local

You can ignore `Dockerfile.local` unless you're @al-niessner.
