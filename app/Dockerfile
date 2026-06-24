# Stage 1 - BUILD
# Use Maven image to compile the Java code into a JAR file
FROM maven:3.9-eclipse-temurin-21-jammy AS build

# Set working directory inside the build container
WORKDIR /app

# Copy pom.xml first - Maven downloads dependencies based on this
# This is done separately so Docker caches dependencies (faster rebuilds)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy the actual source code
COPY src ./src

# Compile and package into a JAR file
# -DskipTests skips running tests during build
RUN mvn package -DskipTests -q

# ─────────────────────────────────────────────────────────────

# Stage 2 - RUN
# Use a smaller image just to run the JAR (no Maven needed at runtime)
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy only the JAR from the build stage
COPY --from=build /app/target/order-tracker-1.0.0.jar app.jar

# This is the port our Spring Boot app listens on
EXPOSE 8080

# Command to start the app
# This is exactly what runs when Docker starts this container
ENTRYPOINT ["java", "-jar", "app.jar"]
