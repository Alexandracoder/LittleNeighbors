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

ENTRYPOINT ["java", \
            "-Dserver.port=${PORT:-10000}", \
            "-Dspring.profiles.active=prod", \
            "-DSPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}", \
            "-DSPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}", \
            "-DSPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}", \
            "-DJWT_SECRET=${JWT_SECRET}", \
            "-jar", "app.jar"]