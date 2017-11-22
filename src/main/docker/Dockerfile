FROM java:8u111-jre-alpine

ADD maven/*.jar ./

ENTRYPOINT java $JAVA_OPTS -jar boinet.jar $SEMAAS_API_KEY $SEMAAS_NAMESPACE

EXPOSE 8080
