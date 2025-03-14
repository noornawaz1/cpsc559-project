# Build the application
FROM gradle:8.13.0-jdk17 AS build
COPY . /app
WORKDIR /app
RUN ./gradlew build --no-daemon

# Create slim image
FROM amazoncorretto:17-alpine
COPY --from=build /app/gradle/wrapper/gradle-wrapper.jar /tmp/app.jar
ENTRYPOINT [ "java", "-jar", "/tmp/app.jar" ]