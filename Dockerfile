# ========== Сборка ==========
FROM eclipse-temurin:21-jdk-alpine AS builder

# Устанавливаем Maven
RUN apk add --no-cache maven

WORKDIR /app
COPY . .

# Сборка JAR
RUN mvn clean package -DskipTests

# ========== Запуск ==========
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Исправленный путь: /app/target/*.jar
COPY --from=builder /app/target/*.jar app.jar

# Railway использует $PORT
EXPOSE $PORT

ENTRYPOINT ["java", "-jar", "app.jar"]