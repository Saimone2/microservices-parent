FROM eclipse-temurin:21-jdk-alpine
LABEL authors="Saimone2"

WORKDIR /app
COPY target/gateway-service-1.0.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]