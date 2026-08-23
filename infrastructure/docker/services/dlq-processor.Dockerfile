FROM openjdk:17-jdk-slim
WORKDIR /app
COPY services/dlq-processor/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]