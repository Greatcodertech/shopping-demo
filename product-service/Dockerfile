
# Stage 1: Build the Java application
FROM maven:3.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy Maven configuration
COPY pom.xml .

# Copy Java source code
COPY src ./src

# Compile and create WAR file
RUN mvn clean package -DskipTests

# Stage 2: Run the application using Tomcat
FROM tomcat:10.1-jdk21-temurin

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the built application into Tomcat
COPY --from=build /app/target/product-service-1.0.war /usr/local/tomcat/webapps/ROOT.war

# Tomcat listens on port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]

