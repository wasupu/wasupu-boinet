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
    $(awsCli) configure set aws_access_key_id ${AWS_ACCESS_KEY}
    $(awsCli) configure set aws_secret_access_key ${AWS_SECRET_KEY}
    $(awsCli) configure set default.region eu-west-1
}

configureEcs() {
    local clusterName="$1"

    $(ecsCli) configure \
        --region eu-west-1 \
        --access-key ${AWS_ACCESS_KEY} \
        --secret-key ${AWS_SECRET_KEY} \
        --cluster ${clusterName}
}

createEcsCluster() {
    local clusterName="$1"

}

removeEcsCluster() {
    $(ecsCli) down --force
}

createOrUpdateEcsService() {
    local clusterName="$1"
    local serviceName="$2"
    local taskDefinitionName="$3"

    set +e
    $(awsCli) ecs update-service \
        --cluster ${clusterName} \
        --service ${serviceName} \
        --task-definition ${taskDefinitionName} \
        --desired-count 1 \
        --deployment-configuration maximumPercent=100,minimumHealthyPercent=0
    local updateServiceExitCode=$?
    set -e

    if [[ ${updateServiceExitCode} != 0 ]]; then
        $(awsCli) ecs create-service \
            --cluster ${clusterName} \
            --service-name ${serviceName} \
            --task-definition ${taskDefinitionName} \
            --desired-count 1 \
            --deployment-configuration maximumPercent=100,minimumHealthyPercent=0
    fi

    $(awsCli) ecs wait services-stable \
        --cluster ${clusterName} \
        --services ${serviceName}
}

deleteEcsService() {
    local clusterName="$1"
    local serviceName="$2"

    $(awsCli) ecs update-service \
        --cluster ${clusterName} \
        --service ${serviceName} \
        --desired-count 0

    $(awsCli) ecs wait services-stable \
        --cluster ${clusterName} \
        --services ${serviceName}

    $(awsCli) ecs delete-service \
        --cluster ${clusterName} \
        --service ${serviceName}

    $(awsCli) ecs wait services-inactive \
        --cluster ${clusterName} \
        --services ${serviceName}
}

createEcsTaskDefinition() {
    local family="$1"
    local taskDefinitionFile="$2"

    $(awsCli) ecs register-task-definition \
        --family ${family} \
        --cli-input-json file:///${taskDefinitionFile}
}

deleteEcsTaskDefinitions() {
    local family="$1"
    local taskDefinitions=$($(awsCli) ecs list-task-definitions \
        --family-prefix ${family} \
        | $(jq) -r -c '.taskDefinitionArns' \
        | $(jq) -r '.[]')

    for taskDefinitionArn in ${taskDefinitions}; do
        $(awsCli) ecs deregister-task-definition \
            --task-definition ${taskDefinitionArn}
    done
}

showRunningContainers() {
    local clusterName="$1"

    $(ecsCli) ps --cluster ${clusterName}
}

# All this fucking shit is for getting ip address
getTaskArn() {
    local clusterName="$1"
    local serviceName="$2"

    echo "$($(awsCli) ecs list-tasks \
        --cluster ${clusterName} \
        --service-name ${serviceName} \
        | $(jq) -r '.taskArns[0]')"
}
