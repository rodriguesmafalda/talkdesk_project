FROM maven:3.8.1-jdk-11-slim AS MAVEN_BUILD

COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build/
RUN mvn clean package -DskipTests

FROM openjdk:11-jre-slim

WORKDIR /app
EXPOSE 8080
COPY --from=MAVEN_BUILD /build/target/calls-service-1.0-SNAPSHOT.jar /app
ENTRYPOINT ["java", "-jar", "calls-service-1.0-SNAPSHOT.jar"]

