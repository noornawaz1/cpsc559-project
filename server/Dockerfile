# Build the application
FROM gradle:8.13.0-jdk17 AS build
WORKDIR /app/server
COPY . /app/server
RUN ./gradlew build --no-daemon -x test

# Create slim image
FROM amazoncorretto:17-alpine
WORKDIR /app/server
ENV SPRING_PROFILES_ACTIVE=production
COPY --from=build /app/server/build/libs/*.jar app.jar
ENTRYPOINT [ "java", "-jar", "/app/server/app.jar" ]