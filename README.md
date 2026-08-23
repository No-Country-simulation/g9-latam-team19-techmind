# TECHMIND ENGINE - Frontend 🚀

Bienvenido al repositorio frontend de Techmind Engine. Este proyecto fue desarrollado para el Hackathon ONE (G9-LATAM - Equipo 19) en colaboración con Alura y Oracle.

Techmind es un catálogo inteligente diseñado para centralizar, organizar y sugerir recursos de aprendizaje en tecnología. Su principal característica es la integración con un backend de Inteligencia Artificial que analiza y clasifica automáticamente cada nuevo contenido que registras.

## ✨ Características Principales

* Dashboard Central: Una vista principal que muestra todo tu contenido tecnológico organizado en tarjetas, con barra de búsqueda y filtros rápidos por categoría.
* Análisis Predictivo (IA): Al ingresar un fragmento de texto técnico, la aplicación se comunica con el modelo de IA para detectar la categoría (Backend, Frontend, DevOps, etc.), extraer palabras clave y calcular un porcentaje de confianza. 
* Recomendaciones Inteligentes: El sistema sugiere automáticamente contenido relacionado (cursos, artículos, documentación) y lo organiza en estantes interactivos con filtros por idioma.
* Diseño y UX: Implementación de un diseño limpio con CSS puro. Incluye modo oscuro, modales de detalles interactivos y notificaciones para confirmar acciones o reportar errores sin interrumpir la navegación.

## 🛠️ Stack Tecnológico

* Core: React 18
* Lenguaje: TypeScript
* Estilos: CSS3 puro (con variables nativas para Theming)
* Entorno de desarrollo: Vite

## ⚙️ Instalación y Ejecución Local

Para probar el proyecto en tu entorno local, asegúrate de tener instalado Node.js (v16 o superior). 

Importante: Este frontend requiere que el backend de Spring Boot de Techmind esté en ejecución (por defecto en http://localhost:8080) junto con su base de datos MySQL para funcionar correctamente.

1. Clona este repositorio:
git clone <https://github.com/No-Country-simulation/g9-latam-team19-techmind.git>
cd <frontend>
cd <techmind-frontend>

2. Instala las dependencias necesarias:
npm install

3. Inicia el servidor de desarrollo:
npm run dev

Una vez que el servidor inicie, abre tu navegador en la dirección indicada en la terminal.

## 🔌 Conexión con el Backend

El frontend se comunica con el servidor a través de los siguientes endpoints principales:

* GET /api/contenido: Recupera el catálogo completo de contenidos, incluyendo las predicciones, palabras clave y recomendaciones generadas por la IA.
* POST /api/contenido/procesar: Envía el texto y título de un nuevo recurso al motor de análisis para su evaluación y guardado automático.


---
Desarrollado por el Equipo 19
Agosto 2026
