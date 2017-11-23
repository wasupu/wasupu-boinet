# wasupu boinet [![Build Status](https://travis-ci.org/rai22474/wasupu-boinet.svg?branch=master)](https://travis-ci.org/rai22474/wasupu-boinet)

![Image of the boinet](https://raw.githubusercontent.com/rai22474/wasupu-boinet/master/boinet-pic.png)

## How to

* To build the project:

```shell-script
mvn clean package
```

* To run the app:

```shell-script
docker run -t [-e STREAM_SERVICE_API_KEY=<STREAM_SERVICE_API_KEY>] [-e STREAM_SERVICE_NAMESPACE=<STREAM_SERVICE_NAMESPACE>] [-e NUMBER_OF_TICKS=<NUMBER_OF_TICKS>] --rm boinet
```

* To deploy the service:

```shell-script
./deploy.sh <AWS_ACCESS_KEY> <AWS_SECRET_KEY> <AWS_DEFAULT_REGION> <AWS_DOCKER_REGISTRY> <STREAM_SERVICE_API_KEY> <STREAM_SERVICE_NAMESPACE> [<NUMBER_OF_TICKS>]
```
