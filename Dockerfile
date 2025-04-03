FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean install

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/classes /app/classes
CMD ["java", "-cp", "/app/classes", "com.tradesim.App"]
