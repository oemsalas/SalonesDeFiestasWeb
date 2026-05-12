# Salón de Fiestas API

API REST para gestión integral de un salón de fiestas. Java 17 + Spring Boot 3.2 + PostgreSQL.

## Stack

| Capa | Tecnología |
|------|-----------|
| Runtime | Java 17 |
| Framework | Spring Boot 3.2 |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | PostgreSQL 15+ |
| Migraciones | Flyway |
| Validación | Jakarta Validation |
| Docs | SpringDoc OpenAPI (Swagger UI) |
| Seguridad | Spring Security (stateless, listo para JWT) |
| Tests | JUnit 5 + Mockito |

## Estructura del proyecto

```
src/main/java/com/salon/fiestas/
├── controller/          # REST controllers (@RestController)
│   ├── ClienteController.java
│   ├── SalonController.java
│   └── ReservaController.java
├── service/             # Lógica de negocio
│   ├── ClienteService.java
│   ├── SalonService.java
│   ├── ReservaService.java
│   └── DisponibilidadService.java   ← lógica central de disponibilidad
├── repository/          # JPA repositories
├── model/
│   ├── entity/          # Entidades JPA
│   ├── dto/             # Request/Response records
│   └── enums/           # Enums del dominio
├── exception/           # GlobalExceptionHandler + excepciones custom
└── config/              # Security, OpenAPI
```

## Levantar el proyecto

### 1. Crear la base de datos en PostgreSQL

```sql
CREATE DATABASE salon_fiestas;
CREATE USER salon_user WITH PASSWORD 'salon_pass';
GRANT ALL PRIVILEGES ON DATABASE salon_fiestas TO salon_user;
```

### 2. Variables de entorno (opcional, tiene defaults)

```bash
export DB_USERNAME=salon_user
export DB_PASSWORD=salon_pass
```

### 3. Compilar y correr

```bash
./mvnw spring-boot:run
```

Flyway ejecuta automáticamente `V1__create_initial_schema.sql` al iniciar.

## API Endpoints

### Disponibilidad

```
POST /api/v1/salones/disponibilidad
{
  "fecha": "2025-12-15",
  "horaInicio": "18:00",
  "horaFin": "23:00",
  "capacidadMinima": 100
}
```

### Reservas

```
POST   /api/v1/reservas              → crear reserva (valida disponibilidad)
GET    /api/v1/reservas/{id}         → obtener reserva
GET    /api/v1/reservas/cliente/{id} → listar por cliente
POST   /api/v1/reservas/{id}/confirmar
POST   /api/v1/reservas/{id}/cancelar
```

### Clientes

```
GET    /api/v1/clientes
GET    /api/v1/clientes/{id}
POST   /api/v1/clientes
PUT    /api/v1/clientes/{id}
DELETE /api/v1/clientes/{id}
```

### Salones

```
GET    /api/v1/salones
POST   /api/v1/salones
PUT    /api/v1/salones/{id}
PATCH  /api/v1/salones/{id}/estado?estado=MANTENIMIENTO
POST   /api/v1/salones/bloqueos       → crear bloqueo manual
DELETE /api/v1/salones/bloqueos/{id}  → eliminar bloqueo
```

## Swagger UI

Una vez levantado: http://localhost:8080/api/swagger-ui.html

## Lógica de disponibilidad

El `DisponibilidadService` realiza 4 validaciones en orden:

1. **Horario operativo** — verifica `HORARIO_SALON` para el día de la semana
2. **Bloqueos manuales** — verifica `BLOQUEO_SALON` por rango de fechas y horas
3. **Solapamiento de reservas** — query: `horaInicio < fin_nueva AND horaFin > inicio_nueva`
4. **Reglas de configuración** — duración mínima/máxima, días de anticipación

## Ejecutar tests

```bash
./mvnw test
```



## correr app con docker 
# Construir y levantar ambos servicios
docker-compose up --build

# En segundo plano
docker-compose up --build -d

# Ver logs
docker-compose logs -f app

# Detener todo
docker-compose down

# Detener y eliminar volúmenes (borra la BD)
docker-compose down -v
