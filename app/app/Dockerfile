# Base image
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the project files (Make sure to use the correct .jar file)
COPY target/app-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port your app runs on (assuming 9090 is the port your app uses)
EXPOSE 9090

# Command to run your application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
