# 🧠 Data Science Pipeline

## 📌 Descripción

Este módulo contiene el pipeline de Data Science de TechMind, encargado de analizar contenido técnico, identificar sus categorías principales y generar recomendaciones relevantes a partir de la base de conocimiento.

El pipeline integra tres procesos principales:

1. **Extracción de keywords**
2. **Clasificación del contenido mediante Machine Learning**
3. **Generación de recomendaciones**

El pipeline recibe un documento con un título y un texto, procesa el contenido, extrae keywords, genera un embedding para el texto, clasifica el contenido mediante un modelo previamente entrenado y utiliza las categorías y keywords obtenidas para seleccionar recursos relacionados de la base de conocimiento.

El resultado final contiene:

- Categoría principal detectada.
- Confianza de la categoría principal.
- Keywords detectadas.
- Recomendaciones relacionadas.

El módulo está diseñado para integrarse con Backend mediante la función principal:

    process_data()

---

# 🏗️ Arquitectura del proyecto

    Documento de entrada
            |
            v
       extractor.py
       process_data()
            |
            +------------------+------------------+
            |                  |                  |
            v                  v                  v
      Keyword Pipeline    Model Pipeline    Recommendation Pipeline
            |                  |                  |
            v                  v                  v
    keyword_functions.py  model_functions.py  recommendation_functions.py
            |                  |                  |
            v                  v                  v
        keywords        category + confidence    recommendations
            |                  |                  |
            +------------------+------------------+
                               |
                               v
                         Resultado final
                               |
                               v
                             JSON

---

# 📂 Estructura del proyecto

    data_science/
    │
    ├── extractor.py
    │   └── Pipeline principal
    │       ├── process_keywords()
    │       ├── process_model()
    │       ├── generate_recommendations()
    │       └── process_data()
    │
    ├── keyword_functions.py
    │   ├── extract_text()
    │   ├── clean_text()
    │   ├── load_keyword_catalog()
    │   └── extract_keywords()
    │
    ├── model_functions.py
    │   ├── load_model()
    │   ├── generate_embedding()
    │   └── predict_category()
    │
    ├── recommendation_functions.py
    │   ├── load_knowledge_base()
    │   ├── categories_for_recommendation()
    │   ├── filter_by_category()
    │   ├── calculate_keyword_matches()
    │   ├── rank_by_matches()
    │   ├── select_recommendations()
    │   └── format_recommendations()
    │
    ├── keyword_catalog.json
    │   └── Lista de keywords utilizada para la extracción
    │
    ├── update_catalog.ipynb
    │   └── Notebook para actualizar el catálogo de keywords
    │
    ├── keyword_extractor_testing.ipynb
    │   └── Notebook para probar la extracción de keywords
    │
    └── recommendations_testing.ipynb
        └── Notebook para probar el algoritmo de recomendaciones

El modelo de clasificación y la base de conocimiento se almacenan en Oracle Cloud Infrastructure (OCI) y se cargan durante la ejecución del pipeline.

---

# ⚙️ Funcionamiento general

El pipeline completo sigue los siguientes pasos:

    1. Recibir documento
            |
            v
    2. Extraer título + texto
            |
            v
    3. Limpiar y normalizar texto
            |
            +-------------------------+
            |                         |
            v                         v
    4. Extraer keywords        5. Generar embedding
            |                         |
            |                         v
            |                  6. Clasificar contenido
            |                         |
            |                         v
            |                Obtener categoría principal
            |                + categoría secundaria
            |                + sus confianzas
            |                         |
            +------------+------------+
                         |
                         v
              7. Determinar categorías
                 para recomendaciones
                         |
                         v
              8. Filtrar knowledge base
                         |
                         v
              9. Calcular coincidencias
                         |
                         v
             10. Ordenar resultados
                         |
                         v
             11. Seleccionar recomendaciones
                         |
                         v
             12. Dar formato a recomendaciones
                         |
                         v
                    Resultado JSON

---

# 📥 Formato de entrada

El pipeline espera un objeto tipo diccionario/JSON que contenga los campos `title` y `text`.

Ejemplo:

    {
        "title": "Angular: Controla la navegación",
        "text": "Aprende a crear componentes reutilizables con spring boot y python"
    }

---

# 📤 Formato de salida

El resultado final contiene la categoría principal detectada, su confianza, las keywords encontradas y las recomendaciones generadas.

