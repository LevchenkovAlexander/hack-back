# ========== Сборка ==========
FROM eclipse-temurin:21-jdk AS builder

# Устанавливаем Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY . .

# Сборка JAR
RUN mvn clean package -DskipTests

# ========== Запуск ==========
FROM eclipse-temurin:21-jre

WORKDIR /app

# Исправленный путь: /app/target/*.jar
COPY --from=builder /app/target/*.jar app.jar

# Railway использует $PORT
EXPOSE $PORT

ENTRYPOINT ["java", "-jar", "app.jar"]