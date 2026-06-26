# Match Service

[![Docker](https://github.com/Axel-DaMage/fullstack-ss-match-service/actions/workflows/docker.yml/badge.svg)](https://github.com/Axel-DaMage/fullstack-ss-match-service/actions/workflows/docker.yml)
![Java](https://img.shields.io/badge/java-17-orange)
![Spring Boot](https://img.shields.io/badge/spring%20boot-3.1.2-brightgreen)

Microservicio para el algoritmo de coincidencias entre mascotas perdidas y encontradas. Calcula porcentajes de similitud basados en raza, color y tamano.

## Stack

- Java 17, Spring Boot 3.1.2
- Spring Data JPA, Hibernate, Liquibase, MySQL
- Eureka Discovery Client
- Maven, JaCoCo
- Docker multi-stage
- Resilience4j Circuit Breaker (cliente HTTP)

## Patrones de Diseno

| Patron | Tipo | Donde |
|--------|------|-------|
| **Strategy** | GoF | `calculateMatch()` (pesos por atributo) vs `calculateSimpleMatch()` (peso plano) en `MatchingService` |
| **Singleton** | GoF | `AppConfig` — configuracion global con `minMatchPercentage` y `autoMatchingEnabled` |
| **Template Method** | GoF | Entidades JPA con `@PrePersist`/`@PreUpdate` para timestamps |
| **Circuit Breaker** | Cloud | Resilience4j en `PetServiceConsumer` — falla graceful ante caida de pet-service |
| **Proxy** | Spring AOP | `@Transactional` + `RestTemplate` clients |
| **DTO** | GoF | `PetDto`, `LocationDto` para comunicacion entre servicios |

## Algoritmo de Matching

El servicio implementa dos estrategias de calculo de similitud:

**calculateMatch (ponderado):**
- Raza: 40% del puntaje total
- Color: 30% del puntaje total
- Tamano: 30% del puntaje total

**calculateSimpleMatch (plano):**
- Cada atributo (raza, color, tamano) tiene igual peso (33.3%)

Ambos devuelven un porcentaje de 0 a 100. `runAutomaticMatching()` aplica el algoritmo simple contra todos los pares perdido/encontrado.

## Endpoints

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/api/matching` | Listar todas las coincidencias |
| GET | `/api/matching/{id}` | Obtener por ID |
| POST | `/api/matching` | Crear coincidencia |
| PUT | `/api/matching/{id}` | Actualizar estado |
| DELETE | `/api/matching/{id}` | Eliminar |
| GET | `/api/matching/search/status/{status}` | Buscar por estado |
| GET | `/api/matching/search/percentage/{percentage}` | Buscar por % minimo |
| GET | `/api/matching/totals/status` | Totales por estado |
| POST | `/api/matching/run-automatic` | Matching automatico |
| GET | `/health` | Health check |

## Base de Datos

MySQL `match_service` con tablas: `matches`, `match_criteria`. Migraciones Liquibase en XML.

**Entidades:**
- `Match` → `matches` (id, mascotaPerdidaId, mascotaEncontradaId, porcentajeCoincidencia, status, fechaCreacion) — `@OneToMany` → MatchCriteria
- `MatchCriteria` → `match_criteria` (id, nombreCriterio, puntaje) — `@ManyToOne` → Match

## Pruebas

```bash
mvn clean test
mvn clean verify
```

32 tests en 2 archivos: `MatchingServiceTest`, `MatchControllerTest`.

## Docker

```bash
docker build -t d4mag3/match-service .
docker run -p 3003:3003 d4mag3/match-service
```

Imagen disponible en: `d4mag3/match-service:latest`

## Variables de Entorno

| Variable | Default | Descripcion |
|----------|---------|-------------|
| `SERVER_PORT` | 3003 | Puerto del servicio |
| `DB_URL` | `jdbc:mysql://db-match:3306/match_service` | URL de base de datos |
| `DB_USER` | user | Usuario MySQL |
| `DB_PASSWORD` | password | Password MySQL |
| `PET_SERVICE_URL` | `http://pet-service:3001` | URL de pet-service |
| `GEO_SERVICE_URL` | `http://geo-service:3002` | URL de geo-service |
| `EUREKA_URL` | `http://eureka-server:8761/eureka/` | URL de Eureka |