Ejemplo:

    {
        "category": "Frontend",
        "confidence": 0.86,
        "keywords": [
            "angular",
            "spring boot",
            "python"
        ],
        "recommendations": [
            {   "id": 23,
                "title": "Angular Routing",
                "category_recs": "Frontend",
                "type": "Course",
                "level": "Intermediate",
                "language": "Spanish",
                "url": "https://example.com"
            }
        ]
    }

---

# 🔎 Extracción de Keywords

## `keyword_functions.py`

### Propósito

Contiene las funciones utilizadas para procesar el texto y detectar keywords relevantes a partir de un catálogo predefinido.

### `extract_text(data: dict) -> str`

Extrae los campos `title` y `text` del documento de entrada y los combina en una sola cadena de texto.

### `clean_text(raw_text: str) -> str`

Normaliza el texto mediante:

- Conversión a minúsculas.
- Eliminación de saltos de línea.
- Eliminación de espacios múltiples.

### `load_keyword_catalog(filename: str) -> list`

Carga el catálogo de keywords desde `keyword_catalog.json`.

### `extract_keywords(cleaned_text: str, catalog: list) -> list`

Busca las keywords presentes en el texto utilizando el catálogo predefinido.

La búsqueda utiliza expresiones regulares para identificar palabras y frases completas y evitar coincidencias accidentales dentro de otras palabras.

---

# 🤖 Clasificación del contenido

## `model_functions.py`

### Propósito

Contiene las funciones utilizadas para generar la representación numérica del texto y realizar la clasificación mediante el modelo de Machine Learning previamente entrenado.

El proceso de clasificación transforma el texto limpio en un embedding y posteriormente utiliza este embedding como entrada para el clasificador.

---

## 🔢 Generación de embeddings

Para representar el texto numéricamente se utiliza el modelo:

    all-MiniLM-L6-v2

El texto limpio se transforma en un embedding antes de enviarlo al clasificador.

El embedding representa semánticamente el contenido del texto como un vector numérico que puede utilizarse como entrada para el modelo de clasificación.

### `generate_embedding(cleaned_text: str, model)`

Genera el embedding correspondiente al texto procesado utilizando `all-MiniLM-L6-v2`.

El embedding generado se utiliza posteriormente como entrada para el modelo de clasificación.

---

# 📈 Modelo de clasificación

El pipeline utiliza un modelo de **Logistic Regression** previamente entrenado para clasificar el contenido.

Durante el entrenamiento se utilizó un `LabelEncoder` para convertir las categorías originales en etiquetas numéricas que pudieran ser utilizadas por el modelo.

El modelo y el encoder se guardan juntos en un archivo `.pkl`.

Esto permite mantener la correspondencia entre:

    categoría original
            ↕
    etiqueta numérica utilizada por el modelo

Por ejemplo, si durante el entrenamiento una categoría fue transformada a un valor numérico, el mismo `LabelEncoder` debe utilizarse posteriormente para interpretar correctamente las predicciones.

### `load_model()`

Carga desde Oracle Cloud Infrastructure el archivo `.pkl` que contiene:

- El modelo de clasificación entrenado.
- El `LabelEncoder`.

La función devuelve ambos elementos para utilizarlos durante la predicción.

### `predict_category(embedding, best_model, encoder)`

Utiliza el modelo de clasificación para obtener las probabilidades correspondientes a las categorías disponibles.

A partir de estas probabilidades se identifican las dos categorías con mayor probabilidad:

- **Categoría principal:** categoría con la probabilidad más alta.
- **Categoría secundaria:** categoría con la segunda probabilidad más alta.

Además de las categorías, se obtiene la confianza asociada a cada una.

La función devuelve:

    category
    confidence
    second_category
    second_confidence

Por ejemplo:

    category = "Frontend"
    confidence = 0.52

    second_category = "Backend"
    second_confidence = 0.44

---

# 🎯 Categoría principal y categoría secundaria

El modelo siempre obtiene las dos categorías con mayor probabilidad para el contenido.

La categoría principal corresponde a la predicción con mayor probabilidad.

La categoría secundaria corresponde a la segunda predicción con mayor probabilidad.

La confianza representa la probabilidad asignada por el modelo a cada categoría.

Por ejemplo:

    Frontend = 0.52
    Backend  = 0.44

En este caso, `Frontend` es la categoría principal porque tiene la mayor confianza, mientras que `Backend` es la categoría secundaria.

