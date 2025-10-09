# Database Setup (PostgreSQL)

Simple ways to get PostgreSQL running locally.

## Option A: Linux (Ubuntu/Debian)
```
sudo apt update && sudo apt install -y postgresql postgresql-contrib
sudo systemctl enable --now postgresql

# Create database + user
sudo -u postgres psql <<'SQL'
CREATE DATABASE postgres;
CREATE USER postgres WITH PASSWORD 'secret';
GRANT ALL PRIVILEGES ON DATABASE postgres TO postgres;
\q
SQL
```

## Option B: Docker
```
docker run --name wesports-postgres -e POSTGRES_PASSWORD=secret -e POSTGRES_DB=postgres -p 5432:5432 -d postgres:16
```

## Defaults used by the app
- URL: jdbc:postgresql://localhost:5432/postgres
- User: postgres
- Password: secret

If you change these, update your environment variables accordingly.
