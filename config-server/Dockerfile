FROM eclipse-temurin:21-jdk-alpine
LABEL authors="Saimone2"

WORKDIR /app
COPY target/config-server-1.0.0.jar app.jar

EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]