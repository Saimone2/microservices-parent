FROM eclipse-temurin:21-jdk-alpine
LABEL authors="Saimone2"

WORKDIR /app
COPY target/auth-service-1.0.0.jar auth-service.jar

EXPOSE 8087
ENTRYPOINT ["java", "-jar", "auth-service.jar"]