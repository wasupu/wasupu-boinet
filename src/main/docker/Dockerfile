FROM java:8u111-jre-alpine

ADD maven/*.jar ./

ENTRYPOINT java -Xms1G -Xmx1G -jar boinet.jar --population=$POPULATION --companies=$COMPANIES --number-of-ticks=$NUMBER_OF_TICKS --stream-service-api-key=$STREAM_SERVICE_API_KEY --stream-service-namespace=$STREAM_SERVICE_NAMESPACE

EXPOSE 8080
