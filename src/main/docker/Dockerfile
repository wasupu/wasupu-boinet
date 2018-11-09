FROM openjdk:11.0.1-slim

ADD maven/*.jar ./

ENTRYPOINT ["java", "-Xms1G", "-Xmx1G", "-jar", "boinet.jar"]

EXPOSE 8080
