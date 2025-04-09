FROM eclipse-temurin:21-jdk-alpine
LABEL authors="Saimone2"

WORKDIR /app
COPY target/eureka-server-1.0.0.jar eureka-server.jar

EXPOSE 8761
ENTRYPOINT ["java", "-jar", "eureka-server.jar"]