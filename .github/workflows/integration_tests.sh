#! /usr/bin/env bash
#
# requires: docker, git, mvn, and shellcheck
#
# docker - builds the registry api image, uses compose to run a host of services
# git clones registry repo
# mvn - to build the jar file for the current source registry api source code
# "shellcheck" - linter to keep this script clean
#

build() {
    mvn --quiet clean package
    jar_file="$(find ./service/target/ -maxdepth 1 -regextype posix-extended -regex '.*/registry-api-service-[0-9]+\.[0-9]+\.[0-9]+(-SNAPSHOT)?\.jar')"
    docker build --build-arg api_jar="$jar_file" -t nasapds/registry-api-service:latest -f docker/Dockerfile .
}

clean() {
    docker compose \
           --ansi never \
           --profile int-registry-batch-loader \
           --project-name registry \
           down ${IT_CLEANSE:---rmi all}
}    

double_check_logfile() {
    grep -Eq "[[:space:]]*#[[:space:]]+failure[[:space:]]+detail" "$1" \
        && echo "failure" || echo "success"
}

record() {
    cat > last_integration_test.json <<EOF
{
  "api_gitrev": "$1",
  "reg_gitrev": "$2",
  "status": "$3"
}
EOF
}

run() {
    cd docker || exit 1
    ( cd certs || exit 1 ; ./generate-certs.sh )
    export REG_API_IMAGE=nasapds/registry-api-service:latest
    docker image inspect nasapds/registry-api-service:latest >/dev/null
    docker compose \
           --ansi never \
           --profile int-registry-batch-loader \
           --project-name registry \
           up --detach --quiet-pull || return 5
    docker compose \
           --ansi never \
           --profile int-registry-batch-loader \
           --project-name registry \
           run --rm --no-TTY reg-api-integration-test-with-wait
    status=$?
    clean
    # shellcheck disable=SC2086 # because we need to return an int
    return $status
}

if [ $# -gt 1 ]
then
    echo "Usage: $0 [--verify]"
    exit 1
fi

if [ $# -eq 1 ] && [ "$1" != "--verify" ]
then
    echo "Error: Invalid argument '$1'"
    echo "Usage: $0 [--verify]"
    exit 1
fi

bdir=$(dirname "$(realpath "$0")")
rdir=$(realpath "$bdir/../..")
cd "$rdir" || exit 1
api_gitrev=$(git describe --always --abbrev=40 --dirty='+' --exclude '*')
tdir=$(mktemp -d)
# The EXIT pseudo-signal covers normal exits, errors, and interruptions (Ctrl+C)
trap 'rm -rf "$tdir"' EXIT
cd "$tdir" || exit 1
git clone --quiet https://github.com/NASA-PDS/registry.git
cd registry || exit 1
reg_gitrev=$(git describe --always --abbrev=40 --dirty='+' --exclude '*')

if [ "$1" == "--verify" ]; then
    echo "Running in VERIFY mode..."
    status=success
    cd "$tdir" || exit 1
    record "$api_gitrev" "$reg_gitrev" "$status"
    diff "$bdir"/last_integration_test.json "$tdir"/last_integration_test.json \
        || status=failure
    if [ "$status" == "failure" ]
    then
        echo "If you are reading this in the github actions log, then it seems "
        echo "this test cannot verify that this registry-api repository branch "
        echo "has been successfully tested. The first step at resolving this "
        echo "message is to run the script .github/workflows/integration_tests.sh "
        echo "locally. If it is successful, then commit all changes and push. "
        echo "Otherwise, fix any problems demonstrated from running the tests, "
        echo "then commit and push all changes when the script is successful."
    fi
else
    rm -f "$bdir"/last_integration_test.json
    cd "$rdir" || exit 1
    clean || exit 2
    build || exit 3
    cd "$tdir"/registry || exit 1
    run 2>&1 | tee "$rdir"/integration_tests.rpt.txt \
        && status=success || status=failure
    [ "$status" == "success" ] && \
        status=$(double_check_logfile "$rdir"/integration_tests.rpt.txt)
    cd "$bdir" || exit 1
    record "$api_gitrev" "$reg_gitrev" "$status"
    [ "$status" == "success" ] && rm "$rdir"/integration_tests.rpt.txt
fi

echo "Status: $status"
[ "$status" == "success" ] && exit 0 || exit 1
