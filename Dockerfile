#  Build the WAR using Maven + Java 8
FROM maven:3.9.11-eclipse-temurin-8 AS build

WORKDIR /app

# Copy pom.xml first (cache dependencies)
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build WAR file
RUN mvn clean package

# Run with Tomcat
FROM tomcat:8.5-jdk8-temurin

WORKDIR /usr/local/tomcat

# Remove default Tomcat apps
RUN rm -rf webapps/*

# Copy WAR file into Tomcat
COPY --from=build /usr/local/tomcat/target/Dashboard-1.0-SNAPSHOT.war ./webapps/ROOT.war

# Expose Tomcat port
EXPOSE 8083

# Start Tomcat
CMD ["catalina.sh", "run"]