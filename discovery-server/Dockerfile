FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY build/libs/discovery-server*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]