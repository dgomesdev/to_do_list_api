# Use an OpenJDK base image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the application JAR file
COPY target/*.jar app.jar

# Expose the port that the application will run on
EXPOSE 8080

# Set the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
