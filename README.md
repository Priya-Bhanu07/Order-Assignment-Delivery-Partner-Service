Order Assignment & Delivery Partner Service

Tarrina Health – Backend Technical Assessment
 
Overview

This project implements a Spring Boot microservice for managing:

Order creation and tracking

Delivery partner management

Intelligent order assignment

Partner reassignment handling

Service zone validation using PostGIS

Audit logging for assignments and availability changes

The system simulates a real-world healthcare fulfillment workflow where medical supplies are assigned to delivery partners based on availability, geographic coverage, and capacity.

Business Logic Implemented
Order Assignment Algorithm
When assigning an order, the system evaluates:
Availability
Partner must be available
Skips busy, on_leave, offline
Service Zone Coverage
Uses PostGIS spatial query (ST_Contains)
Ensures delivery location falls within partner's service zone polygon

Capacity Check

current_active_orders < max_orders_per_day

Distance Calculation

Google Maps Distance Matrix API

Fallback to straight-line distance if API fails

Scoring Algorithm

score = (1 / (distance_km + 1)) * (1 - (current_orders / max_orders))

Highest score wins.

Reassignment Handling

If a partner becomes unavailable:

All active orders are identified

Reassignment attempted using standard algorithm

Audit trail created in partner_assignments

If no partner available → order remains unassigned

Edge Cases Covered

No available partner

Partner at full capacity

Zone coverage gap

Order timeout strategy defined

 Tech Stack
Layer	Technology
Language	Java 17
Framework	Spring Boot 3.5.10
Database	PostgreSQL 15 + PostGIS
ORM	Spring Data JPA + Hibernate Spatial
Migration	Flyway
API Docs	SpringDoc OpenAPI (Swagger)
Testing	JUnit 5, Mockito, Spring Boot Test
Build Tool	Maven
📂 Project Structure (Microservice using spring initializer)
src/main/java/com/tarrina/order-assignment-service/


├── controller/
├── service/
├── repository/
├── entity/
├── dto/
├── external/ (Google Maps client)
└── OrderAssignmentServiceApplication.java


src/main/resources/
├── application.properties
└── db/migration/

Architecture follows strict separation:

Controller → Service → Repository

DTOs used for API exposure

Entities not exposed directly

Transactional consistency ensured

🗄 Database

Existing schemas integrated:

users

contacts

locations

locationables

Newly implemented tables:

orders

delivery_partners

service_zones

partner_assignments

partner_availability_logs

Spatial indexing via PostGIS (GIST indexes).

Configuration
spring.application.name=order-assignment-service

spring.datasource.url=jdbc:postgresql://localhost:5432/delivery_system
spring.datasource.username=postgres
spring.datasource.password=root

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# ? IMPORTANT FOR POSTGIS
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true

server.port=8080

# ? GOOGLE MAPS CONFIG
google.maps.api-key=${GOOGLE_MAPS_API_KEY}
GOOGLE_MAPS_API_KEY=dummy-key-for-now

google.maps.distance-matrix-url=https://maps.googleapis.com/maps/api/distancematrix/json
google.maps.geocoding-url=https://maps.googleapis.com/maps/api/geocode/json

How to Run
1. Prerequisites

Java 17+

Maven 3.8+

PostgreSQL 15 with PostGIS extension enabled

Enable PostGIS:

CREATE EXTENSION postgis; 
2.Clone Repository
git clone <https://github.com/Priya-Bhanu07/Order-Assignment-Delivery-Partner-Service.git>
cd order-assignment-service
3.Configure Database

Update application.properties with correct credentials.

4. Run Application
mvn clean install
mvn spring-boot:run
 API Documentation (Swagger)

After starting the application:

http://localhost:8080/swagger-ui/index.html

All APIs are auto-documented.
Postman Collection

A ready-to-use Postman collection is included for testing all APIs.
Import Steps
Open Postman
Click Import

Select the file:
API Modules Implemented

User
post  api/v1/users
contact
Post api/v1/contacts

Orders

POST /api/v1/orders

GET /api/v1/orders

GET /api/v1/orders/{id}

PATCH /api/v1/orders/{id}/status

Delivery Partners

POST /api/v1/partners

GET /api/v1/partners

PATCH /api/v1/partners/{id}/availability

GET /api/v1/partners/{id}/orders

Assignments

POST /api/v1/assignments/assign

POST /api/v1/assignments/reassign/{orderId}

GET /api/v1/assignments/unassigned


Service Zones

Post api/v1/service-zones

Testing

Unit Tests

Service layer fully tested-done

Integration Tests-done

Run tests:

Docker Deployment

This application is fully containerized and can be started using Docker Compose:

docker compose up --build

mvn test
🛠 Additional Features

Flyway migration scripts

Spatial queries using Hibernate Spatial

Availability audit logging

Clean RESTful design

Validation using @Valid

Exception handling with global handler

📊 Performance Considerations

GIST indexes on spatial columns

Indexed foreign keys

Batch Google API usage

Database-level constraints enforced

Transaction isolation for assignment safety

Assumptions

Orders assigned individually

Reassignment immediate on availability change

Straight-line fallback used if Maps API fails
 
Future Improvements

Redis caching for distance matrix

Background job for unassigned order retry

Event-driven architecture (Kafka)

Prometheus metrics & monitoring


Author
Bhanu Priya
Backend Developer
