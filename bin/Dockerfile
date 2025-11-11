FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/demo-1.0.0.jar app.jar
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
