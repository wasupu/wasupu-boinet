# wasupu boinet

[![Build Status](https://travis-ci.org/rai22474/wasupu-boinet.svg?branch=master)](https://travis-ci.org/rai22474/wasupu-boinet)
![Image of the boinet](https://raw.githubusercontent.com/rai22474/wasupu-boinet/master/boinet-pic.png)

## How to

* To build the project:

`mvn clean package`

* To run the app:

`docker run --rm wasupu/boinet`

* To deploy ECS cluster:

`./create-infrastructure.sh <AWS_ACCESS_KEY> <AWS_SECRET_KEY>`

* To deploy the service:

`./deploy.sh <AWS_ACCESS_KEY> <AWS_SECRET_KEY>`