La segunda categoría no significa necesariamente que el contenido pertenezca por igual a ambas categorías. Se conserva principalmente para determinar si existe suficiente similitud entre las dos predicciones como para considerar ambas durante la generación de recomendaciones.

---

# 🎯 Selección de categorías para recomendaciones

La categoría principal y la secundaria no siempre se utilizan ambas para generar recomendaciones.

El algoritmo compara sus niveles de confianza mediante un `threshold`.

La diferencia se calcula como:

    difference = confidence - second_confidence

Si la diferencia es menor o igual al `threshold`, se consideran ambas categorías para las recomendaciones.

Si la diferencia es mayor al `threshold`, únicamente se utiliza la categoría principal.

Por ejemplo:

    Frontend = 0.52
    Backend  = 0.44

    difference = 0.08

Si el `threshold` es `0.1`:

    0.08 <= 0.1

Por lo tanto:

    categories = ["Frontend", "Backend"]

En cambio:

    Frontend = 0.90
    Backend  = 0.07

    difference = 0.83

Entonces:

    0.83 > 0.1

Por lo tanto:

    categories = ["Frontend"]

Este mecanismo permite considerar una segunda categoría cuando las predicciones del modelo son suficientemente cercanas, sin obligar al algoritmo a utilizar siempre dos categorías.

---

# 📚 Sistema de recomendaciones

## `recommendation_functions.py`

### Propósito

Contiene las funciones utilizadas para seleccionar recursos relevantes de la base de conocimiento utilizando las categorías y keywords obtenidas durante el procesamiento.

El algoritmo combina dos fuentes de información:

    Categorías obtenidas del modelo
                    +
            Keywords detectadas
                    |
                    v
          Selección de recomendaciones

---

## `load_knowledge_base()`

Carga la base de conocimiento desde Oracle Cloud Infrastructure Object Storage.

La base de conocimiento contiene los recursos disponibles para recomendar y la información asociada a cada recurso.

La función recibe los datos necesarios para conectarse al bucket de OCI y descarga el archivo JSON para utilizarlo como objeto de Python durante el procesamiento.

---

## `categories_for_recommendation()`

Determina las categorías que serán utilizadas para generar recomendaciones.

La función recibe:

- `confidence`
- `second_confidence`
- `category`
- `second_category`
- `threshold`

Devuelve una lista que puede contener una o dos categorías.

    [category]

o:

    [category, second_category]

La segunda categoría solamente se incluye cuando la diferencia entre ambas confianzas es menor o igual al threshold.

---

## `filter_by_category()`

Filtra la base de conocimiento para conservar únicamente los recursos pertenecientes a las categorías seleccionadas.

Por ejemplo, si las categorías seleccionadas son:

    ["Frontend", "Backend"]

la función conserva únicamente los recursos cuya categoría sea `Frontend` o `Backend`.

---

## `calculate_keyword_matches()`

Compara las keywords detectadas en el contenido del usuario con las keywords asociadas a cada recurso de la base de conocimiento.

Para cada recurso se calcula el número de keywords coincidentes.

Ejemplo:

    Keywords del usuario:
    ["python", "pandas", "docker"]

    Keywords del recurso:
    ["python", "pandas", "numpy"]

    Coincidencias:
    ["python", "pandas"]

    match_count = 2

Cada recurso conserva:

- El recurso original.
- Las keywords coincidentes.
- El número de coincidencias.

---

## `rank_by_matches()`

Elimina los recursos que no tienen ninguna coincidencia y ordena los recursos restantes de acuerdo con el número de keywords coincidentes, de mayor a menor.

Los recursos con mayor cantidad de keywords coincidentes aparecen primero.

---

## `select_recommendations()`

Selecciona las recomendaciones finales de acuerdo con las categorías detectadas.

### Una sola categoría

Si solamente se seleccionó una categoría:

- Se toman hasta 4 recomendaciones.
- Se conservan todas las disponibles si existen menos de 4 coincidencias.

Por ejemplo, si solamente se detectó `Frontend` y existen 3 recursos con coincidencias:

    Recommendation 1
    Recommendation 2
    Recommendation 3

se devuelven los 3 recursos disponibles.

No se agregan recursos sin coincidencias únicamente para completar cuatro posiciones.

### Dos categorías

Si se seleccionaron dos categorías:

- Se toman hasta 3 recomendaciones de la categoría principal.
- Se toma hasta 1 recomendación de la categoría secundaria.

La categoría principal mantiene prioridad, mientras que la categoría secundaria puede aportar una recomendación adicional cuando el modelo considera que también es relevante.

