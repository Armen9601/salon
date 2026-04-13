# --- Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache deps first
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Build
COPY src ./src
RUN mvn -q -DskipTests package

# --- Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

ENV JAVA_OPTS=""
ENV TZ="Asia/Yerevan"

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]

