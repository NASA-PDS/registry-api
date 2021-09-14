docker files

Files to build various flavors of Docker images:
    - Dockerfile.https : Builds image based on a release on github, employs a self-signed cert (for now)
    - Dockerfile.https.dev : Builds image based on a local jar file, employs a self-signed cert
    - Dockerfile.http : Builds image based on a release on github, no ssl setup
    - Dockerfile.http.dev : Builds image based on a local jar file, no ssl setup
    - Dockerfile.local : Runs mvn install and springboot:run, no ssl setup

You must specify a release version as a build argument 'version'. The https versions also require 
'keystore_pass' and 'key_pass' build arguments for the keystore password and key password, 
respectively.

The images are built from the base repository directory. An example build command line is:

docker build --build-arg version=4.0.0-SNAPSHOT \
             --build-arg keystore_pass=MyDogHasFleas \
             --build-arg key_pass=SoDoesMyCat \
             -t pds/registry-api-service:4.0.0-SNAPSHOT \
             -f docker/Dockerfile.https .



