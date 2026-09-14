☁️ Cloud Native Encargo

Sistema académico desarrollado bajo una arquitectura de microservicios, como parte de la asignatura Cloud Native.

El proyecto permite gestionar distintos aspectos de un entorno académico, incluyendo integrantes, grupos, asignaturas, secciones, profesores, roles, trabajos, entregas, notas y comentarios.

La solución está compuesta por un Frontend desarrollado con React + TypeScript y un Backend basado en microservicios con Spring Boot, utilizando Spring Cloud Netflix Eureka para el descubrimiento de servicios y Spring Cloud Gateway como punto de entrada a la API.

📋 Tabla de contenidos
Descripción
Arquitectura
Tecnologías utilizadas
Estructura del proyecto
Microservicios
Requisitos
Configuración
Ejecución con Docker
Ejecución manual
Frontend
Autenticación
Endpoints y puertos
Pruebas
Terraform
Autores
📖 Descripción

Cloud Native Encargo es una aplicación académica diseñada utilizando principios de arquitectura Cloud Native y microservicios.

El sistema separa las distintas funcionalidades del dominio académico en servicios independientes. Cada microservicio puede ejecutarse de forma independiente y se registra en un servidor de descubrimiento Eureka.

Las solicitudes externas son gestionadas mediante un API Gateway, que se encarga de enrutar las peticiones hacia el microservicio correspondiente.

El proyecto también incorpora:

🐳 Contenedores Docker.
🗄️ Base de datos MySQL.
🔎 Service Discovery mediante Eureka.
🚪 API Gateway.
🔐 Autenticación mediante Microsoft Entra ID.
⚛️ Frontend SPA con React y TypeScript.
🏗️ Infraestructura preparada mediante Terraform.
🧪 Pruebas mediante Postman, Swagger/OpenAPI y Eureka Dashboard.
🏗️ Arquitectura

La arquitectura general del sistema puede representarse de la siguiente manera:

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

Flujo de una solicitud
El usuario interactúa con el Frontend.
El Frontend obtiene la autenticación mediante Microsoft Entra ID.
Las peticiones son enviadas al API Gateway.
El Gateway identifica el microservicio correspondiente.
Eureka permite localizar los servicios registrados.
El microservicio procesa la solicitud.
Los servicios utilizan MySQL para la persistencia de datos.
La respuesta retorna al Frontend a través del Gateway.
🛠️ Tecnologías utilizadas
Backend
Java 17+
Spring Boot
Spring Cloud
Spring Cloud Netflix Eureka
Spring Cloud Gateway
Maven
MySQL
Frontend
React
TypeScript
Vite
MSAL React
Microsoft Entra ID
Nginx
Infraestructura
Docker
Docker Compose
Terraform
GitHub Actions
📁 Estructura del proyecto
Cloud_Native_Encargo/
│
├── .github/
│   └── workflows/
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
│   ├── roles_service/
│   ├── secciones_service/
│   └── trabajos_service/
│
├── Frontend/
│   ├── public/
│   ├── src/
│   │   ├── api/
│   │   ├── auth/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── routes/
│   │   ├── styles/
│   │   └── types/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
│
├── Terraform/
│
├── docker-compose.yml
├── .gitignore
└── README.md

🔧 Microservicios

El Backend está compuesto por los siguientes servicios:

Servicio	Descripción
eureka-server	Registro y descubrimiento de servicios
gateway	Punto de entrada y enrutamiento de la API
asignaturas_service	Gestión de asignaturas
comentarios_service	Gestión de comentarios
entregas_service	Gestión de entregas
grupos_service	Gestión de grupos
integrantes_service	Gestión de integrantes/estudiantes
notas_service	Gestión de notas
profesores_service	Gestión de profesores
roles_service	Gestión de roles y permisos
secciones_service	Gestión de secciones
trabajos_service	Gestión de trabajos/tareas

Cada microservicio se encuentra separado y puede registrarse en Eureka para ser localizado por el resto de la arquitectura.

📦 Requisitos

Para ejecutar el proyecto localmente se recomienda contar con:

Java JDK 17 o superior
Maven 3.8+
Node.js 20+
npm
Docker
Docker Compose
Una aplicación registrada en Microsoft Entra ID para la autenticación del Frontend y Backend.
⚙️ Configuración
Variables de entorno

El proyecto utiliza variables de entorno para configurar principalmente la autenticación mediante Microsoft Entra ID.

En el Frontend se debe configurar un archivo:

Frontend/.env


A partir del archivo de ejemplo:

cd Frontend
cp .env.example .env


Las variables principales son:

