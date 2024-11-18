# Use an OpenJDK image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy Gradle files and the source code
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
COPY src ./src

# Build the project
RUN ./gradlew build --no-daemon

# Expose the port your app runs on
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "build/libs/your-application.jar"]