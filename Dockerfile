FROM amazoncorretto:17
WORKDIR /app
COPY "target/basic-rate-limiter-0.0.1-SNAPSHOT.jar" app.jar
EXPOSE 8188
ENTRYPOINT ["java", "-jar", "app.jar"]