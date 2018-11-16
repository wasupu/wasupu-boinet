#!/usr/bin/env bash
set -o errexit
set -o pipefail
set -o allexport
[[ "${DEBUG}" == 'true' ]] && set -o xtrace

if [[ $# -gt 5 || $# -lt 4 ]]; then
    echo "Usage: ./deploy.sh <POPULATION> <COMPANIES> <SEED_CAPITAL> <STREAM_SERVICE_NAMESPACE> [<NUMBER_OF_TICKS>]"
    exit 1
fi

POPULATION="$1"
COMPANIES="$2"
SEED_CAPITAL="$3"
STREAM_SERVICE_NAMESPACE="$4"
if [[ $# -eq 5 ]]; then
    NUMBER_OF_TICKS="$5"
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
registerTaskDefinitionWithStreamService ${TASK_DEFINITION_NAME}                         \
                                        ${TASK_DEFINITION_FILE}                         \
                                        ${AWS_LOGS_GROUP}                               \
                                        ${POPULATION}                                   \
                                        ${COMPANIES}                                    \
                                        ${SEED_CAPITAL}                                 \
                                        ${STREAM_SERVICE_NAMESPACE}                     \
                                        ${NUMBER_OF_TICKS}

runEcsTask ${CLUSTER_NAME} ${TASK_DEFINITION_NAME}
