# TECHMIND ENGINE - Frontend 🚀

[svg](https://github.com/No-Country-simulation/g9-latam-team19-techmind#techmind-engine---frontend-)

Bienvenido al repositorio frontend de Techmind Engine. Este proyecto fue desarrollado para el Hackathon ONE (G9-LATAM - Equipo 19) en colaboración con Alura y Oracle.

Techmind es un catálogo inteligente diseñado para centralizar, organizar y sugerir recursos de aprendizaje en tecnología. Su principal característica es la integración con un backend de Inteligencia Artificial que analiza y clasifica automáticamente cada nuevo contenido que registras.

## ✨ Características Principales

[svg](https://github.com/No-Country-simulation/g9-latam-team19-techmind#-caracter%C3%ADsticas-principales)

- Dashboard Central: Una vista principal que muestra todo tu contenido tecnológico organizado en tarjetas, con barra de búsqueda y filtros rápidos por categoría.
- Análisis Predictivo (IA): Al ingresar un fragmento de texto técnico, la aplicación se comunica con el modelo de IA para detectar la categoría (Backend, Frontend, DevOps, etc.), extraer palabras clave y calcular un porcentaje de confianza.
- Recomendaciones Inteligentes: El sistema sugiere automáticamente contenido relacionado (cursos, artículos, documentación) y lo organiza en estantes interactivos con filtros por idioma.
- Diseño y UX: Implementación de un diseño limpio con CSS puro. Incluye modo oscuro, modales de detalles interactivos y notificaciones para confirmar acciones o reportar errores sin interrumpir la navegación.

## 🛠️ Stack Tecnológico

[svg](https://github.com/No-Country-simulation/g9-latam-team19-techmind#%EF%B8%8F-stack-tecnol%C3%B3gico)

- Core: React 18
- Lenguaje: TypeScript
- Estilos: CSS3 puro (con variables nativas para Theming)
- Entorno de desarrollo: Vite

## ⚙️ Instalación y Ejecución Local

[svg](https://github.com/No-Country-simulation/g9-latam-team19-techmind#%EF%B8%8F-instalaci%C3%B3n-y-ejecuci%C3%B3n-local)

Para probar el proyecto en tu entorno local, asegúrate de tener instalado Node.js (v16 o superior).

Importante: Este frontend requiere que el backend de Spring Boot de Techmind esté en ejecución (por defecto en [http://localhost:8080](http://localhost:8080)) junto con su base de datos MySQL para funcionar correctamente.

1. Clona este repositorio:

```bash
git clone https://github.com/No-Country-simulation/g9-latam-team19-techmind.git
cd g9-latam-team19-techmind/frontend/techmind-frontend
```

2. Instala las dependencias necesarias:

```bash
npm install
```

3. Inicia el servidor de desarrollo:

```bash
npm run dev
```

Una vez que el servidor inicie, abre tu navegador en la dirección indicada en la terminal.

## 🔌 Conexión con el Backend

[svg](https://github.com/No-Country-simulation/g9-latam-team19-techmind#-conexi%C3%B3n-con-el-backend)

El frontend se comunica con el servidor a través de los siguientes endpoints principales:

- GET `/api/contenido`: Recupera el catálogo completo de contenidos, incluyendo las predicciones, palabras clave y recomendaciones generadas por la IA.
- POST `/api/contenido/procesar`: Envía el texto y título de un nuevo recurso al motor de análisis para su evaluación y guardado automático.

## ☁️ Implementación en Oracle Cloud Infrastructure (OCI)

El frontend de TechMind Engine fue desplegado en una instancia Compute de Oracle Cloud Infrastructure (OCI), junto con el backend Spring Boot, el servicio de Data Science y la base de datos MySQL.

El repositorio de producción se mantiene en:

```text
/opt/techmind
```

El proyecto frontend se encuentra específicamente en:

```text
/opt/techmind/frontend/techmind-frontend
```

Para el entorno de producción se utilizó Node.js 22 mediante NVM. Las dependencias del proyecto se instalaron utilizando el archivo `package-lock.json`:

```bash
npm ci
```

Posteriormente se generó el build de producción con Vite:

```bash
npm run build
```

Este proceso genera los archivos estáticos optimizados dentro del directorio:

```text
/opt/techmind/frontend/techmind-frontend/dist/
```

Los archivos generados fueron publicados en:

```text
/var/www/techmind/
```

Apache HTTP Server se configuró como servidor web y Reverse Proxy.

Su función dentro de la arquitectura es:

- Servir el frontend React generado por Vite.
- Atender las conexiones HTTP y HTTPS de los usuarios.
- Gestionar el certificado SSL/TLS.
- Redirigir las solicitudes `/api/*` hacia el backend Spring Boot que se ejecuta internamente en el puerto `8080`.

La arquitectura de producción queda de la siguiente manera:

```text
Usuario
   │
   │ HTTPS :443
   ▼
Apache HTTP Server
   │
   ├── / ──────────────► React / Vite
   │                     /var/www/techmind
   │
   └── /api/* ─────────► Spring Boot :8080
                              │
                              ├── FastAPI :8000
                              │
                              └── MySQL :3306
```

El frontend utiliza rutas relativas para comunicarse con el backend:

```text
/api/contenido
/api/contenido/procesar
```

Esto permite que React se comunique con Spring Boot a través del mismo dominio y protocolo utilizado para acceder a la aplicación, sin almacenar directamente la IP o el puerto `8080` dentro del código frontend.

El acceso público a la aplicación desplegada se realiza mediante:

```text
https://136.248.73.33/
```

Para HTTPS se configuró Apache con `mod_ssl` y un certificado SSL/TLS emitido por Let's Encrypt mediante Certbot.

---

Desarrollado por el Equipo 19 Agosto 2026