Si alguna categoría no tiene suficientes resultados, se utilizan únicamente los recursos disponibles.

---

## `format_recommendations()`

Da formato a las recomendaciones seleccionadas para construir la estructura que será incluida en el resultado final.

De cada recurso se conservan únicamente los campos necesarios para Backend:

    title
    category
    type
    level
    language
    url

Esto permite que el resultado final contenga únicamente la información necesaria para identificar y utilizar cada recomendación.

---

# ☁️ Despliegue del módulo Data Science en Oracle Cloud Infrastructure

## Descripción general

El módulo de Data Science de **TechMind** se desplegó en una máquina virtual de **Oracle Cloud Infrastructure (OCI)** con Oracle Linux 8.

Este módulo está desarrollado en Python y utiliza **FastAPI** como framework web y **Uvicorn** como servidor ASGI. Su responsabilidad es recibir información de contenido, procesarla mediante los modelos y funciones del proyecto, extraer palabras clave, clasificar el contenido y devolver recomendaciones.

En el entorno desplegado, FastAPI no se inicia manualmente cada vez que alguien entra por SSH. En su lugar, se configuró como un servicio administrado por **systemd**, de forma similar a cualquier otro servicio del sistema operativo.

El servicio creado se denomina:

```text
techmind-python.service
```

La arquitectura interna es:

```text
Spring Boot
    |
    | HTTP interno
    v
FastAPI
127.0.0.1:8000
    |
    +-- Clasificación
    +-- Extracción de keywords
    +-- Modelo de Machine Learning
    +-- Generación de recomendaciones
```

FastAPI se utiliza como un servicio interno: el cliente final no necesita conectarse directamente a él.

---

## Organización del entorno de producción

Durante las primeras pruebas del proyecto se utilizó el directorio personal del usuario de Oracle Linux:

```text
/home/opc
```

Este directorio es adecuado para tareas como:

- pruebas manuales;
- experimentos;
- clonaciones temporales;
- ejecución de comandos;
- archivos personales del usuario.

Sin embargo, para dejar la aplicación ejecutándose como un servicio permanente se decidió separar el entorno de trabajo del entorno de ejecución.

El código utilizado por los servicios se colocó en:

```text
/opt/techmind
```

y los componentes generados específicamente para ejecutar Python se colocaron en:

```text
/opt/techmind-runtime
```

La estructura resultante es aproximadamente:

```text
/opt
├── techmind
│   ├── backend
│   ├── data-scientist
│   │   ├── api.py
│   │   ├── extractor.py
│   │   ├── model_functions.py
│   │   ├── recommendation_functions.py
│   │   ├── keyword_functions.py
│   │   ├── best_model.pkl
│   │   ├── keyword_catalog.json
│   │   ├── keyword_catalog_v2.json
│   │   ├── knowledge_base.json
│   │   └── ...
│   └── frontend
│
└── techmind-runtime
    ├── venv
    └── hf-cache
```

---

## ¿Por qué se utiliza `/opt`?

En sistemas Linux, `/opt` se utiliza habitualmente para aplicaciones o software adicional instalado en el servidor que no forma parte directamente del sistema operativo.

Por esta razón se eligió:

```text
/opt/techmind
... (854 lines left)
```

---

# 📚 Catálogo de Keywords

El archivo `keyword_catalog.json` contiene la lista de keywords utilizadas durante la extracción.

Ejemplo:

    [
        "python",
        "react",
        "docker",
        "postgresql"
    ]

El catálogo puede actualizarse conforme se agreguen nuevas tecnologías o términos relevantes para la base de conocimiento.

---

## `update_catalog.ipynb`

Este notebook permite actualizar el catálogo de keywords de forma ordenada.

### ¿Qué hace?

- Carga la versión actual de `keyword_catalog.json`.
- Agrega nuevas keywords.
- Elimina duplicados.
- Conserva el orden de las keywords.
- Genera el catálogo actualizado.

### ¿Cómo utilizarlo?

1. Cargar la versión más reciente de `keyword_catalog.json`.
2. Definir las nuevas keywords.
3. Ejecutar el notebook.
4. Reemplazar el catálogo anterior por la nueva versión.

> **Importante:** Las keywords deben proporcionarse como una lista de cadenas (`list[str]`).

---

# 🧪 Testing

El módulo cuenta con notebooks independientes para probar los principales componentes del pipeline.

