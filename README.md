# 🚀 Transportation Route Service

## Overview

The **Transportation Route Service** is a Java-based application designed to manage and optimize transportation routes between various locations. It provides functionalities to search for valid routes based on user-defined criteria, leveraging caching for performance and efficiency.

## Features

- **Route Search**: Find valid transportation routes between specified origin and destination locations.
- **Caching**: Utilizes Redis for caching transportation data to improve response times and reduce database load.
- **Flexible Route Patterns**: Supports various transportation types and enforces business rules for valid route patterns.

## Technologies Used

- **Java 11**
- **Spring Boot**
- **Redis**
- **Postgres DB**
- **RedisInsight UI**
- **React.js**
- **JUnit** (for testing)

## Getting Started

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/brkyc3/route-planner.git
   cd transportation-route-service
   ```

2. **Docker Compose**:
   Ensure that you have a Redis server running. You can use Docker to quickly set up a Redis instance:
   ```bash
   docker compose up --build
   ```

### Configuration

You can configure the application by modifying the `application.yml` file located in `src/main/resources`. Here you can set up database connections, Redis configurations, and other application properties.

### API Documentation

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Frontend

- **React Frontend**: [http://localhost:3000/](http://localhost:3000/)

