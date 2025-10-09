# Run the App

## 1) Prereqs
- PostgreSQL running (see docs/SETUP_DATABASE.md)
- Environment variables set (see docs/ENVIRONMENT.md)
- Java 17+

## 2) Start
```
./mvnw spring-boot:run
```

## 3) Dev profile (H2)
```
./mvnw spring-boot:run -Dspring.profiles.active=h2
```

## 4) Verify
- API: http://localhost:8080
- Health: http://localhost:8080/actuator/health
- H2 (dev only): http://localhost:8080/h2-console
- Swagger UI: http://localhost:8080/swagger-ui/index.html

Optional: see `run.sh` for an example of exporting OAuth and mail vars before starting.
