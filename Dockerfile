FROM openjdk:17-jdk-alpine

# Stel de werkdirectory in
WORKDIR /app

# Kopieer de applicatiebestanden
COPY . .

# Kopieer de .env-file
COPY .env .env

# Installeer Maven wrapper
RUN ./mvnw clean package

# Kopieer de gegenereerde jar naar de app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Zorg ervoor dat Spring Boot de .env kan vinden
ENV SPRING_CONFIG_LOCATION=classpath:application.properties,./.env

# Start de applicatie
ENTRYPOINT ["java", "-jar", "/app.jar"]
