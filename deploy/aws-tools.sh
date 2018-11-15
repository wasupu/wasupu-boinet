#!/usr/bin/env bash

function awsCli() {
    docker run \
        --rm \
        $(tty &>/dev/null && echo "-i") \
        -u $(id -u):$(id -g) \
        -e AWS_PROFILE \
        -e AWS_CONTAINER_CREDENTIALS_RELATIVE_URI \
        -v ${PWD}:${PWD} \
        -v ${HOME}/.aws:/.aws \
        mesosphere/aws-cli@sha256:fb590357c2cf74e868cf110ede38daf4bdf0ebd1bdf36c21d256aa33ab22fa6e \
        "$@"
}

function loginDockerRegistry() {
    : ${AWS_DEFAULT_REGION:?"AWS_DEFAULT_REGION must be set"}

    $(awsCli ecr get-login --region ${AWS_DEFAULT_REGION} --no-include-email)
}

function createCloudwatchLogsGroup() {
    local groupName="$1"

    awsCli logs create-log-group \
        --log-group-name ${groupName} \
        || true
}

function createEcsTaskDefinition() {
    local family="$1"
    local taskDefinitionFile="$2"
    local awsDockerRegistry="$3"
    local awslogsGroupName="$4"
    local awsRegion="$5"
    local population="$6"
    local companies="$7"
    local seedCapital="$8"
    if [[ $# -gt 9 ]]; then
        local streamServiceApiKey="$9"
        local streamServiceNamespace="${10}"
    fi
    if [[ $# -eq 9 ]]; then
        local numberOfTicks="$9"
    elif [[ $# -eq 11 ]]; then
        local numberOfTicks="${11}"
    fi

    sed -i.original "s~{{AWS_DOCKER_REGISTRY}}~${awsDockerRegistry}~" ${taskDefinitionFile}
    sed -i.original "s~{{AWSLOGS_GROUP}}~${awslogsGroupName}~" ${taskDefinitionFile}
    sed -i.original "s~{{AWSLOGS_REGION}}~${awsRegion}~" ${taskDefinitionFile}
    sed -i.original "s~{{STREAM_SERVICE_API_KEY}}~${streamServiceApiKey}~" ${taskDefinitionFile}
    sed -i.original "s~{{STREAM_SERVICE_NAMESPACE}}~${streamServiceNamespace}~" ${taskDefinitionFile}
    sed -i.original "s~{{SEED_CAPITAL}}~${seedCapital}~" ${taskDefinitionFile}
    sed -i.original "s~{{POPULATION}}~${population}~" ${taskDefinitionFile}
    sed -i.original "s~{{COMPANIES}}~${companies}~" ${taskDefinitionFile}
    sed -i.original "s~{{NUMBER_OF_TICKS}}~${numberOfTicks}~" ${taskDefinitionFile}
    rm  ${taskDefinitionFile}.original

    awsCli ecs register-task-definition                                             \
        --family ${family}                                                          \
        --execution-role-arn arn:aws:iam::809230366679:role/ecsTaskExecutionRole    \
        --cli-input-json file:///${taskDefinitionFile}

    sed -i.original "s~${awsDockerRegistry}~\{\{AWS_DOCKER_REGISTRY\}\}~" ${taskDefinitionFile}
    sed -i.original "s~${awslogsGroupName}~\{\{AWSLOGS_GROUP\}\}~" ${taskDefinitionFile}
    sed -i.original "s~${awsRegion}~\{\{AWSLOGS_REGION\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--stream-service-api-key=${streamServiceApiKey}~--stream-service-api-key=\{\{STREAM_SERVICE_API_KEY\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--stream-service-namespace=${streamServiceNamespace}~--stream-service-namespace=\{\{STREAM_SERVICE_NAMESPACE\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--companies=${companies}~--companies=\{\{COMPANIES\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--population=${population}~--population=\{\{POPULATION\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--seed-capital=${seedCapital}~--seed-capital=\{\{SEED_CAPITAL\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--number-of-ticks=${numberOfTicks}~--number-of-ticks=\{\{NUMBER_OF_TICKS\}\}~" ${taskDefinitionFile}
    rm  ${taskDefinitionFile}.original
}

function runEcsTask() {
    local clusterName="$1"
    local taskDefinitionName="$2"

    awsCli ecs run-task                         \
        --cluster ${clusterName}                \
        --task-definition ${taskDefinitionName} \
        --count 1                               \
        --launch-type FARGATE
}
