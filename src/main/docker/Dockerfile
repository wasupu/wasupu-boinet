FROM java:8u111-jre

ADD maven/*.jar ./

ENTRYPOINT java $JAVA_OPTS -jar boinet.jar

EXPOSE 8080
