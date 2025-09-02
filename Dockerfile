# Step 1: Build the JAR using Maven in a builder image
FROM maven:3.8.6-openjdk-17-slim AS build

# Set working directory inside the container
WORKDIR /app

# Copy only the pom.xml and download dependencies (cached layer)
COPY pom.xml .

# Download dependencies (will be cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the JAR (skip tests here if you want to speed up build)
RUN mvn clean package -DskipTests

# Step 2: Create a smaller runtime image
FROM openjdk:17-jdk-slim

# Create a directory for the app
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 1000

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
