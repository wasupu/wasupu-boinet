#!/usr/bin/env bash
set -o errexit
set -o pipefail
set -o allexport
[[ "${DEBUG}" == 'true' ]] && set -o xtrace

if [[ $# -ne 9 && $# -ne 8 ]]; then
    echo "Usage: ./deploy.sh <AWS_ACCESS_KEY> <AWS_SECRET_KEY> <AWS_DEFAULT_REGION> <AWS_DOCKER_REGISTRY> <STREAM_SERVICE_API_KEY> <STREAM_SERVICE_NAMESPACE> <POPULATION> <COMPANIES> [<NUMBER_OF_TICKS>]"
    exit 1
fi
AWS_ACCESS_KEY="$1"
AWS_SECRET_KEY="$2"
AWS_DEFAULT_REGION="$3"
AWS_DOCKER_REGISTRY="$4"
STREAM_SERVICE_API_KEY="$5"
STREAM_SERVICE_NAMESPACE="$6"
POPULATION="$7"
COMPANIES="$8"
NUMBER_OF_TICKS="$9"

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
createEcsTaskDefinition ${TASK_DEFINITION_NAME} ${TASK_DEFINITION_FILE} ${AWS_DOCKER_REGISTRY} ${AWS_LOGS_GROUP} ${AWS_DEFAULT_REGION} ${STREAM_SERVICE_API_KEY} ${STREAM_SERVICE_NAMESPACE} ${POPULATION} ${COMPANIES} ${NUMBER_OF_TICKS}
runEcsTask ${CLUSTER_NAME} ${TASK_DEFINITION_NAME}
