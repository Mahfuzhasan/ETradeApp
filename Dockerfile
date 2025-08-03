# -------- Build Stage --------
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -Pinclude-deps -DskipTests

# -------- Run Stage --------
FROM tomcat:9-jdk17-temurin

# Download PostgreSQL JDBC driver manually
ADD https://jdbc.postgresql.org/download/postgresql-42.3.1.jar /usr/local/tomcat/lib/

# Remove default webapps and deploy the built WAR
RUN rm -rf /usr/local/tomcat/webapps/ROOT*
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
