FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY build/libs/api-gateway-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]