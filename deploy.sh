#!/usr/bin/env bash
set -o errexit
set -o pipefail
set -o allexport
[[ "${DEBUG}" == 'true' ]] && set -o xtrace

if [[ $# -gt 7 || $# -lt 4 ]]; then
    echo "Usage: ./deploy.sh <AWS_DOCKER_REGISTRY> <POPULATION> <COMPANIES> <SEED_CAPITAL> [<NUMBER_OF_TICKS>]"
    echo "Usage: ./deploy.sh <AWS_DOCKER_REGISTRY> <POPULATION> <COMPANIES> <SEED_CAPITAL> [<STREAM_SERVICE_API_KEY> <STREAM_SERVICE_NAMESPACE>] [<NUMBER_OF_TICKS>]"
    exit 1
fi
AWS_DOCKER_REGISTRY="$1"
POPULATION="$2"
COMPANIES="$3"
SEED_CAPITAL="$4"

if [[ $# -gt 5 ]]; then
    STREAM_SERVICE_API_KEY="$5"
    STREAM_SERVICE_NAMESPACE="$6"
fi

if [[ $# -eq 5 ]]; then
    NUMBER_OF_TICKS="$5"
elif [[ $# -eq 7 ]]; then
    NUMBER_OF_TICKS="$7"
fi

CURRENT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

source ${CURRENT_DIR}/deploy/aws-tools.sh
loginDockerRegistry
mvn docker:push -Ddocker.registry=${AWS_DOCKER_REGISTRY}

AWS_LOGS_GROUP="boinet-container-logs"
createCloudwatchLogsGroup ${AWS_LOGS_GROUP}

CLUSTER_NAME="boinet"
TASK_DEFINITION_NAME="boinet-task"
TASK_DEFINITION_FILE="${CURRENT_DIR}/deploy/ecs-task-definition.json"
createEcsTaskDefinition ${TASK_DEFINITION_NAME} ${TASK_DEFINITION_FILE} ${AWS_DOCKER_REGISTRY} ${AWS_LOGS_GROUP} ${AWS_DEFAULT_REGION} ${POPULATION} ${COMPANIES} ${SEED_CAPITAL} ${STREAM_SERVICE_API_KEY} ${STREAM_SERVICE_NAMESPACE} ${NUMBER_OF_TICKS}
runEcsTask ${CLUSTER_NAME} ${TASK_DEFINITION_NAME}
