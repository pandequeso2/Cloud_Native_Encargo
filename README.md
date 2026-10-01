# ☁️ Cloud Native Encargo

Sistema académico desarrollado bajo una arquitectura de microservicios, como parte de la asignatura Cloud Native.

El proyecto permite gestionar distintos aspectos de un entorno académico, incluyendo integrantes, grupos, asignaturas, secciones, profesores, roles, trabajos, entregas, notas y comentarios.

La solución está compuesta por un Frontend desarrollado con React + TypeScript y un Backend basado en microservicios con Spring Boot, utilizando Spring Cloud Netflix Eureka para el descubrimiento de servicios y Spring Cloud Gateway como punto de entrada a la API.

## 📋 Tabla de contenidos

- [Descripción](#-descripción)
- [Arquitectura](#-arquitectura)
- [Tecnologías utilizadas](#-tecnologías-utilizadas)
- [Estructura del proyecto](#-estructura-del-proyecto)
- [Microservicios](#-microservicios)
- [Requisitos](#-requisitos)
- [Configuración](#-configuración)
- [Ejecución con Docker](#-ejecución-con-docker)
- [Frontend](#-frontend)
- [Autenticación](#-autenticación)
- [RabbitMQ y Mensajería](#-rabbitmq-y-mensajería)
- [Endpoints y puertos](#-endpoints-y-puertos)
- [Pruebas](#-pruebas)
- [Terraform](#-terraform)
- [Despliegue (CI/CD)](#-despliegue-cicd)
- [Inicio rápido](#-inicio-rápido)
- [Autores](#-autores)

## 📖 Descripción

Cloud Native Encargo es una aplicación académica diseñada utilizando principios de arquitectura Cloud Native y microservicios.

El sistema separa las distintas funcionalidades del dominio académico en servicios independientes. Cada microservicio puede ejecutarse de forma independiente y se registra en un servidor de descubrimiento Eureka.

Las solicitudes externas son gestionadas mediante un API Gateway, que se encarga de enrutar las peticiones hacia el microservicio correspondiente.

El proyecto también incorpora:

- 🐳 Contenedores Docker.
- 🗄️ Base de datos MySQL.
- 🔎 Service Discovery mediante Eureka.
- 🚪 API Gateway.
- 🐰 Mensajería asíncrona con RabbitMQ (Direct Exchange, Dead Letter Queues y gestión dinámica de recursos).
- 🔐 Autenticación mediante Microsoft Entra ID.
- ⚛️ Frontend SPA con React y TypeScript.
- 🏗️ Infraestructura preparada mediante Terraform.
- 🧪 Pruebas mediante Postman, Swagger/OpenAPI y Eureka Dashboard.

## 🏗️ Arquitectura

La arquitectura general del sistema puede representarse de la siguiente manera:

```
                         ┌─────────────────────┐
                         │     Usuario         │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │      Frontend       │
                         │ React + TypeScript  │
                         │       Vite          │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │    API Gateway      │
                         │   Spring Gateway    │
                         │      :8095          │
                         └──────────┬──────────┘
                                    │
                ┌───────────────────┼───────────────────┐
                │                   │                   │
                ▼                   ▼                   ▼
        ┌──────────────┐    ┌──────────────┐    ┌──────────────┐
        │ Microservicio│    │ Microservicio│    │ Microservicio│
        │ de Grupos    │    │ de Trabajos  │    │ de Entregas  │
        └──────┬───────┘    └──────┬───────┘    └──────┬───────┘
               │                   │                   │
               └───────────────────┼───────────────────┘
                                   │
                                   ▼
                         ┌─────────────────────┐
                         │        MySQL        │
                         │       :3306         │
                         └─────────────────────┘

                         ┌─────────────────────┐
                         │       Eureka        │
                         │ Service Discovery   │
                         │       :8761         │
                         └─────────────────────┘

                         ┌─────────────────────┐
                         │      RabbitMQ       │
                         │   :5672 / :15672    │
                         └─────────────────────┘
```

### 🔀 Flujo de una solicitud

1. El usuario interactúa con el Frontend.
2. El Frontend obtiene la autenticación mediante Microsoft Entra ID.
3. Las peticiones son enviadas al API Gateway.
4. El Gateway identifica el microservicio correspondiente.
5. Eureka permite localizar los servicios registrados.
6. El microservicio procesa la solicitud.
7. Los servicios utilizan MySQL para la persistencia de datos.
8. La respuesta retorna al Frontend a través del Gateway.

### 🐰 Flujo de un mensaje asíncrono

1. El Frontend invoca un endpoint de `rabbitmq_service` a través del API Gateway.
2. El microservicio publica el mensaje en un exchange de RabbitMQ.
3. RabbitMQ enruta el mensaje a la(s) cola(s) suscrita(s) según la routing key.
4. El consumidor del microservicio procesa el mensaje y confirma con `ACK`.
5. Si el procesamiento falla, se responde con `NACK` y el mensaje se redirige a la **Dead Letter Queue (DLQ)**.

## 🛠️ Tecnologías utilizadas

### Backend

- Java 17+
- Spring Boot
- Spring Cloud
- Spring Cloud Netflix Eureka
- Spring Cloud Gateway
- Spring AMQP (RabbitMQ)
- springdoc OpenAPI (Swagger)
- Maven
- MySQL

### Frontend

- React
- TypeScript
- Vite
- MSAL React
- Microsoft Entra ID
- Nginx

### Infraestructura

- Docker
- Docker Compose
- RabbitMQ
- Terraform
- GitHub Actions
## 📁 Estructura del proyecto

```
Cloud_Native_Encargo/
│
├── .github/
│   └── workflows/
│       └── cd.yml
│
├── .vscode/
│
├── Backend/
│   ├── eureka-server/
│   ├── gateway/
│   ├── asignaturas_service/
│   ├── comentarios_service/
│   ├── entregas_service/
│   ├── grupos_service/
│   ├── integrantes_service/
│   ├── notas_service/
│   ├── profesores_service/
│   ├── rabbitmq_service/
│   ├── roles_service/
│   ├── secciones_service/
│   ├── trabajos_service/
│   ├── README.md
│   └── TESTING_PLAN.md
│
├── Frontend/
│   ├── public/
│   ├── src/
│   │   ├── api/
│   │   │   └── rabbitmqService.ts
│   │   ├── auth/
│   │   ├── components/
│   │   ├── pages/
│   │   │   └── RabbitMQDashboard.tsx
│   │   ├── routes/
│   │   ├── styles/
│   │   │   └── rabbitmq.css
│   │   └── types/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
│
├── Terraform/
│
├── docker-compose.yml
├── docker-compose.prod.yml
├── .env.example
├── .gitignore
└── README.md
```

## 🔧 Microservicios

El Backend está compuesto por los siguientes servicios:

| Servicio | Descripción |
|---|---|
| `eureka-server` | Registro y descubrimiento de servicios |
| `gateway` | Punto de entrada y enrutamiento de la API |
| `asignaturas_service` | Gestión de asignaturas |
| `comentarios_service` | Gestión de comentarios |
| `entregas_service` | Gestión de entregas |
| `grupos_service` | Gestión de grupos |
| `integrantes_service` | Gestión de integrantes/estudiantes |
| `notas_service` | Gestión de notas |
| `profesores_service` | Gestión de profesores |
| `rabbitmq_service` | Mensajería asíncrona: exchanges, colas, DLQ y consumidores |
| `roles_service` | Gestión de roles y permisos |
| `secciones_service` | Gestión de secciones |
| `trabajos_service` | Gestión de trabajos/tareas |

Cada microservicio se encuentra separado y puede registrarse en Eureka para ser localizado por el resto de la arquitectura.

Además, el sistema incluye el broker **RabbitMQ** (`rabbitmq` en Docker Compose), que no es un microservicio Spring Boot sino un servicio de infraestructura de mensajería.

## 📦 Requisitos

Para ejecutar el proyecto localmente se recomienda contar con:

- Java JDK 17 o superior
- Maven 3.8+
- Node.js 20+
- npm
- Docker
- Docker Compose
- Una aplicación registrada en Microsoft Entra ID para la autenticación del Frontend y Backend.

RabbitMQ **no requiere instalación local**, ya que se levanta como contenedor mediante `docker compose`.
## ⚙️ Configuración

### Variables de entorno

El proyecto utiliza variables de entorno para configurar principalmente la autenticación mediante Microsoft Entra ID.

#### Backend (archivo raíz `.env`)

Copiar el archivo de ejemplo:

```bash
cp .env.example .env
```

Las variables son:

```bash
ENTRA_TENANT_ID=<tenant-id>
ENTRA_AUDIENCE=api://<backend-client-id>
```

#### Frontend (archivo `Frontend/.env`)

A partir del archivo de ejemplo:

cd Frontend
cp .env.example .env


Las variables principales son:

```bash
VITE_ENTRA_CLIENT_ID=<client-id-del-SPA>
VITE_ENTRA_TENANT_ID=<tenant-id>
VITE_ENTRA_API_CLIENT_ID=<client-id-del-backend>
VITE_API_BASE_URL=http://localhost:8095
VITE_REDIRECT_URI=http://localhost:5173
```

> ⚠️ Importante: no se deben subir credenciales, secretos, tokens ni archivos `.env` con información sensible al repositorio.

> ℹ️ El `VITE_REDIRECT_URI` debe coincidir con el Redirect URI configurado en el registro de la aplicación SPA de Microsoft Entra ID.

### Variables de entorno de RabbitMQ

El microservicio `rabbitmq_service` y el broker usan las siguientes variables, con valores por defecto definidos en el código y en `docker-compose.yml`:

| Variable | Default | Descripción |
|---|---|---|
| `RABBITMQ_HOST` | `localhost` (`rabbitmq` en Docker) | Host del broker AMQP |
| `RABBITMQ_PORT` | `5672` | Puerto del protocolo AMQP |
| `RABBITMQ_USER` | `admin` | Usuario del broker |
| `RABBITMQ_PASSWORD` | `admin123` | Contraseña del broker |
| `EUREKA_HOST` | `localhost` | Host del servidor Eureka |
| `RABBITMQ_SERVICE_HOST` | `localhost` | Host del microservicio, usado por el Gateway para enrutar |

En `docker-compose.yml` el servicio `rabbitmq-service` ya recibe estos valores automáticamente:

```yaml
environment:
  EUREKA_HOST: eureka-server
  RABBITMQ_HOST: rabbitmq
  RABBITMQ_USER: admin
  RABBITMQ_PASSWORD: admin123
```

## 🐳 Ejecución con Docker

La forma recomendada para ejecutar el sistema completo es mediante Docker Compose.

### 1. Clonar el repositorio

```bash
git clone https://github.com/pandequeso2/Cloud_Native_Encargo.git
cd Cloud_Native_Encargo
```

Cambiar a la rama `Develop`:

```bash
git checkout Develop
```

### 2. Configurar las variables de entorno

Configurar las variables necesarias para Microsoft Entra ID antes de iniciar los contenedores.

### 3. Construir los artefactos de los microservicios

Los `Dockerfile` del Backend utilizan el `.jar` ya compilado, por lo que se deben empaquetar los servicios previamente. Desde la carpeta `Backend/`:

```powershell
"eureka-server", "gateway", "notas_service", "asignaturas_service", "profesores_service", "grupos_service", "integrantes_service", "roles_service", "secciones_service", "trabajos_service", "entregas_service", "comentarios_service", "rabbitmq_service" | ForEach-Object { Push-Location $_; .\mvnw.cmd clean package -DskipTests; Pop-Location }
```

### 4. Construir y ejecutar

```bash
docker compose up -d --build
```

Para revisar los contenedores:

```bash
docker compose ps
```

Para visualizar los logs:

```bash
docker compose logs -f
```

Para ver solo los logs de RabbitMQ:

```bash
docker compose logs -f rabbitmq rabbitmq-service
```

### 5. Detener el proyecto

```bash
docker compose down
```

Para detener los contenedores y eliminar también los volúmenes:

```bash
docker compose down -v
```

> ⚠️ `docker compose down -v` elimina los volúmenes asociados, por lo que puede provocar pérdida de los datos almacenados en MySQL **y en RabbitMQ** (`rabbitmq_data`).

## 🖥️ Frontend

El Frontend está desarrollado como una SPA utilizando:

- React
- TypeScript
- Vite
- MSAL
- Microsoft Entra ID

En desarrollo puede ejecutarse directamente mediante:

```bash
cd Frontend
npm install
npm run dev
```

La aplicación estará disponible normalmente en:

```
http://localhost:5173
```

Para generar una versión de producción:

```bash
npm run build
```

Y para visualizar el build:

```bash
npm run preview
```

### Rutas disponibles

| Ruta | Descripción |
|---|---|
| `/trabajos` | Gestión de trabajos |
| `/entregas` | Gestión de entregas |
| `/rabbitmq` | Dashboard de RabbitMQ, DLQ y RabbitAdmin |

## 🔐 Autenticación

La autenticación del Frontend utiliza Microsoft Entra ID mediante `@azure/msal-react`.

El sistema contempla:

- Inicio de sesión.
- Cierre de sesión.
- Protección de rutas.
- Obtención de tokens.
- Envío del token al Backend.
- Lectura de roles y claims.
- Renovación silenciosa del token cuando es posible.

La comunicación con el Backend se realiza mediante el API Gateway.

```
Usuario
   │
   ▼
Microsoft Entra ID
   │
   │ Token
   ▼
Frontend
   │
   │ Bearer Token
   ▼
API Gateway
   │
   ▼
Microservicios
```

> ℹ️ Las rutas bajo `/api/v1/rabbitmq/**` están declaradas como públicas en el `SecurityConfig` del Gateway, por lo que el dashboard de RabbitMQ puede consultarse sin un token válido.

## 🐰 RabbitMQ y Mensajería

El microservicio `rabbitmq_service` implementa comunicación asíncrona usando **Spring AMQP** sobre un broker **RabbitMQ**. Se registra en Eureka con el nombre `rabbitmq-service` y es enrutado por el Gateway bajo el prefijo `/api/v1/rabbitmq/**`.

### Topología de exchanges, colas y bindings

El microservicio declara en `RabbitMQConfig` dos escenarios de demostración:

#### 1. Sistema de logs con Direct Exchange

| Recurso | Nombre | Tipo | Notas |
|---|---|---|---|
| Exchange | `logs_direct_exchange` | Direct | Enruta según la routing key |
| Cola | `all_logs_queue` | Queue (durable) | Recibe `INFO`, `WARNING` y `ERROR` |
| Cola | `errors_only_queue` | Queue (durable) | Recibe solo `ERROR` |

Bindings definidos:

| Binding | Routing Key | Destino |
|---|---|---|
| `logs_direct_exchange` → `all_logs_queue` | `INFO` | Monitor general |
| `logs_direct_exchange` → `all_logs_queue` | `WARNING` | Monitor general |
| `logs_direct_exchange` → `all_logs_queue` | `ERROR` | Monitor general |
| `logs_direct_exchange` → `errors_only_queue` | `ERROR` | Alerta crítica |

#### 2. Sistema resiliente con DLX, DLQ y TTL

| Recurso | Nombre | Tipo | Notas |
|---|---|---|---|
| Exchange | `orders.exchange` | Direct | Exchange principal de órdenes |
| Cola | `orders.queue` | Queue (durable) | TTL 30 s, `x-max-length` 1000, envía a DLX al expirar o ser rechazado |
| Exchange | `orders.dlx` | Fanout | Dead Letter Exchange |
| Cola | `orders.dlq` | Queue (durable) | Dead Letter Queue con TTL de 24 h |

Bindings definidos:

| Binding | Routing Key | Destino |
|---|---|---|
| `orders.exchange` → `orders.queue` | `order.created` | Procesamiento de órdenes |
| `orders.dlx` → `orders.dlq` | *(fanout, sin routing key)* | Cuarentena de mensajes fallidos |

### Consumidores

| Consumidor | Cola | Comportamiento |
|---|---|---|
| `OrderConsumer.processOrder` | `orders.queue` | Usa `acknowledge-mode: manual`. Simula un fallo aleatorio (50 %) y responde con `basicNack(requeue=false)` para enviar el mensaje a la DLQ. |
| `OrderConsumer.processDLQ` | `orders.dlq` | Registra en log los mensajes en cuarentena. |
| `LogConsumer.receiveAllLogs` | `all_logs_queue` | Registra todos los logs del sistema. |
| `LogConsumer.receiveErrorLogs` | `errors_only_queue` | Registra una alerta crítica por cada `ERROR`. |

Configuración de los listeners (`RabbitListenerConfig`):

- `orderListenerFactory`: 3 consumidores concurrentes, hasta 5 máximo.
- `paymentListenerFactory`: 1 consumidor.
- Acknowledge mode: `manual` con `prefetch: 1` y reintentos exponenciales (3 intentos, de 1 s a 10 s).

### Endpoints del microservicio

Todos se exponen a través del Gateway en `/api/v1/rabbitmq/**`.

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/v1/rabbitmq/orders/status` | Estado del servicio y mensajes en `orders.queue` y `orders.dlq` |
| `POST` | `/api/v1/rabbitmq/orders/send` | Publica una orden en `orders.exchange` |
| `POST` | `/api/v1/rabbitmq/logs` | Publica un log con nivel `INFO`, `WARNING` o `ERROR` |
| `GET` | `/api/v1/rabbitmq/listeners` | Lista los listeners registrados |
| `POST` | `/api/v1/rabbitmq/listeners/{id}/pause` | Detiene un listener |
| `POST` | `/api/v1/rabbitmq/listeners/{id}/resume` | Reanuda un listener |
| `POST` | `/api/v1/rabbitmq/admin/queues?queueName=` | Crea una cola dinámica |
| `POST` | `/api/v1/rabbitmq/admin/exchanges?exchangeName=` | Crea un exchange dinámico |
| `POST` | `/api/v1/rabbitmq/admin/bindings?queueName=&exchangeName=&routingKey=` | Crea un binding dinámico |
| `GET` | `/api/v1/rabbitmq/admin/queues/{queueName}` | Información de una cola |
| `DELETE` | `/api/v1/rabbitmq/admin/queues/{queueName}` | Elimina una cola |
| `POST` | `/api/v1/rabbitmq/admin/queues/{queueName}/purge` | Purga los mensajes de una cola |

Ejemplo de envío de un log:

```bash
curl -X POST http://localhost:8095/api/v1/rabbitmq/logs \
  -H "Content-Type: application/json" \
  -d '{"level":"ERROR","message":"Error de prueba"}'
```

Ejemplo de envío de una orden:

```bash
curl -X POST http://localhost:8095/api/v1/rabbitmq/orders/send \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-001","customerName":"Estudiante Duoc"}'
```

> 💡 El `OrderConsumer` falla a propósito el 50 % de las veces, por lo que es normal observar mensajes moviéndose de `orders.queue` a `orders.dlq`.

### Dashboard en el Frontend

La ruta `/rabbitmq` (`RabbitMQDashboard.tsx`) permite:

- Ver el estado del broker y la cantidad de mensajes en la cola principal y en la DLQ.
- Publicar logs y observar su enrutamiento por nivel.
- Enviar órdenes y visualizar el resultado del procesamiento (éxito o caída en DLQ).
- Crear colas, exchanges y bindings en tiempo real (RabbitAdmin).
- Purgar las colas del sistema.
- Pausar y reanudar el listener `order-listener`.

## 🌐 Endpoints y puertos

### Componentes principales

| Componente | Puerto | URL |
|---|---|---|
| Frontend | 5173 | http://localhost:5173 |
| API Gateway | 8095 | http://localhost:8095 |
| Eureka Server | 8761 | http://localhost:8761 |
| MySQL | 3306 | localhost:3306 |
| **RabbitMQ (broker AMQP)** | **5672** | `amqp://localhost:5672` |
| **RabbitMQ (UI de administración)** | **15672** | http://localhost:15672 |

### Microservicios

| Servicio | Puerto | URL |
|---|---|---|
| Notas | 8083 | localhost:8083 |
| Asignaturas | 8084 | localhost:8084 |
| Profesores | 8085 | localhost:8085 |
| Grupos | 8086 | localhost:8086 |
| Integrantes | 8087 | localhost:8087 |
| Roles | 8088 | localhost:8088 |
| Secciones | 8089 | localhost:8089 |
| Trabajos | 8090 | localhost:8090 |
| Entregas | 8091 | localhost:8091 |
| Comentarios | 8092 | localhost:8092 |
| **RabbitMQ Service** | **8093** | localhost:8093 |

En un despliegue normal, las peticiones del Frontend deberían realizarse a través del API Gateway, en lugar de acceder directamente a cada microservicio.

> ℹ️ La interfaz de administración de RabbitMQ (`http://localhost:15672`) utiliza las credenciales `admin` / `admin123` definidas en `docker-compose.yml`.

## 🧪 Pruebas

El proyecto contempla pruebas funcionales de los principales componentes del sistema.

Las herramientas utilizadas incluyen:

- Postman
- Swagger / OpenAPI
- Eureka Dashboard
- RabbitMQ Management UI
- Navegador web

Entre las operaciones que pueden probarse se encuentran:

- Crear registros.
- Listar registros.
- Buscar registros por ID.
- Actualizar registros.
- Eliminar registros.
- Relacionar integrantes y grupos.
- Verificar el registro de los servicios en Eureka.
- Validar autenticación y autorización.

### Pruebas específicas de RabbitMQ

| Caso de prueba | Verificación |
|---|---|
| Publicar un log `INFO` | Llega a `all_logs_queue` y se registra en el monitor general |
| Publicar un log `ERROR` | Llega simultáneamente a `all_logs_queue` y `errors_only_queue` |
| Publicar un log `WARNING` | Llega solo a `all_logs_queue` |
| Enviar una orden | Se enruta a `orders.queue` a través de `order.created` |
| Simulación de fallo (50 %) | El mensaje se redirige a `orders.dlq` y queda en cuarentena |
| Ver estado del broker | `GET /orders/status` refleja los mensajes de la cola y la DLQ |
| Crear cola / exchange / binding | Recursos creados en tiempo real vía RabbitAdmin |
| Purgar colas | `orders.queue`, `orders.dlq` y `all_logs_queue` quedan vacías |
| Pausar / reanudar listener | `order-listener` se detiene y se reinicia correctamente |

> 💡 Para observar el comportamiento de las colas en detalle, se recomienda revisar la UI de administración en http://localhost:15672 y los logs del contenedor con `docker compose logs -f rabbitmq-service`.

El Backend también contiene un documento específico con el plan de pruebas:

- `Backend/TESTING_PLAN.md`

## 🏗️ Terraform

El repositorio contiene una carpeta:

```
Terraform/
```

destinada a la configuración de infraestructura mediante Terraform.

Actualmente esta carpeta sirve como base para incorporar y administrar infraestructura como código dentro del proyecto.

```
Terraform/
└── .gitkeep
```

## 🔄 Arquitectura de despliegue

El sistema está preparado para ejecutarse utilizando contenedores Docker.

```
                       ┌─────────────────┐
                       │     Internet    │
                       └────────┬────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │    Frontend     │
                       │ React + Nginx   │
                       │      :5173      │
                       └────────┬────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │  API Gateway    │
                       │      :8095      │
                       └────────┬────────┘
                                │
               ┌────────────────┼────────────────┐
               │                │                │
               ▼                ▼                ▼
          Microservicio    Microservicio    Microservicio
               │                │                │
               └────────────────┼────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │      MySQL      │
                       │      :3306      │
                       └─────────────────┘

                       ┌─────────────────┐
                       │     Eureka      │
                       │      :8761      │
                       └─────────────────┘

                       ┌─────────────────┐
                       │    RabbitMQ     │
                       │  :5672 / :15672 │
                       └─────────────────┘
```

## 🚀 Inicio rápido

Si ya tienes Docker instalado y configuradas las variables de entorno necesarias:

### Paso 1: crear las variables de entorno

```bash
cp .env.example .env
```

Las variables necesarias se encuentran en `.env.example`:

```bash
ENTRA_TENANT_ID=<tenant-id>
ENTRA_AUDIENCE=api://<backend-client-id>
```

### Paso 2: crear los jars en el Backend

Desde la carpeta `Backend/`:

```powershell
"eureka-server", "gateway", "notas_service", "asignaturas_service", "profesores_service", "grupos_service", "integrantes_service", "roles_service", "secciones_service", "trabajos_service", "entregas_service", "comentarios_service", "rabbitmq_service" | ForEach-Object { Push-Location $_; .\mvnw.cmd clean package -DskipTests; Pop-Location }
```

### Paso 3: levantar los contenedores

En la terminal principal, desde la raíz del proyecto:

```bash
docker compose up -d --build
```

### Clonar el repositorio

```bash
git clone https://github.com/pandequeso2/Cloud_Native_Encargo.git
cd Cloud_Native_Encargo
git checkout Develop
docker compose up -d --build
```

### URLs de acceso

| Servicio | URL |
|---|---|
| Frontend | http://localhost:5173 |
| Dashboard RabbitMQ (en la app) | http://localhost:5173/rabbitmq |
| Eureka | http://localhost:8761 |
| API Gateway | http://localhost:8095 |
| RabbitMQ Management UI | http://localhost:15672 |

> ℹ️ El dashboard de RabbitMQ dentro de la aplicación requiere autenticación por tratarse de una ruta protegida del Frontend; los endpoints del microservicio (`/api/v1/rabbitmq/**`) son públicos en el Gateway.

## 🚀 Despliegue (CI/CD)

El repositorio incluye el workflow `.github/workflows/cd.yml`, que se ejecuta en cada `push` a la rama `Develop` (y de forma manual mediante `workflow_dispatch`).

El pipeline realiza dos etapas:

### 1. `build-and-push`

Compila cada servicio, construye la imagen Docker y la publica en **Amazon ECR** con dos tags: `latest` y el SHA del commit.

La matriz de servicios incluye `eureka-server`, `gateway`, los microservicios de dominio, `frontend`.

> ⚠️ **`rabbitmq-service` aún no está incluido en la matriz del workflow**, por lo que su imagen no se compila ni se publica en ECR. Para que el microservicio forme parte del despliegue en AWS, habría que agregar la entrada correspondiente:
>
> ```yaml
> - { name: rabbitmq-service, path: Backend/rabbitmq_service }
> ```
>
> Asimismo, `docker-compose.prod.yml` todavía no define los servicios `rabbitmq` ni `rabbitmq-service`, por lo que la mensajería no está presente en el despliegue de producción.

### 2. `deploy`

Se conecta por SSH a la instancia EC2, copia `docker-compose.prod.yml`, genera el archivo `.env` con los secretos y ejecuta:

```bash
sudo docker compose pull
sudo docker compose up -d
sudo docker image prune -f
```

El Frontend se despliega por separado en otra instancia, escuchando en los puertos `80` y `443`.

## 👥 Autores

Proyecto desarrollado para la asignatura Cloud Native.

- Benjamin Araya
- Matias Miranda
- Vicente Garrido

**Repositorio:** [pandequeso2/Cloud_Native_Encargo](https://github.com/pandequeso2/Cloud_Native_Encargo)

