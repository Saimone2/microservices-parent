FROM eclipse-temurin:21-jdk-alpine
LABEL authors="Saimone2"

WORKDIR /app
COPY target/payment-service-1.0.0.jar payment-service.jar

EXPOSE 8084
ENTRYPOINT ["java", "-jar", "payment-service.jar"]