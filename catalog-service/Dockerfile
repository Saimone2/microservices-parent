FROM eclipse-temurin:21-jdk-alpine
LABEL authors="Saimone2"

WORKDIR /app
COPY target/catalog-service-1.0.0.jar catalog-service.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "catalog-service.jar"]