# Build the application
FROM gradle:8.13.0-jdk17 AS build
WORKDIR /app/proxy
COPY . /app/proxy
RUN ./gradlew build --no-daemon -x test

# Create slim image
FROM amazoncorretto:17-alpine
WORKDIR /app/proxy
ENV SPRING_PROFILES_ACTIVE=production
COPY --from=build /app/proxy/build/libs/*.jar app.jar
ENTRYPOINT [ "java", "-jar", "/app/proxy/app.jar" ]