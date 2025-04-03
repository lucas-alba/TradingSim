# Stage 1: Build
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/TradingSim-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
