FROM openjdk:17-jdk-slim
WORKDIR /app
COPY services/worker-service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]