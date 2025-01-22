# Gebruik de OpenJDK 17 base image
FROM openjdk:17-jdk

# Stel de werkdirectory in
WORKDIR /app

# Kopieer de Maven-wrapper en projectbestanden naar de container
COPY . .

# Zorg dat de Maven-wrapper uitvoerbaar is
RUN chmod +x mvnw

# Voer de Maven build uit (de .jar wordt gegenereerd in de target-directory)
RUN ./mvnw clean package -DskipTests

# Kopieer het gegenereerde jar-bestand naar de werkdirectory
RUN cp target/demo-0.0.1-SNAPSHOT.jar app.jar

# Kopieer de .env-file naar de container
COPY .env /app/.env

# Start de applicatie
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
