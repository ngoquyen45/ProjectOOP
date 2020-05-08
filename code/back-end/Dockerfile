FROM ubuntu AS ubuntu
FROM openjdk:8 AS java
VOLUME /tmp
ADD backend.war /app/backend.war
ADD application.properties /app/application.properties
ENTRYPOINT ["java" ,"-Dspring.config.location=classpath:file:/app/application.properties","-jar","/app/backend.war"]
EXPOSE 8443


