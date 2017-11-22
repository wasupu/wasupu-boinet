#!/usr/bin/env bash

awsCli() {
    : ${AWS_ACCESS_KEY:?"AWS_ACCESS_KEY must be set"}
    : ${AWS_SECRET_KEY:?"AWS_SECRET_KEY must be set"}
    mkdir -p ~/.aws
    # image: mikesir87/aws-cli:1.11.172
    echo "docker run -i --rm -u $(id -u):$(id -g) -v ${PWD}:${PWD} -v $HOME/.aws:/tmp/.aws -e HOME=/tmp -e AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY} -e AWS_SECRET_ACCESS_KEY=${AWS_SECRET_KEY} mikesir87/aws-cli@sha256:dc1dc4cc10a74739cce778a883eb2ed906c3113b7a39f19f93eceb6ca205682e aws"
}

ecsCli() {
    : ${AWS_ACCESS_KEY:?"AWS_ACCESS_KEY must be set"}
    : ${AWS_SECRET_KEY:?"AWS_SECRET_KEY must be set"}
    mkdir -p ~/.ecs
    echo "docker run -i --rm -u $(id -u):$(id -g) -v $HOME/.ecs:/tmp/.ecs -e HOME=/tmp mnuma/docker-ecs-cli"
}

configureAws() {
    : ${AWS_ACCESS_KEY:?"AWS_ACCESS_KEY must be set"}
    : ${AWS_SECRET_KEY:?"AWS_SECRET_KEY must be set"}
    : ${AWS_DEFAULT_REGION:?"AWS_DEFAULT_REGION must be set"}

    $(awsCli) configure set aws_access_key_id ${AWS_ACCESS_KEY}
    $(awsCli) configure set aws_secret_access_key ${AWS_SECRET_KEY}
    $(awsCli) configure set default.region ${AWS_DEFAULT_REGION}
}

loginDockerRegistry() {
    : ${AWS_ACCESS_KEY:?"AWS_ACCESS_KEY must be set"}
    : ${AWS_SECRET_KEY:?"AWS_SECRET_KEY must be set"}
    : ${AWS_DEFAULT_REGION:?"AWS_DEFAULT_REGION must be set"}

    # image: mikesir87/aws-cli:1.11.172
    $($(awsCli) ecr get-login --region ${AWS_DEFAULT_REGION} --no-include-email)
}

createCloudwatchLogsGroup() {
    local groupName="$1"

    $(awsCli) logs create-log-group \
        --log-group-name ${groupName} \
        || true
}

createEcsTaskDefinition() {
    local family="$1"
    local taskDefinitionFile="$2"
    local awsDockerRegistry="$3"
    local awslogsGroupName="$4"
    local awsRegion="$5"
    local semaasApiKey="$6"
    local semaasNamespace="$7"

    sed -i.original "s~{{AWS_DOCKER_REGISTRY}}~${awsDockerRegistry}~" ${taskDefinitionFile}
    sed -i.original "s~{{AWSLOGS_GROUP}}~${awslogsGroupName}~" ${taskDefinitionFile}
    sed -i.original "s~{{AWSLOGS_REGION}}~${awsRegion}~" ${taskDefinitionFile}
    sed -i.original "s~{{SEMAAS_API_KEY}}~${semaasApiKey}~" ${taskDefinitionFile}
    sed -i.original "s~{{SEMAAS_NAMESPACE}}~${semaasNamespace}~" ${taskDefinitionFile}
    rm  ${taskDefinitionFile}.original

    $(awsCli) ecs register-task-definition \
        --family ${family} \
        --cli-input-json file:///${taskDefinitionFile}

    sed -i.original "s~${awsDockerRegistry}~\{\{AWS_DOCKER_REGISTRY\}\}~" ${taskDefinitionFile}
    sed -i.original "s~${awslogsGroupName}~\{\{AWSLOGS_GROUP\}\}~" ${taskDefinitionFile}
    sed -i.original "s~${awsRegion}~\{\{AWSLOGS_REGION\}\}~" ${taskDefinitionFile}
    sed -i.original "s~${semaasApiKey}~\{\{SEMAAS_API_KEY\}\}~" ${taskDefinitionFile}
    sed -i.original "s~${semaasNamespace}~\{\{SEMAAS_NAMESPACE\}\}~" ${taskDefinitionFile}
    rm  ${taskDefinitionFile}.original
}

runEcsTask() {
    local clusterName="$1"
    local taskDefinitionName="$2"

    $(awsCli) ecs run-task \
        --cluster ${clusterName} \
        --task-definition ${taskDefinitionName} \
        --count 1
}
