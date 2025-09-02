# Use OpenJDK 21 image with Maven installed for build
FROM eclipse-temurin:21-jdk as build

# Install Maven (using SDKMAN or package manager)
RUN apt-get update && apt-get install -y maven

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 1000

ENTRYPOINT ["java", "-jar", "app.jar"]
