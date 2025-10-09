# Base image
FROM openjdk:17-jdk-slim

# Copy the jar file
COPY target/*.jar /app.jar
# Expose the port Spring Boot runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
