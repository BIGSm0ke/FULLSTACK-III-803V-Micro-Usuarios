# Micro-Usuarios - Microservicio de Usuarios

Microservicio Spring Boot para autenticación y gestión de usuarios. Implementa JWT, Spring Security y Circuit Breaker con Resilience4j.

## Tecnologías

- Spring Boot 3.3.0, Spring Security, Spring Data JPA, Spring Cloud
- PostgreSQL (AWS RDS), H2 (tests)
- JWT, Resilience4j, AOP
- JaCoCo, Mockito, JUnit 5, Springdoc OpenAPI

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/auth/register` | Registrar usuario |
| POST | `/auth/login` | Iniciar sesión |
| GET | `/auth/usuarios` | Listar usuarios |
| GET | `/auth/health` | Health check |
| GET | `/users/profile` | Obtener perfil |
| PUT | `/users/profile` | Actualizar perfil |
| GET | `/api/test/protegido` | Ruta JWT de prueba |

Swagger: `http://localhost:8085/swagger-ui/index.html`

## Ejecutar

```bash
.\mvnw.cmd spring-boot:run
```

## Pruebas

```bash
.\mvnw.cmd test        # ejecutar tests
.\mvnw.cmd verify      # tests + JaCoCo report
```

Cobertura: **89.0%**

## Capturas

<img width="1072" height="212" alt="image" src="https://github.com/user-attachments/assets/d8457662-278a-4f53-864a-45d9dec1a194" />

