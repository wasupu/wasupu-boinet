#!/usr/bin/env bash
set -o errexit
set -o pipefail
set -o allexport
[[ "${DEBUG}" == 'true' ]] && set -o xtrace

if [[ $# -gt 4 || $# -lt 3 ]]; then
    echo "Usage: ./deploy.sh <POPULATION> <COMPANIES> <SEED_CAPITAL> [<NUMBER_OF_TICKS>]"
    exit 1
fi

POPULATION="$1"
COMPANIES="$2"
SEED_CAPITAL="$3"
if [[ $# -eq 4 ]]; then
    NUMBER_OF_TICKS="$4"
fi

readonly CURRENT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

source ${CURRENT_DIR}/deploy/aws-tools.sh
loginDockerRegistry
: ${AWS_DOCKER_REGISTRY:?"AWS_DOCKER_REGISTRY must be set"}
mvn docker:push -Ddocker.registry=${AWS_DOCKER_REGISTRY}

readonly AWS_LOGS_GROUP="boinet-container-logs"
createCloudwatchLogsGroup ${AWS_LOGS_GROUP}

readonly CLUSTER_NAME="boinet"
readonly TASK_DEFINITION_NAME="boinet-task"
readonly TASK_DEFINITION_FILE="${CURRENT_DIR}/deploy/ecs-task-definition.json"
registerTaskDefinition ${TASK_DEFINITION_NAME}                         \
                       ${TASK_DEFINITION_FILE}                         \
                       ${AWS_LOGS_GROUP}                               \
                       ${POPULATION}                                   \
                       ${COMPANIES}                                    \
                       ${SEED_CAPITAL}                                 \
                       ${NUMBER_OF_TICKS}

runEcsTask ${CLUSTER_NAME} ${TASK_DEFINITION_NAME}
