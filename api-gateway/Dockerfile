FROM eclipse-temurin:21-jdk-alpine
LABEL authors="Saimone2"

WORKDIR /app
COPY target/api-gateway-1.0.0.jar api-gateway.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]