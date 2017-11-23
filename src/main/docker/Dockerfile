FROM java:8u111-jre-alpine

ADD maven/*.jar ./

ENTRYPOINT ["java", "-Xms1G", "-Xmx1G", "-jar", "boinet.jar"]

EXPOSE 8080
