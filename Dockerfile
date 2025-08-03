# -------- Build Stage --------
FROM maven:3.9.4-eclipse-temurin-17 AS builder

# Set working directory inside the container
WORKDIR /app

# Copy the entire project into the container
COPY . .

# Build the project and package it into a WAR file
RUN mvn clean package -Pinclude-deps -DskipTests

# -------- Run Stage --------
FROM tomcat:9-jdk17-temurin

# Download PostgreSQL JDBC driver and place it in Tomcat's lib directory
ADD https://jdbc.postgresql.org/download/postgresql-42.3.1.jar /usr/local/tomcat/lib/

# Remove the default webapps including ROOT
RUN rm -rf /usr/local/tomcat/webapps/ROOT*

# Copy the WAR file from the builder stage and rename it to ROOT.war
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
