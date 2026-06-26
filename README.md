# Match Service

[![CI](https://github.com/Axel-DaMage/fullstack-ss-match-service/actions/workflows/ci.yml/badge.svg)](https://github.com/Axel-DaMage/fullstack-ss-match-service/actions/workflows/ci.yml)
[![Docker](https://github.com/Axel-DaMage/fullstack-ss-match-service/actions/workflows/docker.yml/badge.svg)](https://github.com/Axel-DaMage/fullstack-ss-match-service/actions/workflows/docker.yml)
![Java](https://img.shields.io/badge/java-17-orange)
![Spring Boot](https://img.shields.io/badge/spring%20boot-3.1.2-brightgreen)

Microservice for matching lost and found pets using similarity algorithms.

## Stack

- Java 17, Spring Boot 3.1.2
- Spring Data JPA, Liquibase, MySQL
- Eureka Discovery Client
- Maven, JaCoCo

## Quick start

```bash
mvn clean install
mvn spring-boot:run
```

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/matching` | List all |
| GET | `/api/matching/{id}` | Get by ID |
| POST | `/api/matching` | Create |
| PUT | `/api/matching/{id}` | Update status |
| DELETE | `/api/matching/{id}` | Delete |
| GET | `/api/matching/search/status/{status}` | Search by status |
| GET | `/api/matching/search/percentage/{percentage}` | Search by min percentage |
| GET | `/api/matching/totals/status` | Count by status |
| POST | `/api/matching/run-automatic` | Run auto matching |
| GET | `/health` | Health check |

## Matching algorithm

Score based on race, color, and size similarity. Final percentage is the average of all criteria scores.

## Tests

```bash
mvn test
mvn clean verify
```

## Database

MySQL `match_service` with tables: `matches`, `match_criteria`. Managed via Liquibase changelogs.
