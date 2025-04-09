FROM eclipse-temurin:21-jdk-alpine
LABEL authors="Saimone2"

WORKDIR /app
COPY target/inventory-service-1.0.0.jar inventory-service.jar

EXPOSE 8085
ENTRYPOINT ["java", "-jar", "inventory-service.jar"]