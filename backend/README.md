# TechMind Engine - Backend API REST

API REST profesional desarrollada con **Spring Boot 3** e **Java 17+** para la plataforma **TechMind**. El sistema actúa como el núcleo de persistencia, lógica de negocio y orquestación entre clientes frontend y un microservicio externo de inteligencia artificial (**FastAPI**).

Incluye migraciones de base de datos controladas por versión, validación por secuencias, manejo global de errores con tolerancia a fallos y soporte para borrado y restauración lógica en cascada.

---

## 🛠️ Tecnologías Utilizadas

* **Java 17+ & Spring Boot 3**
* **Spring Data JPA & Hibernate**
* **MySQL 8** (Base de datos relacional)
* **Flyway** (Gestión y versionado de migraciones SQL)
* **Spring Validation** (Jakarta Validation)
* **RestClient** (Cliente HTTP reactivo/síncrono para consumir FastAPI)
* **Springdoc OpenAPI / Swagger UI** (Documentación interactiva de la API)

---

## 📂 Estructura del Proyecto

```plaintext
src
└── main
    ├── java
    │   └── com.techmind.backend
    │       ├── config
    │       │   └── RestClientConfig.java
    │       ├── controller
    │       │   ├── ContenidoController.java
    │       │   └── RecomendacionController.java
    │       ├── dto
    │       │   ├── ContenidoDTO.java
    │       │   ├── ContenidoResponseDto.java
    │       │   ├── DataScienceRequestDto.java
    │       │   ├── DataScienceResponseDto.java
    │       │   ├── KeywordDTO.java
    │       │   ├── PrediccionDTO.java
    │       │   ├── RecomendacionDTO.java
    │       │   └── RecomendacionResponseDto.java
    │       ├── entity
    │       │   ├── Contenido.java
    │       │   ├── Keyword.java
    │       │   ├── Prediccion.java
    │       │   └── Recomendacion.java
    │       ├── exception
    │       │   ├── GlobalExceptionHandler.java
    │       │   └── ResourceNotFoundException.java
    │       ├── repository
    │       │   ├── ContenidoRepository.java
    │       │   ├── KeywordRepository.java
    │       │   ├── PrediccionRepository.java
    │       │   └── RecommendationRepository.java
    │       ├── service
    │       │   ├── ClasificadorService.java
    │       │   ├── ContenidoService.java
    │       │   ├── PythonApiClient.java
    │       │   └── RecomendacionService.java
    │       ├── validation
    │       │   └── ValidationGroups.java
    │       └── BackendApplication.java
    └── resources
        ├── db/migration
        │   ├── V1__estructura_inicial.sql
        │   ├── V2__add_activo_column.sql
        │   ├── V3__add_activo_to_relations.sql
        │   └── V4__add_recomendacion_table.sql
        |   └── V5__conver_to_many_to_many_recommendations.sql
        └── application.properties
```

## 📋 Configuración de Entorno
Asegúrate de contar con las siguientes variables y servicios configurados en tu archivo `application.properties`:

```java
spring.application.name=backend

# MYSQL CONFIGURATION
spring.datasource.url=jdbc:mysql://localhost:3306/techmind
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}

# FLYWAY MIGRATIONS
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

# JPA / HIBERNATE
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

# FASTAPI INTEGRATION
python.api.url=http://localhost:8000

# DOCUMENTATION (SWAGGER UI)
springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true
```

## 🏛️ Modelo de Datos y Catálogo Reutilizable
La entidad `Recomendacion` funciona como un catálogo general reutilizable mediante una relación `@ManyToMany` con `Prediccion` respaldada por la tabla intermedia `prediccion_recomendacion`:

* **Optimización de Persistencia:** Se utiliza la restricción `UNIQUE (external_id)` para evitar registros duplicados.

* **Idempotencia:** Al procesar una nueva consulta de IA, el sistema verifica la existencia de la recomendación antes de insertar una nueva fila, reutilizando las existentes en la BD.

* **Aislamiento en Borrado Lógico:** La desactivación (`activo = false`) de un registro de `Contenido` solo oculta su `Prediccion` y sus `Keywords`. Las recomendaciones pertenecientes al catálogo global permanecen intactas para no afectar otros contenidos vinculados.

