# Fullstack Microservicios

Sistema académico construido con una arquitectura de **microservicios en Java (Spring Boot)**, orientado a la gestión de integrantes, grupos, asignaturas, secciones, profesores, roles, trabajos, entregas, notas y comentarios dentro de un entorno educativo.

El proyecto incluye un **servidor de descubrimiento (Eureka)** y un **API Gateway**, permitiendo que cada microservicio se registre y sea enrutado de forma centralizada.

## Arquitectura

El sistema está compuesto por los siguientes módulos:

| Servicio | Descripción |
|---|---|
| `eureka-server` | Servidor de descubrimiento de servicios (Service Discovery) |
| `gateway` | Puerta de enlace (API Gateway) que enruta las peticiones a cada microservicio |
| `integrantes_service` | Gestión de integrantes/estudiantes |
| `grupos_service` | Gestión de grupos de trabajo |
| `asignaturas_service` | Gestión de asignaturas |
| `secciones_service` | Gestión de secciones |
| `profesores_service` | Gestión de profesores |
| `roles_service` | Gestión de roles y permisos |
| `trabajos_service` | Gestión de trabajos/tareas asignadas |
| `entregas_service` | Gestión de entregas de trabajos |
| `notas_service` | Gestión de notas/calificaciones |
| `comentarios_service` | Gestión de comentarios |

Cada microservicio se registra en Eureka (`localhost:8761`) y es accesible a través del Gateway.

## Tecnologías

- **Java**
- **Spring Boot**
- **Spring Cloud Netflix Eureka** (Service Discovery)
- **Spring Cloud Gateway** (API Gateway)
- **Maven**

## Requisitos previos

- JDK 17 o superior
- Maven 3.8+
- Un IDE compatible (IntelliJ IDEA, Eclipse, VS Code, etc.)

## Cómo ejecutar el proyecto

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/NombreDeDesarrollador/Fullstack-Microservicios.git
   cd Fullstack-Microservicios
   ```

2. **Levantar primero el servidor Eureka** (descubrimiento de servicios):
   ```bash
   cd eureka-server
   mvn spring-boot:run
   ```
   Verificar que esté activo en: [http://localhost:8761](http://localhost:8761)

3. **Levantar el Gateway**:
   ```bash
   cd gateway
   mvn spring-boot:run
   ```

4. **Levantar cada microservicio** (en terminales separadas), por ejemplo:
   ```bash
   cd integrantes_service
   mvn spring-boot:run
   ```
   Repetir para `grupos_service`, `asignaturas_service`, `secciones_service`, `profesores_service`, `roles_service`, `trabajos_service`, `entregas_service`, `notas_service` y `comentarios_service` según lo que se necesite probar.

5. Verificar en Eureka (`http://localhost:8761`) que todos los microservicios levantados aparezcan registrados correctamente.

## Pruebas

El proyecto cuenta con un [Plan de Pruebas](./TESTING_PLAN.md) documentado, que detalla los casos de prueba ejecutados (creación, listado, búsqueda por ID, actualización y eliminación de integrantes, relación integrante-grupo, y verificación de registro en Eureka).

Las pruebas se realizan mediante:
- **Postman**
- **Swagger / OpenAPI**
- Navegador web
- **Eureka Dashboard**

## Estructura del repositorio

```
Fullstack-Microservicios/
├── eureka-server/
├── gateway/
├── asignaturas_service/
├── comentarios_service/
├── entregas_service/
├── grupos_service/
├── integrantes_service/
├── notas_service/
├── profesores_service/
├── roles_service/
├── secciones_service/
├── trabajos_service/
└── TESTING_PLAN.md
```

## Autor

Proyecto desarrollado por [NombreDeDesarrollador](https://github.com/NombreDeDesarrollador).

## Licencia

Este proyecto no cuenta actualmente con una licencia definida. Si deseas reutilizarlo, contacta al autor.
