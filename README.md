# Payroll System

A simple **Payroll Management System** built with **Spring Boot 3.3.x** and **Java 21**, designed to send email notifications without using a database.

## Project Structure

### payroll/
#### ├── src/main/java/com/atdev/payroll/ # Java source code
#### ├── src/main/resources/ # Configuration files
#### ├── pom.xml # Maven configuration
#### └── README.md # Project documentation


## Features

- REST API endpoints with Spring Web
- Validation of request payloads with Spring Validation
- Email notifications using Spring Boot Starter Mail
- Built with Lombok to reduce boilerplate code
- No database required


## Prerequisites

- Java 21 (for Maven build)  
- Maven 3.9+  
- Docker (for containerized build/run)  
- `.env` file with environment variables for sensitive data  

### Example `.env` file

```env
SPRING_APPLICATION_NAME=payroll
PAYROLL_USER=admin
PAYROLL_PASSWORD=secret
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-email-password
SPRING_MAIL_SMTP_AUTH=true
SPRING_MAIL_SMTP_STARTTLS=true
```
# Clone and Build the project

```bash
git clone https://github.com/YOUR-USERNAME/payroll.git
cd payroll

```

To build the project, run:

```bash
./mvn clean install
```



Run The App

To run the application, use:

```bash
./mvn spring-boot:run
```

# Configuration - application properties
```
spring.application.name=${SPRING_APPLICATION_NAME:payroll}

payroll.user=${PAYROLL_USER:admin}
payroll.password=${PAYROLL_PASSWORD:secret}

spring.mail.host=${SPRING_MAIL_HOST:smtp.gmail.com}
spring.mail.port=${SPRING_MAIL_PORT:587}
spring.mail.username=${SPRING_MAIL_USERNAME}
spring.mail.password=${SPRING_MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=${SPRING_MAIL_SMTP_AUTH:true}
spring.mail.properties.mail.smtp.starttls.enable=${SPRING_MAIL_SMTP_STARTTLS:true}

```
Testing
```bash
mvn test
```
# Build and Run with Docker
```bash
docker build -t payroll-service:latest .
docker run -p 8080:8080 --env-file .env payroll-service:latest
```

# TEST API


```bash
curl.exe -X POST "http://localhost:8080/payroll/process?country=<country>&company=<company>" -u <PAYROLL_USER>:<PAYROLL_PASSWORD> -F "file=@<path to payroll.csv>"```
```


# TEST API EXAMPLE


```bash
curl.exe -X POST "http://localhost:8080/payroll/process?country=do&company=atdev" -u admin:secret -F "file=@<path to payroll.csv>"```
```
select you county, company, payroll_user, payroll_password on that api call

# Testing

```bash
mvn test
```
# References
Spring Boot Documentation

Docker Documentation

Maven Documentation


