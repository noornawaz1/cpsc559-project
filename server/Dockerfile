# Build the application
FROM gradle:8.13.0-jdk17 AS build
COPY . /app
WORKDIR /app
RUN ./gradlew build --no-daemon

# Create slim image
FROM amazoncorretto:17-alpine
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=production
COPY --from=build /app/build/lib/*.jar app.jar
ENTRYPOINT [ "java", "-jar", "/tmp/app.jar" ]