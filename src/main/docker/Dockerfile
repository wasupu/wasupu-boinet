FROM java:8u111-jre-alpine

ADD maven/*.jar ./

ENTRYPOINT java -Xms1G -Xmx1G -jar boinet.jar $STREAM_SERVICE_API_KEY $STREAM_SERVICE_NAMESPACE

EXPOSE 8080
