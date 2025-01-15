FROM openjdk:17-jdk

# Stel de werkdirectory in
WORKDIR /app

# Kopieer de applicatiebestanden
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Kopieer de .env-file naar de werkdirectory
COPY .env /app/.env

# Start de applicatie
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
