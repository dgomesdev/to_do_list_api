# Stage 1: Build the application
FROM gradle:7.5.1-jdk17 AS builder
WORKDIR /app
# Copy all project files into the container
COPY --chown=gradle:gradle . .

# Build the project and create the JAR file
RUN ./gradlew build --no-daemon

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port your app runs on
EXPOSE 8080

# Specify the default command to run the application
CMD ["java", "-jar", "app.jar"]
