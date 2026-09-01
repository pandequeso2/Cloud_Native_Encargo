FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/roles_service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java","-jar","app.jar"]