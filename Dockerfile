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

ENTRYPOINT ["java", "--enable-preview", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]