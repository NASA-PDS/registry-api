#! /usr/bin/env bash
#
# requires: docker, git, mvn, and shellcheck
#
# docker - builds the registry api image, uses compose to run a host of services
# git clones registry repo
# jq - bash JSON tool
# mvn - to build the jar file for the current source registry api source code
# "shellcheck" - linter to keep this script clean
#

build() {
    mvn --quiet clean package
    jar_file="$(find ./service/target/ -maxdepth 1 -regextype posix-extended -regex '.*/registry-api-service-[0-9]+\.[0-9]+\.[0-9]+(-SNAPSHOT)?\.jar')"
    docker build --build-arg api_jar="$jar_file" -t nasapds/registry-api-service:latest -f docker/Dockerfile .
}

clean() {
    # shellcheck disable=SC2086 # for correct docker interpretation
    docker compose \
           --ansi never \
           --profile int-registry-batch-loader \
           --project-name registry \
           down ${IT_CLEANSE:---rmi all}
}    

double_check_logfile() {
    echo "everything looked ok, so double check postman logs"
    [ -s "$1" ] || { echo "$1 is an empty file"; return 1; }
    grep -Eq "[[:space:]]*#[[:space:]]+failure[[:space:]]+detail" "$1" \
        && { echo "postman log file reported failures" ; return 2; }
    return 0
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
    echo "launch services"
    docker compose \
           --ansi never \
           --profile int-registry-batch-loader \
           --project-name registry \
           up --detach --quiet-pull || return 5
    echo "launch tests"
    docker compose \
           --ansi never \
           --profile int-registry-batch-loader \
           --project-name registry \
           run --rm --no-TTY reg-api-integration-test-with-wait
    status=$?
    echo "run status: ${status}"
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
    status=failure
    cd "$tdir" || exit 1
    record "$api_gitrev" "$reg_gitrev" "$status"
    cd "$rdir" || exit 1
    test_key=$(jq -r '.api_gitrev' "$bdir"/last_integration_test.json | sed 's/+$//')
    files=$(git diff --name-only -r "$test_key")
    # shellcheck disable=SC2046 # because comparing integers
    if [ $(echo "$files" | wc -l) -eq 1 ]
    then
        if [ "$files" == ".github/workflows/last_integration_test.json" ]
        then
            if [ -s "$files" ]
            then
                # do a one line diff from last test run
                # look at additions or subtractions
                # ignore --- and +++ because those are the filenames
                # ignore the api_gitrev because that must be different
                # count all other changes
                # if there are none, then status is meaningful
                # shellcheck disable=SC2126 # because simpler to understand
                if [ $(git diff -U0 -r "$test_key" | \
                           grep "^[+-]" | \
                           grep -v "^---" | \
                           grep -v "^+++" | \
                           grep -v "api_gitrev" | \
                           wc -l) == 0 ]
                then
                    status=$(jq -r '.status' "$bdir"/last_integration_test.json)
                    echo "Found the I&T test to be: ${status}"
                else
                    git diff -r "$test_key"
                fi
            else
                echo "Reporting file is empty"
            fi
        else
            echo "the file changed was not for I&T: $files"
        fi
    else
        echo "commit contains edits beyond those of last_integration_test.json"
        echo "files changed: $files"
    fi
    if [ "$status" == "failure" ]
    then
        echo
        echo "If you are reading this in the github actions log, then it seems"
        echo "this test cannot verify that this registry-api repository branch"
        echo "has been successfully tested. The first step at resolving this"
        echo "message is to run the script .github/workflows/integration_tests.sh"
        echo "locally. If it is successful, then commit all changes and push."
        echo "Otherwise, fix any problems demonstrated from running the tests,"
        echo "then commit and push all changes when the script is successful."
        echo "Once commited, run this script again to generate the single file"
        echo "last_integration_test.json, commit it, and push it."
        echo
        echo "Note: there are timing tests that can cause temporary failures."
        echo "      If those failures occur, just run the script again until"
        echo "      a success is achived."
        echo
        echo "Note: to determine if the latest commit will pass, run the script"
        echo "      with 'integration_tests.sh --verify'"
    else
        echo "Verified tests completed and successful"
    fi
else
    cd "$rdir" || exit 1
    clean || exit 2
    build || exit 3
    cd "$tdir"/registry || exit 1
    ( set -o pipefail ; run 2>&1 | tee "$rdir"/integration_tests.rpt.txt ) \
        && status=success || status=failure
    if [ "$status" == "success" ]
    then
        double_check_logfile "$rdir"/integration_tests.rpt.txt \
            || status=failure
    else
        echo "docker run did not return success"
    fi
    cd "$bdir" || exit 1
    record "$api_gitrev" "$reg_gitrev" "$status"
    [ "$status" == "success" ] && rm "$rdir"/integration_tests.rpt.txt
fi

echo "Status: $status"
[ "$status" == "success" ] && exit 0 || exit 1