VITE_ENTRA_CLIENT_ID=<client-id-del-SPA>
VITE_ENTRA_TENANT_ID=<tenant-id>
VITE_ENTRA_API_CLIENT_ID=<client-id-del-backend>
VITE_API_BASE_URL=http://localhost:8095
VITE_REDIRECT_URI=http://localhost:5173


Importante: no se deben subir credenciales, secretos, tokens ni archivos .env con información sensible al repositorio.

El VITE_REDIRECT_URI debe coincidir con el Redirect URI configurado en el registro de la aplicación SPA de Microsoft Entra ID.

🐳 Ejecución con Docker

La forma recomendada para ejecutar el sistema completo es mediante Docker Compose.

1. Clonar el repositorio
git clone https://github.com/pandequeso2/Cloud_Native_Encargo.git
cd Cloud_Native_Encargo


Cambiar a la rama Develop:

git checkout Develop

2. Configurar las variables de entorno

Configurar las variables necesarias para Microsoft Entra ID antes de iniciar los contenedores.

3. Construir y ejecutar
docker compose up -d --build


Para revisar los contenedores:

docker compose ps


Para visualizar los logs:

docker compose logs -f

4. Detener el proyecto
docker compose down


Para detener los contenedores y eliminar también los volúmenes:

docker compose down -v


⚠️ docker compose down -v elimina los volúmenes asociados, por lo que puede provocar pérdida de los datos almacenados en MySQL.

🖥️ Frontend

El Frontend está desarrollado como una SPA utilizando:

React
TypeScript
Vite
MSAL
Microsoft Entra ID

En desarrollo puede ejecutarse directamente mediante:

cd Frontend
npm install
npm run dev


La aplicación estará disponible normalmente en:

http://localhost:5173


Para generar una versión de producción:

npm run build


Y para visualizar el build:

npm run preview

🔐 Autenticación

La autenticación del Frontend utiliza Microsoft Entra ID mediante @azure/msal-react.

El sistema contempla:

Inicio de sesión.
Cierre de sesión.
Protección de rutas.
Obtención de tokens.
Envío del token al Backend.
Lectura de roles y claims.
Renovación silenciosa del token cuando es posible.

La comunicación con el Backend se realiza mediante el API Gateway.

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

🌐 Endpoints y puertos
Componente	Puerto	URL
Frontend	5173	http://localhost:5173
API Gateway	8095	http://localhost:8095
Eureka Server	8761	http://localhost:8761
MySQL	3306	localhost:3306
Notas	8083	localhost:8083
Asignaturas	8084	localhost:8084
Profesores	8085	localhost:8085
Grupos	8086	localhost:8086
Integrantes	8087	localhost:8087
Roles	8088	localhost:8088
Secciones	8089	localhost:8089
Trabajos	8090	localhost:8090
Entregas	8091	localhost:8091
Comentarios	8092	localhost:8092

En un despliegue normal, las peticiones del Frontend deberían realizarse a través del API Gateway, en lugar de acceder directamente a cada microservicio.

🧪 Pruebas

El proyecto contempla pruebas funcionales de los principales componentes del sistema.

Las herramientas utilizadas incluyen:

Postman
Swagger / OpenAPI
Eureka Dashboard
Navegador web

Entre las operaciones que pueden probarse se encuentran:

Crear registros.
Listar registros.
Buscar registros por ID.
Actualizar registros.
Eliminar registros.
Relacionar integrantes y grupos.
Verificar el registro de los servicios en Eureka.
Validar autenticación y autorización.

El Backend también contiene un documento específico con el plan de pruebas:

Backend/TESTING_PLAN.md

🏗️ Terraform

El repositorio contiene una carpeta:

Terraform/


destinada a la configuración de infraestructura mediante Terraform.

Actualmente esta carpeta sirve como base para incorporar y administrar infraestructura como código dentro del proyecto.

Terraform/
└── .gitkeep

🔄 Arquitectura de despliegue

El sistema está preparado para ejecutarse utilizando contenedores Docker.

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

🚀 Inicio rápido

Si ya tienes Docker instalado y configuradas las variables de entorno necesarias:

git clone https://github.com/pandequeso2/Cloud_Native_Encargo.git

cd Cloud_Native_Encargo

git checkout Develop

docker compose up -d --build


Luego puedes acceder a:

Frontend:
http://localhost:5173

Eureka:
http://localhost:8761

API Gateway:
http://localhost:8095

👥 Autores

Proyecto desarrollado para la asignatura Cloud Native.
Benjamin Araya
Matias Miranda
Vicente Garrido

Repositorio:

pandequeso2/Cloud_Native_Encargo

