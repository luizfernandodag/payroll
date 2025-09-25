# Payroll System

A simple **Payroll Management System** built with **Spring Boot 3.3.x** and **Java 21**, designed to send email notifications without using a database.

## Project Structure

payroll/
├── src/main/java/com/atdev/payroll/ # Java source code
├── src/main/resources/ # Configuration files
├── pom.xml # Maven configuration
└── README.md # Project documentation


## Features

- REST API endpoints with Spring Web
- Validation of request payloads with Spring Validation
- Email notifications using Spring Boot Starter Mail
- Built with Lombok to reduce boilerplate code
- No database required
# Build the project

To build the project, run:

```bash
./mvn clean install
```


```bash
git clone https://github.com/YOUR-USERNAME/payroll.git
cd payroll

```

Run The App

To run the application, use:

```bash
./mvn spring-boot:run
```

#Configuration

spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=your-email@example.com
spring.mail.password=your-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

Testing
```bash
mvn test
```

