# Plan De Pruebas - Microservicios

## Objetivo

Evaluar el funcionamiento de los microservicios del proyecto en base a las pruebas automatizadas (JUnit 5 + Mockito + MockMvc) actualmente implementadas en el código.

### Microservicios testeados:

* grupos_service
* integrantes_service
* trabajos_service
* eureka-server (solo carga de contexto)
* gateway (solo carga de contexto)

---

# integrantes_service


## P-01 MOSTRAR INTEGRANTES.
### Metodo: obtenerTodosLosIntegrantes (GET /api/v1/integrantes)
### Objetivos:
- Mostrar integrantes junto a un mensaje de aprobación.
- En caso de no existir integrantes, mostrar un mensaje diciendo que no hay integrantes.

### Objetivos cumplidos: 2/2

## P-02 MOSTRAR UN INTEGRANTE POR UN ID ESPECÍFICO.
### Metodo: GET /api/v1/integrantes/{id}
### Objetivos:
- Ingresar un ID y mostrar al integrante del mismo.
- Mostrar mensaje de error en caso de que no exista un integrante con el ID ingresado.

### Objetivos cumplidos: 2/2

---

# grupos_service

## P-01 MOSTRAR GRUPOS.
### Metodo: obtenerGrupos (GET /api/v1/grupos)
### Objetivos:
- Mostrar grupos junto a un mensaje de aprobación.
- En caso de no existir grupos, mostrar un mensaje diciendo que no hay grupos.

### Objetivos cumplidos: 2/2

## P-02 MOSTRAR UN GRUPO POR UN ID ESPECÍFICO.
### Metodo: buscarGrupoPorId (GET /api/v1/grupos/{id})
### Objetivos:
- Ingresar un ID y mostrar el grupo perteneciente al mismo.
- Mostrar mensaje de error en caso de que no exista un grupo con el ID ingresado.

### Objetivos cumplidos: 2/2


## P-06 MOSTRAR GRUPO JUNTO A SUS INTEGRANTES.
### Metodo: obtenerGrupoConIntegrantes (GET /api/v1/grupos/{idGrupo}/con-integrantes)
### Objetivos:
- Mostrar el grupo junto al listado de sus integrantes.

### Objetivos cumplidos: 1/1

## P-07 MOSTRAR GRUPO JUNTO A SU TRABAJO.
### Metodo: obtenerGrupoConTrabajo (GET /api/v1/grupos/{idGrupo}/con-trabajo)
### Objetivos:

- Mostrar el grupo junto al trabajo que tiene asignado.

### Objetivos cumplidos: 1/1

---

# eureka-server y gateway

## P-01 VERIFICAR QUE EL CONTEXTO DE SPRING BOOT LEVANTA CORRECTAMENTE.
### Metodo: contextLoads (`EurekaServerApplicationTests`, `GatewayApplicationTests`)
### Objetivos:
- Confirmar que el ApplicationContext de eureka-server levanta sin errores.
- Confirmar que el ApplicationContext de gateway levanta sin errores.

### Objetivos cumplidos: 2/2

---

## P-FINAL VERIFICAR SI LOS MICROSERVICIOS SE MUESTRAN EN EUREKA

### Se muestran todos los módulos en Eureka (localhost:8761)

