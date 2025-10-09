# Docs Index 📚

Quick index of the documentation in this folder. Keep the README at the root simple — detailed, technical steps live here.

- Setup PostgreSQL: [SETUP_DATABASE.md](SETUP_DATABASE.md)
  - Install PostgreSQL locally, create the database/user, and run migrations.
- Environment variables: [ENVIRONMENT.md](ENVIRONMENT.md)
  - Copy .env.example → .env and fill in secrets (DB, JWT, OAuth, Mail).
- Run the app: [RUNNING.md](RUNNING.md)
  - Start the Spring Boot app (dev/prod tips), useful commands.
- Database design (source of truth): [DATABASE_DESIGN.md](DATABASE_DESIGN.md)
  - ER diagram, DBML/SQL, conventions (Tables: PascalCase, Columns: camelCase).
- Contributing guide: [CONTRIBUTING.md](CONTRIBUTING.md)
  - Lightweight flow: issue → branch → PR, style and PR checklist.
- API reference (root): [../API.md](../API.md)
  - Endpoints, request/response examples.

If you need to change the database, contact me first so everyone stays in sync and our single source of truth is updated: [Email](mailto:houssameddine.haddouche@ilyara.com). 🙌
