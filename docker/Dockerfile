FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
ARG SERVICE
COPY gradle gradle
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY settings.gradle build.gradle ./
COPY contracts contracts
COPY api-gateway api-gateway
COPY solicitud-service solicitud-service
COPY evaluacion-service evaluacion-service
COPY auth-service auth-service
RUN chmod +x gradlew && ./gradlew :${SERVICE}:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
ARG SERVICE
COPY --from=build /workspace/${SERVICE}/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
