#!/usr/bin/env bash
set -o errexit
set -o pipefail
set -o xtrace

CURRENT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

source ${CURRENT_DIR}/scripts/ecs-tools.sh

