# 🔎 Keyword Extractor

## 📌 Descripción

Este módulo permite extraer palabras clave relevantes a partir de documentos de la base de conocimiento.

El pipeline recibe un documento con un título y un texto, procesa y normaliza el contenido, compara la información contra un catálogo predefinido de keywords y genera un archivo JSON con las palabras clave detectadas.

El módulo está diseñado para integrarse fácilmente con backend mediante la función principal:

```python
process_data()
```

---

# 🏗️ Arquitectura del proyecto

```text
                         Documento de entrada
                                |
                                |
                                v
                    +-------------------+
                    |   extractor.py    |
                    |                   |
                    |  process_data()   |
                    +-------------------+
                                |
                                |
                                v
                  +-----------------------+
                  | keyword_functions.py  |
                  |                       |
                  | extract_text()        |
                  | clean_text()          |
                  | load_keyword_catalog()|
                  | extract_keywords()    |
                  | save_keywords_json()  |
                  +-----------------------+
                                |
                                |
                                v
                         keywords.json
```

---

# 📂 Estructura del proyecto

```text
keyword_extractor/
│
├── extractor.py
│   └── Pipeline principal
│       └── process_data()
│
├── keyword_functions.py
│   ├── extract_text()
│   ├── clean_text()
│   ├── load_keyword_catalog()
│   ├── extract_keywords()
│   └── save_keywords_json()
│
├── keyword_catalog.json
│   └── Lista de keywords utilizada para la búsqueda
│
├── keywords.json
│   └── Archivo de salida con las keywords detectadas
│
└── keyword_extractor_testing.ipynb
    └── Notebook de desarrollo y pruebas
```

---

# ⚙️ Funcionamiento

El pipeline de extracción de keywords sigue los siguientes pasos:

```text
1. Recibir documento
        |
        v
2. Extraer título + texto
        |
        v
3. Limpiar y normalizar texto
        |
        v
4. Cargar catálogo de keywords
        |
        v
5. Detectar coincidencias
        |
        v
6. Generar salida JSON
```

---

# 📥 Formato de entrada

El pipeline espera un objeto tipo diccionario/JSON que contenga:

```json
{
    "title": "Angular: Controla la navegación",
    "text": "Aprende a crear componentes reutilizables con spring boot y python"
}
```

---

# 📤 Formato de salida

El resultado generado contiene las keywords detectadas:

```json
{
    "keywords": [
        "angular",
        "spring boot",
        "python"
    ]
}
```

---

# 🧩 Módulos

## `extractor.py`

### Propósito

Contiene el pipeline principal que conecta todos los pasos de extracción de keywords.

### Función principal

```python
process_data(data: dict) -> dict
```

### Responsabilidades

* Recibir los datos de entrada.
* Ejecutar el procesamiento del texto.
* Extraer las keywords detectadas.
* Crear el formato final compatible con JSON.
* Guardar el resultado.

---

## `keyword_functions.py`

### Propósito

Contiene las funciones reutilizables para procesamiento de texto y extracción de keywords.

### Funciones

### `extract_text(data: dict) -> str`

Extrae los campos `title` y `text` del documento de entrada y los combina en una sola cadena de texto.

---

### `clean_text(raw_text: str) -> str`

Normaliza el texto mediante:

* Conversión a minúsculas.
* Eliminación de saltos de línea.
* Eliminación de espacios múltiples.

---

### `load_keyword_catalog(filename: str) -> list`

Carga el catálogo de keywords desde:

```
keyword_catalog.json
```

---

### `extract_keywords(cleaned_text: str, catalog: list) -> list`

Busca las keywords presentes en el texto limpio utilizando el catálogo predefinido.

---

### `save_keywords_json(keywords: list, filename: str) -> None`

Genera el archivo JSON de salida con las keywords extraídas.

---

# 📚 Catálogo de Keywords

El archivo:

```
keyword_catalog.json
```

contiene la lista de keywords utilizadas durante la extracción.

Ejemplo:

```json
[
    "python",
    "react",
    "docker",
    "postgresql"
]
```

El catálogo puede actualizarse conforme se agreguen nuevas tecnologías o keywords relevantes para la base de conocimiento.

---

# 🚀 Uso

## Importar el pipeline

```python
from extractor import process_data
```

## Ejecutar extracción

```python
result = process_data(data)
```

Ejemplo:

```python
data = {
    "title": "Angular: Controla la navegación",
    "text": "Learn spring boot and python"
}

result = process_data(data)
```

Salida:

```json
{
    "keywords": [
        "angular",
        "spring boot",
        "python"
    ]
}
```

---

# 🧪 Pruebas locales

`extractor.py` incluye un bloque de prueba local:

```python
if __name__ == "__main__":
```

Este bloque se ejecuta únicamente cuando el archivo se corre directamente.

Permite probar el pipeline localmente sin afectar otros módulos que importen la función `process_data()`.

Ejecutar:

```bash
python extractor.py
```
---

# 📏 Lineamientos de código

Para mantener consistencia entre los módulos del proyecto se siguen las siguientes convenciones:

- Variables y funciones utilizan `snake_case`.
  - Ejemplo: `extract_keywords()`

- Clases utilizan `PascalCase`.
  - Ejemplo: `KeywordExtractor`

- El código se escribe en inglés:
  - Nombres de funciones.
  - Variables.
  - Archivos.
  - Comentarios técnicos.

- Los archivos JSON utilizan claves en inglés:
  - `title`
  - `text`
  - `category`
  - `keywords`

- Los comentarios deben ser breves y utilizarse únicamente cuando aporten contexto adicional.

---

# 🔮 Integración futura con modelo de clasificación

El pipeline actual está preparado para integrar posteriormente un modelo de clasificación.

Ejemplo de salida futura:

```json
{
    "keywords": [
        "python",
        "docker"
    ],
    "category": "data_science",
    "confidence": 0.95
}
```

El modelo puede agregarse después de la extracción de keywords sin modificar la estructura actual de entrada.
