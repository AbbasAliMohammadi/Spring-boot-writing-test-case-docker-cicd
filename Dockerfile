# Step 1: Build the JAR using Maven in a builder image with Java 21
FROM maven:3.9.0-eclipse-temurin-21 AS build

# Set working directory inside the container
WORKDIR /app

# Copy only the pom.xml and download dependencies (cached layer)
COPY pom.xml .

# Download dependencies (cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the JAR (skip tests to speed up build)
RUN mvn clean package -DskipTests

# Step 2: Create a smaller runtime image with Java 21 JDK
FROM eclipse-temurin:21-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot app runs on (change 1000 to 8080 if you prefer)
EXPOSE 1000

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
