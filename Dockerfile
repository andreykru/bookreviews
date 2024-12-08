FROM openjdk:17-alpine

WORKDIR /app

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]

EXPOSE 8080
