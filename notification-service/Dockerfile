FROM gradle:8.14-jdk21 AS build
WORKDIR /home/gradle/src

COPY --chown=gradle:gradle build.gradle settings.gradle ./
RUN gradle dependencies
COPY --chown=gradle:gradle src ./src
RUN gradle build -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]