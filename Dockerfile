FROM maven:3.8.7-openjdk-20-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean install

FROM openjdk:20-slim
WORKDIR /app
COPY --from=build /app/target/classes /app/classes
CMD ["java", "-cp", "/app/classes", "com.tradesim.App"]
