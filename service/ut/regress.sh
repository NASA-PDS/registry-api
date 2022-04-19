#! /bin/bash

# make sure an instance of registry-api/service is runningn with the
# test data set

base=$(realpath $0)
base=$(dirname $base)
PYTHONPATH=${base}:${PYTHONPATH}
pytest -v $(find $base -name \*_ut.py)
