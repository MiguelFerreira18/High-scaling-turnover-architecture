FROM eclipse-temurin:25-jdk

COPY /target/Monolith-0.0.1-SNAPSHOT.jar /app/Monolith.jar

WORKDIR /app

EXPOSE 8080

ENTRYPOINT ["java","-jar","Monolith.jar"]
