FROM openjdk:17-jdk-slim
WORKDIR /app
COPY services/retry-orchestrator/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]