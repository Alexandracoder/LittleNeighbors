FROM eclipse-temurin:21-jdk-alpine

# Copia el archivo jar
COPY target/*.jar app.jar

# Define el punto de entrada pasando las variables de entorno correctamente
ENTRYPOINT ["java", "-Dserver.port=${PORT:-10000}", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]