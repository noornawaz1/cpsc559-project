# Build the application
FROM gradle:8.13.0-jdk17 AS build
COPY . /app
WORKDIR /app
RUN ./gradlew build --no-daemon -x test

# Create slim image
FROM amazoncorretto:17-alpine
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=production
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT [ "java", "-jar", "/app/app.jar" ]