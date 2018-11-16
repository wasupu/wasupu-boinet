#!/usr/bin/env bash

function awsCli() {
    docker run \
        --rm \
        $(tty &>/dev/null && echo "-i") \
        -u $(id -u):$(id -g) \
        -e AWS_PROFILE \
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

function registerTaskDefinition() {
    : ${ECS_TASK_EXECUTION_ROLE_ARN:?"ECS_TASK_EXECUTION_ROLE_ARN must be set"}
    : ${AWS_DOCKER_REGISTRY:?"AWS_DOCKER_REGISTRY must be set"}
    : ${AWS_DEFAULT_REGION:?"AWS_DEFAULT_REGION must be set"}
    : ${STREAM_SERVICE_TRUSTSTORE_PASSPHRASE:?"STREAM_SERVICE_TRUSTSTORE_PASSPHRASE must be set"}
    : ${STREAM_SERVICE_KEYSTORE_PASSPHRASE:?"STREAM_SERVICE_KEYSTORE_PASSPHRASE must be set"}

    local family="$1"
    local taskDefinitionFile="$2"
    local awslogsGroupName="$3"
    local population="$4"
    local companies="$5"
    local seedCapital="$6"
    if [[ $# -eq 7 ]]; then
        local numberOfTicks="$7"
    fi

    sed -i.original "s~{{AWS_DOCKER_REGISTRY}}~${AWS_DOCKER_REGISTRY}~" ${taskDefinitionFile}
    sed -i.original "s~{{AWSLOGS_GROUP}}~${awslogsGroupName}~" ${taskDefinitionFile}
    sed -i.original "s~{{AWSLOGS_REGION}}~${AWS_DEFAULT_REGION}~" ${taskDefinitionFile}
    sed -i.original "s~{{STREAM_SERVICE_NAMESPACE}}~${streamServiceNamespace}~" ${taskDefinitionFile}
    sed -i.original "s~{{STREAM_SERVICE_TRUSTSTORE_PASSPHRASE}}~${STREAM_SERVICE_TRUSTSTORE_PASSPHRASE}~" ${taskDefinitionFile}
    sed -i.original "s~{{STREAM_SERVICE_KEYSTORE_PASSPHRASE}}~${STREAM_SERVICE_KEYSTORE_PASSPHRASE}~" ${taskDefinitionFile}
    sed -i.original "s~{{SEED_CAPITAL}}~${seedCapital}~" ${taskDefinitionFile}
    sed -i.original "s~{{POPULATION}}~${population}~" ${taskDefinitionFile}
    sed -i.original "s~{{COMPANIES}}~${companies}~" ${taskDefinitionFile}
    sed -i.original "s~{{NUMBER_OF_TICKS}}~${numberOfTicks}~" ${taskDefinitionFile}
    rm  ${taskDefinitionFile}.original

    awsCli ecs register-task-definition                     \
        --family ${family}                                  \
        --execution-role-arn ${ECS_TASK_EXECUTION_ROLE_ARN} \
        --cli-input-json file:///${taskDefinitionFile}

    sed -i.original "s~${AWS_DOCKER_REGISTRY}~\{\{AWS_DOCKER_REGISTRY\}\}~" ${taskDefinitionFile}
    sed -i.original "s~${awslogsGroupName}~\{\{AWSLOGS_GROUP\}\}~" ${taskDefinitionFile}
    sed -i.original "s~${AWS_DEFAULT_REGION}~\{\{AWSLOGS_REGION\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--client-key-store-passphrase=${STREAM_SERVICE_KEYSTORE_PASSPHRASE}~--client-key-store-passphrase=\{\{STREAM_SERVICE_KEYSTORE_PASSPHRASE\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--server-key-store-passphrase=${STREAM_SERVICE_TRUSTSTORE_PASSPHRASE}~--server-key-store-passphrase=\{\{STREAM_SERVICE_TRUSTSTORE_PASSPHRASE\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--stream-service-namespace=${streamServiceNamespace}~--stream-service-namespace=\{\{STREAM_SERVICE_NAMESPACE\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--companies=${companies}~--companies=\{\{COMPANIES\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--population=${population}~--population=\{\{POPULATION\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--seed-capital=${seedCapital}~--seed-capital=\{\{SEED_CAPITAL\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--number-of-ticks=${numberOfTicks}~--number-of-ticks=\{\{NUMBER_OF_TICKS\}\}~" ${taskDefinitionFile}
    rm  ${taskDefinitionFile}.original
}

function registerTaskDefinitionWithStreamService() {
    : ${ECS_TASK_EXECUTION_ROLE_ARN:?"ECS_TASK_EXECUTION_ROLE_ARN must be set"}
    : ${AWS_DOCKER_REGISTRY:?"AWS_DOCKER_REGISTRY must be set"}
    : ${AWS_DEFAULT_REGION:?"AWS_DEFAULT_REGION must be set"}
    : ${STREAM_SERVICE_TRUSTSTORE_PASSPHRASE:?"STREAM_SERVICE_TRUSTSTORE_PASSPHRASE must be set"}
    : ${STREAM_SERVICE_KEYSTORE_PASSPHRASE:?"STREAM_SERVICE_KEYSTORE_PASSPHRASE must be set"}

    local family="$1"
    local taskDefinitionFile="$2"
    local awslogsGroupName="$3"
    local population="$4"
    local companies="$5"
    local seedCapital="$6"
    local streamServiceNamespace="$7"
    if [[ $# -eq 8 ]]; then
        local numberOfTicks="$8"
    fi

    sed -i.original "s~{{AWS_DOCKER_REGISTRY}}~${AWS_DOCKER_REGISTRY}~" ${taskDefinitionFile}
    sed -i.original "s~{{AWSLOGS_GROUP}}~${awslogsGroupName}~" ${taskDefinitionFile}
    sed -i.original "s~{{AWSLOGS_REGION}}~${AWS_DEFAULT_REGION}~" ${taskDefinitionFile}
    sed -i.original "s~{{STREAM_SERVICE_NAMESPACE}}~${streamServiceNamespace}~" ${taskDefinitionFile}
    sed -i.original "s~{{STREAM_SERVICE_TRUSTSTORE_PASSPHRASE}}~${STREAM_SERVICE_TRUSTSTORE_PASSPHRASE}~" ${taskDefinitionFile}
    sed -i.original "s~{{STREAM_SERVICE_KEYSTORE_PASSPHRASE}}~${STREAM_SERVICE_KEYSTORE_PASSPHRASE}~" ${taskDefinitionFile}
    sed -i.original "s~{{SEED_CAPITAL}}~${seedCapital}~" ${taskDefinitionFile}
    sed -i.original "s~{{POPULATION}}~${population}~" ${taskDefinitionFile}
    sed -i.original "s~{{COMPANIES}}~${companies}~" ${taskDefinitionFile}
    sed -i.original "s~{{NUMBER_OF_TICKS}}~${numberOfTicks}~" ${taskDefinitionFile}
    rm  ${taskDefinitionFile}.original

    awsCli ecs register-task-definition                     \
        --family ${family}                                  \
        --execution-role-arn ${ECS_TASK_EXECUTION_ROLE_ARN} \
        --cli-input-json file:///${taskDefinitionFile}

    sed -i.original "s~${AWS_DOCKER_REGISTRY}~\{\{AWS_DOCKER_REGISTRY\}\}~" ${taskDefinitionFile}
    sed -i.original "s~${awslogsGroupName}~\{\{AWSLOGS_GROUP\}\}~" ${taskDefinitionFile}
    sed -i.original "s~${AWS_DEFAULT_REGION}~\{\{AWSLOGS_REGION\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--client-key-store-passphrase=${STREAM_SERVICE_KEYSTORE_PASSPHRASE}~--client-key-store-passphrase=\{\{STREAM_SERVICE_KEYSTORE_PASSPHRASE\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--server-key-store-passphrase=${STREAM_SERVICE_TRUSTSTORE_PASSPHRASE}~--server-key-store-passphrase=\{\{STREAM_SERVICE_TRUSTSTORE_PASSPHRASE\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--stream-service-namespace=${streamServiceNamespace}~--stream-service-namespace=\{\{STREAM_SERVICE_NAMESPACE\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--companies=${companies}~--companies=\{\{COMPANIES\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--population=${population}~--population=\{\{POPULATION\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--seed-capital=${seedCapital}~--seed-capital=\{\{SEED_CAPITAL\}\}~" ${taskDefinitionFile}
    sed -i.original "s~--number-of-ticks=${numberOfTicks}~--number-of-ticks=\{\{NUMBER_OF_TICKS\}\}~" ${taskDefinitionFile}
    rm  ${taskDefinitionFile}.original
}

function runEcsTask() {
    : ${ECS_TASK_SUBNETS:?"ECS_TASK_SUBNETS must be set"}
    : ${ECS_TASK_SECURITY_GROUPS:?"ECS_TASK_SECURITY_GROUPS must be set"}

    local clusterName="$1"
    local taskDefinitionName="$2"

    awsCli ecs run-task                         \
        --cluster ${clusterName}                \
        --task-definition ${taskDefinitionName} \
        --count 1                               \
        --launch-type FARGATE                   \
        --network-configuration "awsvpcConfiguration={subnets=[${ECS_TASK_SUBNETS}],securityGroups=[${ECS_TASK_SECURITY_GROUPS}],assignPublicIp=DISABLED}"
}
