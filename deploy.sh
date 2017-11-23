#!/usr/bin/env bash
set -o errexit
set -o pipefail
set -o allexport
[[ "${DEBUG}" == 'true' ]] && set -o xtrace

if [[ $# -gt 9 || $# -lt 6 ]]; then
    echo "Usage: ./deploy.sh <AWS_ACCESS_KEY> <AWS_SECRET_KEY> <AWS_DEFAULT_REGION> <AWS_DOCKER_REGISTRY> <POPULATION> <COMPANIES> [<NUMBER_OF_TICKS>]"
    echo "Usage: ./deploy.sh <AWS_ACCESS_KEY> <AWS_SECRET_KEY> <AWS_DEFAULT_REGION> <AWS_DOCKER_REGISTRY> <POPULATION> <COMPANIES> [<STREAM_SERVICE_API_KEY> <STREAM_SERVICE_NAMESPACE>] [<NUMBER_OF_TICKS>]"
    exit 1
fi
AWS_ACCESS_KEY="$1"
AWS_SECRET_KEY="$2"
AWS_DEFAULT_REGION="$3"
AWS_DOCKER_REGISTRY="$4"
POPULATION="$5"
COMPANIES="$6"

if [[ $# -gt 7 ]]; then
    STREAM_SERVICE_API_KEY="$7"
    STREAM_SERVICE_NAMESPACE="$8"
fi

if [[ $# -eq 7 ]]; then
    NUMBER_OF_TICKS="$7"
elif [[ $# -eq 9 ]]; then
    NUMBER_OF_TICKS="$9"
fi

CURRENT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

source ${CURRENT_DIR}/deploy/aws-tools.sh
loginDockerRegistry
mvn docker:push -Ddocker.registry=${AWS_DOCKER_REGISTRY}

configureAws
AWS_LOGS_GROUP="boinet-container-logs"
createCloudwatchLogsGroup ${AWS_LOGS_GROUP}

CLUSTER_NAME="boinet-test"
TASK_DEFINITION_NAME="boinet-test-task"
TASK_DEFINITION_FILE="${CURRENT_DIR}/deploy/ecs-task-definition.json"
createEcsTaskDefinition ${TASK_DEFINITION_NAME} ${TASK_DEFINITION_FILE} ${AWS_DOCKER_REGISTRY} ${AWS_LOGS_GROUP} ${AWS_DEFAULT_REGION} ${POPULATION} ${COMPANIES} ${STREAM_SERVICE_API_KEY} ${STREAM_SERVICE_NAMESPACE} ${NUMBER_OF_TICKS}
runEcsTask ${CLUSTER_NAME} ${TASK_DEFINITION_NAME}
