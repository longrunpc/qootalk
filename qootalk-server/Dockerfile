FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle build.gradle ./
COPY module-common module-common
COPY module-domain module-domain
COPY module-application module-application
COPY module-infrastructure module-infrastructure
COPY module-presentation module-presentation

RUN chmod +x gradlew
RUN ./gradlew :module-presentation:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /workspace/module-presentation/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
