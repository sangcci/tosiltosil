# gradle build
FROM gradle:8.13.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar --no-daemon

# image build
FROM openjdk:17-slim
WORKDIR /app
# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/*.jar app.jar
ARG SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE $SPRING_PROFILES_ACTIVE
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]