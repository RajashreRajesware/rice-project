FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/model-project-0.0.1-SNAPSHOT.jar /app/model-project-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar" ,"model-project-0.0.1-SNAPSHOT.jar"]