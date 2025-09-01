# build stage
FROM gradle:7.6-jdk17 AS build
WORKDIR /app
COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY src src
RUN chmod +x gradlew
RUN ./gradlew build -x test

# deploy stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dserver.port=${PORT}", "app.jar"]