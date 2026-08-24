# 🚀 TechMind Engine

**TechMind Engine** es un catálogo inteligente diseñado para centralizar, organizar y recomendar recursos de aprendizaje tecnológico. La plataforma integra un motor de Inteligencia Artificial que analiza contenido técnico en texto plano, clasifica automáticamente su categoría principal, extrae palabras clave y sugiere recursos educativos relevantes (cursos, artículos y documentación) en tiempo real.

> Proyecto desarrollado durante la simulación **No Country Hackathon ONE** (G9-LATAM - Equipo 19) en colaboración con Alura y Oracle.

---

### 🌐 Prueba en Vivo (Demo)

El ecosistema completo (Frontend, Backend API y Data Science API) se encuentra desplegado y ejecutándose en un entorno de producción en **Oracle Cloud Infrastructure (OCI)**. Puedes probar toda la experiencia integrada ingresando directamente a:

👉 **[https://136.248.73.33/](https://136.248.73.33/)**

> **Nota:** Al acceder al enlace web del Frontend, la aplicación interactúa automáticamente en segundo plano con la API de Spring Boot y el pipeline de Data Science sin necesidad de realizar configuraciones adicionales.

## 🏗️ Arquitectura General del Sistema

El ecosistema está construido bajo una arquitectura desacoplada de microservicios e interfaz web, totalmente containerizada y desplegada en **Oracle Cloud Infrastructure (OCI)** con soporte HTTPS y Reverse Proxy vía Apache.

```plaintext
                                [ Usuario ]
                                     │
                                 HTTPS :443
                                     ▼
                            Apache HTTP Server
                         (/var/www/techmind)
                                  │
                 ┌────────────────┴────────────────┐
                 ▼                                 ▼
         [ Frontend Web ]                 [ Backend API ]
         React 18 + Vite                Spring Boot 3 (Java 17)
          (Archivos Estáticos)                 :8080
                                                   │
                                     ┌─────────────┴─────────────┐
                                     ▼                           ▼
                             [ Data Science API ]          [ Base de Datos ]
                             FastAPI (Python 3)               MySQL 8.0
                                   :8000                        :3306
```

## 🔄 Flujo de Datos

1. **Frontend:** El usuario envía un título y un extracto técnico desde la interfaz web.
2. **Backend:** Recibe la solicitud vía `/api/contenido/procesar` y orquesta la validación y comunicación.
3. **Data Science:** Un servicio FastAPI procesa el texto, extrae keywords, genera embeddings (`all-MiniLM-L6-v2`) y clasifica la categoría con un modelo de Logistic Regression.
4. **Recomendación y Persistencia:** El pipeline selecciona los mejores recursos del catálogo, el Backend persiste el resultado idempotente en MySQL y lo retorna formateado a la UI.

## 📂 Módulos del Proyecto
Cada área cuenta con su propio entorno y documentación detallada:

| Módulo | Tecnología Principal | Descripción | Documentación |
| :--- | :--- | :--- | :--- |
| **Frontend** | React 18, TypeScript, Vite, CSS3 | Interfaz de usuario interactiva, dashboard de recursos y modales de análisis. | `📂 frontend/README.md` |
| **Backend** | Spring Boot 3, Java 17, JPA, Flyway | Núcleo de persistencia, reglas de negocio, validaciones y orquestación REST. | `📂 backend/README.md` |
| **Data Science** | Python 3, FastAPI, Scikit-learn, OCI | Extracción de palabras clave, clasificación por ML y motor de recomendaciones. | `📂 data-science/README.md` |

## 🧪 Pruebas de API con Postman / cURL
Puedes probar los endpoints de forma directa importando las peticiones en Postman o ejecutando los comandos `curl` en tu terminal.

1. **Backend Principal (Spring Boot API):** Host Producción: [https://136.248.73.33](https://136.248.73.33/api/contenido)

### Procesar y guardar nuevo contenido con IA
* **Método:** `POST`
* **Endpoint:** `/api/contenido/procesar`
* **Headers:** `Content-Type: application/json`
* **Body (JSON):**
  ```json
  {
  "title": "Introducción a React y Componentes",
  "text": "Aprende a crear componentes reutilizables con React, JSX y manejo de estado con Hooks como useState y useEffect."
  }
  ```
### Listar todos los contenidos activos
* **Método:** GET
* **Endpoint:** `/api/contenido`

### Consultar catálogo de recomendaciones con filtro
* **Método:** `GET`
* **Endpoint:** `/api/recomendaciones?category=Frontend`

## 🌐 Entorno de Despliegue (OCI)
El sistema se encuentra desplegado en una Máquina Virtual en Oracle Cloud Infrastructure (OCI) corriendo Oracle Linux 8.

* **Servidor Web:** Apache 2.4 con `mod_ssl` y certificado SSL emitido por Let's Encrypt.
* **Administración de Servicios:** `techmind-backend.service` (Spring Boot) y `techmind-python.service` (FastAPI) administrados mediante `systemd`.
* **Ubicación de Producción:** /opt/techmind

## ⚡ Instalación y Ejecución Local
Prerrequisitos Globales
* Java: 17+ & Maven 3.8+
* Node.js: v16+ (recomendado v22)
* Python: 3.9+
* MySQL: 8.0+

## Pasos para Iniciar los Servicios
1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/No-Country-simulation/g9-latam-team19-techmind.git](https://github.com/No-Country-simulation/g9-latam-team19-techmind.git)
   cd g9-latam-team19-techmind
   ```
2. **Iniciar Data Science (Python/FastAPI):**
   ```bash
   cd data_science
   python -m venv venv
   source venv/bin/activate  # En Windows: venv\Scripts\activate
   pip install -r requirements.txt
   uvicorn api:app --reload --port 8000
   ```
3. **Iniciar Backend (Spring Boot):**
   ```bash
   cd ../backend
   # Configurar credenciales de MySQL en src/main/resources/application.properties
   ./mvnw spring-boot:run
   ```
4. **Iniciar Frontend (React/Vite):**
   ```bash
   cd ../frontend/techmind-frontend
   npm install
   npm run dev
   ```

### 👥 Equipo de Desarrollo
Proyecto desarrollado por el equipo G9-LATAM - Equipo 19:

| Integrante | Rol / Especialidad | Responsabilidades Clave |
| :--- | :--- | :--- |
| **Ana Berenice Noriega Camacho** | Tech Lead & Data Science Lead | Liderazgo técnico del proyecto, diseño de la arquitectura de IA, entrenamiento del modelo de ML y pipeline de NLP con FastAPI. |
| **Juan Camarillo Gutiérrez** | Backend Lead Developer | Arquitectura y desarrollo completo de la API REST en Spring Boot, integración con FastAPI/Flyway, y supervisión técnica de la integración frontend. |
| **Valeria Villicana Ponce de Leon** | Full-Stack Engineer (Frontend Focus) | Desarrollo de la interfaz interactiva en React, integración con la API REST y soporte en el diseño y modelado de la base de datos MySQL. |
| **Jorge Luis Marquez Miguel** | DevOps & Cloud Engineer | Configuración y despliegue del ecosistema completo en OCI (Apache, SSL, Systemd) y apoyo en el desarrollo de la API Backend. |
| **Joan Valle** | Data Scientist | Desarrollo del pipeline de NLP, entrenamiento del modelo clasificador y extracción de keywords. |
| **Sara Rosaura Rapalino Vasquez** | Data Scientist | Curaduría del catálogo de recomendaciones, evaluación del modelo y pruebas de integración con FastAPI. |
| **Harold David Perez Martinez** | Data Scientist | Procesamiento de texto, generación de vectorizaciones (*embeddings*) y optimización del modelo de ML. |
   
