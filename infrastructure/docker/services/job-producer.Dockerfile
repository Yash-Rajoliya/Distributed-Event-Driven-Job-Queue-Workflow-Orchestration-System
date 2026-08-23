FROM openjdk:17-jdk-slim
WORKDIR /app
COPY services/job-producer/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]