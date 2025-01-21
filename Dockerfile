# Build stage
FROM maven:3.9.6-eclipse-temurin-17-focal AS build
WORKDIR /build

COPY pom.xml mvnw ./
COPY .mvn .mvn

RUN ./mvnw dependency:go-offline -B

RUN chmod +x mvnw

COPY src src

RUN ./mvnw package -DskipTests -B

# Run stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080


ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar" \
]