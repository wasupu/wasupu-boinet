FROM java:8u111-jre-alpine

ADD maven/*.jar ./

ENTRYPOINT java $JAVA_OPTS -jar boinet.jar

EXPOSE 8080
