FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src

RUN mvn package -DskipTests -B -Dcompiler.arg=--enable-preview


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app


COPY --from=builder /app/target/*.jar app.jar

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Dspring.datasource.url=${SPRING_DATASOURCE_URL}", "-Dspring.datasource.username=${SPRING_DATASOURCE_USERNAME}", "-Dspring.datasource.password=${SPRING_DATASOURCE_PASSWORD}", "-jar", "app.jar"]