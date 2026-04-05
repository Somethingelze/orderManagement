FROM gradle:8.14-jdk21 AS build
WORKDIR /home/gradle/src

# 1. Копируем ТОЛЬКО файлы конфигурации сборки
COPY --chown=gradle:gradle build.gradle settings.gradle ./

# 2. Скачиваем все зависимости.
# Этот слой закешируется и не будет пересобираться, пока ты не изменишь build.gradle
RUN gradle dependencies --no-daemon || true

# 3. Копируем исходный код
COPY --chown=gradle:gradle src ./src

# 4. Собираем проект.
# Теперь Gradle возьмет все библиотеки из локального кэша внутри образа
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]