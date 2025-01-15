FROM openjdk:17-jdk-alpine

# Stel de werkdirectory in
WORKDIR /app

# Kopieer alle bestanden naar de container
COPY . .

# Voer de Maven-build uit om de jar te genereren
RUN ./mvnw clean package

# Kopieer de gegenereerde jar naar de app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Start de applicatie
ENTRYPOINT ["java", "-jar", "/app.jar"]