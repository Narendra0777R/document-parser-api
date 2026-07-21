# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# This packages your app into a .jar file
RUN mvn clean package -DskipTests

# Stage 2: Create the final lightweight production image
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Install Tesseract OCR and the English + Marathi language packs
RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-eng \
    tesseract-ocr-mar \
    && rm -rf /var/lib/apt/lists/*

# Set the Tesseract Data Path so your Java code knows where to find the AI models in Linux
ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/4.00/tessdata/

# Copy the compiled .jar file from Stage 1
COPY --from=build /app/target/*.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-Xms256m", "-Xmx384m", "-XX:+UseG1GC", "-jar", "app.jar"]