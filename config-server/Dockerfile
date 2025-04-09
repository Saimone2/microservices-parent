FROM eclipse-temurin:21-jdk-alpine
LABEL authors="Saimone2"

WORKDIR /app
COPY target/config-server-1.0.0.jar config-server.jar

EXPOSE 8888
ENTRYPOINT ["java", "-jar", "config-server.jar"]