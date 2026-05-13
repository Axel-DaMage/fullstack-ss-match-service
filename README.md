# Match Service

Microservicio de matching para el proyecto **Sanos y Salvos**. Encargado de encontrar coincidencias potenciales entre mascotas perdidas y encontradas basándose en criterios de similitud.

## Objetivo

El Match Service proporciona una API REST para crear y gestionar coincidencias entre mascotas reportadas como perdidas y encontradas. Utiliza algoritmos de comparación por raza, color y tamaño para calcular un porcentaje de coincidencia y facilitar la reunificación de mascotas con sus dueños.

## Arquitectura

### Patrón Matching
El servicio implementa lógica de comparación para determinar posibles matches entre mascotas:

```
Pet Service → Pet Service Client → Match Service
Geo Service → Location Service Client → Match Service → Matching Service
```

### Componentes

- [MatchController](src/main/java/com/sanosysalvos/matchservice/controller/MatchController.java): Endpoints REST principales
- [MatchingService](src/main/java/com/sanosysalvos/matchservice/service/MatchingService.java): Lógica de matching y algoritmos de comparación
- [PetServiceConsumer](src/main/java/com/sanosysalvos/matchservice/service/PetServiceConsumer.java): Consumidor de servicios de mascotas
- [MatchRepository](src/main/java/com/sanosysalvos/matchservice/repository/MatchRepository.java): Repositorio JPA para coincidencias
- [MatchCriteriaRepository](src/main/java/com/sanosysalvos/matchservice/repository/MatchCriteriaRepository.java): Repositorio para criterios de matching
- [PetServiceClient](src/main/java/com/sanosysalvos/matchservice/client/PetServiceClient.java): Cliente HTTP para Pet Service
- [LocationServiceClient](src/main/java/com/sanosysalvos/matchservice/client/LocationServiceClient.java): Cliente HTTP para Geo Service
- [Match](src/main/java/com/sanosysalvos/matchservice/model/Match.java): Modelo de entidad coincidencia
- [MatchCriteria](src/main/java/com/sanosysalvos/matchservice/model/MatchCriteria.java): Modelo de criterios de comparación
- [PetDto](src/main/java/com/sanosysalvos/matchservice/model/PetDto.java): DTO de mascota
- [LocationDto](src/main/java/com/sanosysalvos/matchservice/model/LocationDto.java): DTO de ubicación

## Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/matching` | Listar todas las coincidencias |
| GET | `/api/matching/{id}` | Obtener coincidencia por ID |
| POST | `/api/matching` | Crear nueva coincidencia |
| PUT | `/api/matching/{id}` | Actualizar estado de coincidencia |
| DELETE | `/api/matching/{id}` | Eliminar coincidencia |
| GET | `/api/matching/search/status/{status}` | Buscar coincidencias por estado |
| GET | `/api/matching/search/percentage/{percentage}` | Buscar por porcentaje mínimo |
| GET | `/api/matching/totals/status` | Contar coincidencias por estado |
| POST | `/api/matching/run-automatic` | Ejecutar matching automático |
| GET | `/health` | Verificar estado del servicio |

## Algoritmo de Matching

El servicio calcula el porcentaje de coincidencia basándose en tres criterios:

1. **Raza**: Coincidencia exacta = 100 puntos, diferente = 30 puntos
2. **Color**: Coincidencia exacta = 100 puntos, diferente = 40 puntos
3. **Tamaño**: Coincidencia exacta = 100 puntos, diferente = 50 puntos

El porcentaje final se calcula como el promedio de los scores obtenidos.

## Tecnologías

- Java 17
- Spring Boot 3
- Spring Web (REST)
- Spring Data JPA
- Liquibase
- MySQL
- Maven

## Configuración

```properties
# Puerto del servicio
server.port=3003

# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/match_service
spring.datasource.username=root
spring.datasource.password=password

# URLs de servicios externos
pet.service.url=http://pet-service:3001
geo.service.url=http://geo-service:3002

# Liquibase
spring.liquibase.enabled=true
```

## Instalación

```bash
mvn clean install
mvn spring-boot:run
```

## Pruebas

```bash
mvn test
```

## Notas

- El servicio consume datos de Pet Service y Geo Service para realizar el matching.
- Implementa matching automático que compara todas las mascotas perdidas con las encontradas.
- Solo crea coincidencias con porcentaje mayor o igual al 60% en matching automático.
- Permite confirmar o rechazar coincidencias manualmente.
- Proporciona estadísticas por estado: PENDING, CONFIRMED, REJECTED.
- Utiliza auditoría automática mediante @PrePersist y @PreUpdate.

---

## Despliegue

Este servicio se despliega automáticamente como parte del repositorio **pet-service** a la instancia **Backend (t3.medium)**.

Ver [Setup Guide](../fullstack-ss-pet-service/README.md#despliegue-en-aws-ec2) para detalles completos de la infraestructura.