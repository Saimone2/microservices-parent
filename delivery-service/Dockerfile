FROM eclipse-temurin:21-jdk-alpine
LABEL authors="Saimone2"

WORKDIR /app
COPY target/delivery-service-1.0.0.jar delivery-service.jar

EXPOSE 8086
ENTRYPOINT ["java", "-jar", "delivery-service.jar"]