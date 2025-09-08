FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B -e

COPY src ./src

RUN mvn clean install -DskipTests -B -e

FROM openjdk:17-slim
WORKDIR /app

COPY --from=builder /app/target/card_management-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
