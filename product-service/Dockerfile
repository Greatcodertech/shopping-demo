FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM tomcat:10.1-jdk21-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /build/target/product-service-1.0.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
