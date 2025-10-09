# WeSports Backend 🚀

Spring Boot API for the WeSports platform.

## Quick Start

1) Set up PostgreSQL — Guide: [docs/SETUP_DATABASE.md](docs/SETUP_DATABASE.md)

2) Configure environment — Copy [.env.example](.env.example) to `.env` — Guide: [docs/ENVIRONMENT.md](docs/ENVIRONMENT.md)

3) Run — `./mvnw spring-boot:run` — More: [docs/RUNNING.md](docs/RUNNING.md)

4) Verify
- API: http://localhost:8080
- Health: http://localhost:8080/actuator/health
- H2 (dev only): http://localhost:8080/h2-console
- Swagger UI: http://localhost:8080/swagger-ui/index.html

## Docs 📚
- Docs index: [docs/README.md](docs/README.md)
- Database setup: [docs/SETUP_DATABASE.md](docs/SETUP_DATABASE.md)
- Environment variables: [docs/ENVIRONMENT.md](docs/ENVIRONMENT.md)
- Running the app: [docs/RUNNING.md](docs/RUNNING.md)
- API reference: [API.md](API.md)
- Database design: [docs/DATABASE_DESIGN.md](docs/DATABASE_DESIGN.md)
- Contributing: [docs/CONTRIBUTING.md](docs/CONTRIBUTING.md)
- Swagger UI: http://localhost:8080/swagger-ui/index.html

## Architecture 🧱
- Hexagonal/DDD style
- Domain: business logic
- Application: orchestration/use cases
- Infrastructure: web, persistence, email, security

## Database Design 🗺️
- Single source of truth lives in [docs/DATABASE_DESIGN.md](docs/DATABASE_DESIGN.md)
- ER diagram:

![ER Diagram](DATABASE_DESIGN/ER.svg)

- Source files: [DATABASE_DESIGN/ER.dbml](DATABASE_DESIGN/ER.dbml) · [DATABASE_DESIGN/ER.sql](DATABASE_DESIGN/ER.sql)
- Interactive diagram: https://dbdiagram.io/d/68df591bd2b621e42213833c
- Visualize with: [dbdiagram.io](https://dbdiagram.io) · [ChartDB](https://chartdb.io)

- Conventions: Tables and columns in PascalCase
- Schema changes? Contact me first to keep it in sync: [Email](mailto:houssameddine.haddouche@ilyara.com)

## Contributing 🤝
Keep it simple: issue → branch → PR. See [docs/CONTRIBUTING.md](docs/CONTRIBUTING.md).

If something is missing or unclear, feel free to contact me [Email](mailto:houssameddine.haddouche@ilyara.com). 🙌
