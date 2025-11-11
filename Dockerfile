FROM eclipse-temurin:21-jdk-alpine AS builder
RUN apk add --no-cache maven
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder target/demo-1.0.0.jar app.jar
EXPOSE $PORT
ENTRYPOINT ["java", "-jar", "app.jar"]