## 🚀 Endpoints de la API
1. Procesamiento y Gestión de Contenido (`/api/contenido`)

| Método | Endpoint | Descripción | Estado HTTP |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/contenido/procesar` | Envía texto al microservicio FastAPI (IA) y guarda la respuesta en MySQL | `201 Created` |
| `GET` | `/api/contenido` | Lista todos los contenidos activos registrados | `200 OK` |
| `GET` | `/api/contenido/{id}` | Obtiene un contenido activo específico por su ID | `200 OK` / `404 Not Found` |
| `DELETE` | `/api/contenido/{id}` | Desactiva el contenido y sus relaciones (Borrado Lógico) | `204 No Content` |
| `PATCH` | `/api/contenido/{id}/restaurar` | Reactiva un contenido y sus relaciones deshabilitadas | `200 OK` |

Ejemplos de Petición y Respuesta (`POST /api/contenido/procesar`):
```json
{
  "title": "HTML y CSS: Clases, Posicionamiento y Flexbox",
  "text": "Resumen del curso. Aprende qué son las clases CSS y su importancia. Conoce Flexbox y aplica técnicas para posicionar tus elementos de forma práctica. Aprende a estilizar tus textos, fuentes e iconos. Entiende cómo posicionar los elementos de una página con CSS. Descubre cómo aplicar efectos de estilización cambiando colores, redondeos e importando fuentes para tu proyecto. Público Objetivo: Personas que desean profundizar su conocimiento en HTML y CSS. Personas que desean aprender a como posicionar elementos en una pagina web con CSS."
}
```

Respuesta Exitosa (`201 Created`):
```json
{
    "category": "Frontend",
    "confidence": 0.9947767291228496,
    "keywords": [
        "html",
        "css",
        "flexbox"
    ],
    "recommendations": [
        {
            "id": 2,
            "title": "HTML y CSS: Clases, Posicionamiento y Flexbox",
            "type": "Curso",
            "level": "Básico",
            "language": "es",
            "url": "https://www.aluracursos.com/curso-online-html-css-clases-posicionamiento-flexbox",
            "category_recs": "Frontend"
        },
        {
            "id": 5,
            "title": "Bootstrap 5: crea una página web responsiva",
            "type": "Curso",
            "level": "Intermedio",
            "language": "es",
            "url": "https://www.aluracursos.com/curso-online-bootstrap-5-pagina-web-responsiva",
            "category_recs": "Frontend"
        },
        {
            "id": 1,
            "title": "HTML y CSS: ambientes de desarrollo, estructura de archivos y tags",
            "type": "Curso",
            "level": "Básico",
            "language": "es",
            "url": "https://www.aluracursos.com/curso-online-html-css-desarrollo-estructura-archivos-tags",
            "category_recs": "Frontend"
        },
        {
            "id": 4,
            "title": "CSS: Flexbox y layouts responsivos",
            "type": "Curso",
            "level": "Intermedio",
            "language": "es",
            "url": "https://www.aluracursos.com/curso-online-css-flexbox-layouts-responsivos",
            "category_recs": "Frontend"
        }
    ]
}
```

## Catálogo de Recomendaciones (`/api/recomendaciones`)

| Método | Endpoint | Parámetro Query | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/recomendaciones` | *Ninguno* | Lista todas las recomendaciones activas |
| `GET` | `/api/recomendaciones/{id}` | *Ninguno* | Busca una recomendación específica por ID |
| `GET` | `/api/recomendaciones` | `category={cat}` | Filtra recomendaciones por categoría |
| `GET` | `/api/recomendaciones` | `level={nivel}` | Filtra recomendaciones por nivel de dificultad |
| `GET` | `/api/recomendaciones` | `language={lang}` | Filtra recomendaciones por idioma |
| `GET` | `/api/recomendaciones` | `type={tipo}` | Filtra recomendaciones por tipo de recurso |

