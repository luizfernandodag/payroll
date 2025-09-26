# Use a Maven + JDK image to build the application
FROM maven:3.9.7-eclipse-temurin-21-jammy AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# ---------------------------------------------
# Second stage: run the JAR
# ---------------------------------------------
FROM maven:3.9.7-eclipse-temurin-21-jammy

WORKDIR /app

COPY --from=build /app/target/payroll-0.0.1-SNAPSHOT.jar payroll.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/payroll.jar"]
