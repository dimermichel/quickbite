# -------- Stage 1: Build --------
FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder
WORKDIR /quickbite
COPY . .
RUN mvn clean package -DskipTests

# -------- Stage 2: Runtime --------
FROM eclipse-temurin:21-jre-jammy
WORKDIR /quickbite
COPY --from=builder /quickbite/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]