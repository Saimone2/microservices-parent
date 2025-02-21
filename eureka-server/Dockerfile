FROM eclipse-temurin:21-jdk-alpine
LABEL authors="Saimone2"

WORKDIR /app
COPY target/eureka-server-1.0.0.jar app.jar

EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]