## ⚡ Validaciones y Secuenciación
El proyecto utiliza una estrategia de Validación por Secuencia Estricta (`ValidationGroups.SecuenciaOrdenada`) para evitar la acumulación de mensajes redundantes:

  * PrimerGrupo: Verifica la presencia y vacíos del dato (`@NotBlank`, `@NotNull`).

  * SegundoGrupo: Evalúa el formato y la expresión regular (`@Pattern`, `@Size`).

  * Default: Ejecuta las reglas estándar restantes.

Respuestas de Error de Validación (`400 Bad Request`).
Si se envía un parámetro de búsqueda vacío o con números en filtros alfabéticos:
```json
{
  "category": "La categoria no puede estar vacia"
}
```
```json
{
  "language": "El idioma solo debe contener letras"
}
```

## 🛡️ Manejo Global de Excepciones y Resiliencia
El `GlobalExceptionHandler` unifica las respuestas de error formateadas en JSON para toda la aplicación:

| Status Code | Excepción Interceptada | Causa / Descripción |
| :--- | :--- | :--- |
| `400 Bad Request` | `MethodArgumentNotValidException`, `HttpMessageNotReadableException`, `ConstraintViolationException`, `MethodArgumentTypeMismatchException` | Errores de sintaxis en JSON, tipos incoherentes o fallo en DTOs/Parámetros. |
| `404 Not Found` | `ResourceNotFoundException` | El recurso solicitado no existe en la base de datos. |
| `405 Method Not Allowed` | `HttpRequestMethodNotSupportedException` | Verbo HTTP no soportado en la ruta invocada. |
| `409 Conflict` | `DataIntegrityViolationException` | Violación de restricciones únicas o relacionales en la BD. |
| `502 Bad Gateway` | `HttpStatusCodeException` | Error interno propagado desde la API de IA (FastAPI). |
| `503 Service Unavailable` | `ResourceAccessException`, `CannotCreateTransactionException` | Caída de conexión con FastAPI (`http://localhost:8000`) o MySQL. |
| `504 Gateway Timeout` | `SocketTimeoutException` | La consulta a FastAPI superó el tiempo límite de respuesta (>5 segundos). |
| `500 Internal Server Error` | `Exception` | Error no controlado dentro de la aplicación. |

## 📄 Documentación Interactiva
Una vez ejecutada la aplicación, la documentación OpenAPI interactiva estará disponible en:

* Swagger UI: http://localhost:8080/swagger-ui.html
* API Docs (JSON): http://localhost:8080/v3/api-docs

## 🚀 Instalación y Ejecución Local

### Prerrequisitos
* **Java 17** o superior.
* **Maven 3.8+** (o utilizar el wrapper `./mvnw` incluido en el proyecto).
* **MySQL 8.0+** en ejecución local.
* Microservicio de IA (**FastAPI**) en ejecución en `http://localhost:8000` (opcional si solo se prueban endpoints de catálogo).

---

### Pasos para Ejecutar

1. **Clonar el repositorio y navegar al proyecto:**
   ```bash
   git clone <https://github.com/No-Country-simulation/g9-latam-team19-techmind.git>
   cd backend
   ```

2. **Crear la Base de Datos en MySQL:**
   Crea la base de datos localmente desde tu cliente o CLI de MySQL:
   ```bash
   CREATE DATABASE techmind;
   ```

3. **Configurar credenciales:**
   Verifica las propiedades en `src/main/resources/application.properties` y define tus credenciales locales de MySQL:
   ```bash
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña
   ```

4. **Ejecutar migraciones y levantar la aplicación:**
   Flyway se encargará de ejecutar los scripts SQL contenidos en `db/migration` al iniciar el servidor:
   ```bash
   ./mvnw spring-boot:run
   ```

5. **🧪 Pruebas de Endpoints**
   Una vez iniciada la aplicación (`http://localhost:8080`), puedes realizar pruebas directamente sin depender del cliente web:

   *  **Documentación Interactiva (Swagger UI):** Abre `http://localhost:8080/swagger-ui.html` en tu navegador para probar todos los endpoints disponibles de forma gráfica.
   *  **Pruebas vía Postman / cURL:** Puedes consumir directamente los endpoints descritos en la sección de Endpoints de la API (ej. `GET /api/recomendaciones o GET /api/contenido`).