## `keyword_extractor_testing.ipynb`

Permite probar:

- Procesamiento de texto.
- Carga del catálogo.
- Extracción de keywords.

El notebook importa las funciones de `keyword_functions.py` y permite ejecutar cada paso de forma independiente.

---

## `recommendations_testing.ipynb`

Permite probar el algoritmo de recomendaciones utilizando una copia local de la base de conocimiento.

Durante las pruebas se pueden definir manualmente valores para:

- Keywords.
- Categoría principal.
- Confianza de la categoría principal.
- Categoría secundaria.
- Confianza de la categoría secundaria.

Esto permite probar distintos escenarios del algoritmo antes de integrarlo al pipeline completo.

Entre los escenarios que pueden probarse se encuentran:

- Una sola categoría.
- Dos categorías con confianzas similares.
- Una categoría claramente dominante.
- Diferentes cantidades de keywords.
- Recursos con diferentes cantidades de coincidencias.
- Menos de cuatro recomendaciones disponibles.

Durante el testing, la base de conocimiento puede cargarse localmente para no depender de la conexión con OCI.

---

# 🚀 Uso

## Importar el pipeline

    from extractor import process_data

## Ejecutar el pipeline

    result = process_data(data)

Ejemplo:

    data = {
        "title": "Angular: Controla la navegación",
        "text": "Learn spring boot and python"
    }

    result = process_data(data)

El resultado contiene la categoría principal, su confianza, las keywords detectadas y las recomendaciones generadas.

---

# 🧪 Pruebas locales

`extractor.py` incluye un bloque de prueba local:

    if __name__ == "__main__":

Este bloque se ejecuta únicamente cuando `extractor.py` se ejecuta directamente.

Permite probar el pipeline completo sin afectar otros módulos que importen `process_data()`.

Para ejecutar la prueba:

    python extractor.py

---

# 📦 Dependencias

El módulo utiliza Python 3 y las principales librerías utilizadas en el pipeline son:

    sentence-transformers
    scikit-learn
    pandas
    numpy
    joblib
    oci

El modelo `all-MiniLM-L6-v2` se utiliza para generar embeddings y `scikit-learn` se utiliza para el modelo de clasificación y `LabelEncoder`.

---

# 📏 Lineamientos de código

Para mantener consistencia entre los módulos del proyecto se siguen las siguientes convenciones:

- Variables y funciones utilizan `snake_case`.
  - Ejemplo: `extract_keywords()`

- Clases utilizan `PascalCase`.
  - Ejemplo: `KeywordExtractor`

- Los nombres de archivos utilizan `snake_case`.

- El código se escribe en inglés:
  - Nombres de funciones.
  - Variables.
  - Archivos.
  - Docstrings.

- Los comentarios del código se escriben en español para facilitar el trabajo colaborativo del equipo.

- Los archivos JSON utilizan claves en inglés: 
  - `title`
  - `text`
  - `category`
  - `confidence`
  - `keywords`
  - `recommendations`

- Los comentarios deben ser breves y utilizarse únicamente cuando aporten contexto adicional.

---

# 🔄 Pipeline completo

    INPUT
      |
      v
    +-------------------+
    | Text Processing   |
    +-------------------+
      |
      +-----------------------+
      |                       |
      v                       v
    Keyword Extraction    Text Embedding
      |                       |
      |                       v
      |                 Logistic Regression
      |                       |
      |                 +-----+-----+
      |                 |           |
      |                 v           v
      |            Category 1   Category 2
      |            + Confidence + Confidence
      |                 \           /
      |                  \         /
      +-------------------+-------+
                          |
                          v
                Category Selection
                          |
                          v
                  Knowledge Base
                          |
                          v
                Keyword Match Scoring
                          |
                          v
                       Ranking
                          |
                          v
                Recommendation Selection
                          |
                          v
                Recommendation Formatting
                          |
                          v
                       OUTPUT

---

# 🎯 Objetivo del módulo

El objetivo del módulo de Data Science es automatizar el análisis y organización del contenido técnico recibido por TechMind, reduciendo la necesidad de clasificación manual y proporcionando recursos relacionados de la base de conocimiento.

El pipeline combina:

    Contenido del usuario
            +
         Keywords
            +
      Machine Learning
            +
      Knowledge Base
            |
            v
      Recomendaciones

La arquitectura modular permite mantener separados los diferentes procesos de Data Science y facilita su integración con Backend mediante una única función principal:

    process_data()
