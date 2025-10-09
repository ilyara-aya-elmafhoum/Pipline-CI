# Contributing

A lightweight workflow.

## 1) Create an issue
- Describe goal briefly
- Labels: feature, bug, refactor, cleanup, docs

## 2) Create a branch
- feature/add-club-entity
- fix/jwt-cookie
- refactor/player-service
- cleanup/remove-logs

## 3) Commit small, clear changes
- feat: add club entity and repository
- fix: secure accessToken cookie
- refactor: simplify player profile service
- docs: update API.md for onboarding endpoints

## 4) Pull Request
- Link issue: "Closes #123" (or Fixes/Resolves)
- Keep description short and focused

### Quick checklist
- No TODO/debug noise in code
- Tests pass: `./mvnw test`
- API.md updated if endpoints changed

## 5) Style
- Hexagonal/DDD: domain logic in domain, orchestration in application
- Keep logging minimal and meaningful
