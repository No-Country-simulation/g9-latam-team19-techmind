# Importa FastAPI para crear la API y definir endpoints
# Importa BaseModel para crear modelos de datos y validar entradas
from fastapi import FastAPI
from pydantic import BaseModel
from extractor import process_data

# Crea la aplicación FastAPI
app = FastAPI()


# Modelo de entrada esperado por la API con estructura JSON que recibirá el endpoint
class Document(BaseModel):
    title: str  
    text: str  


# Define un endpoint POST  /extract-keywords
# Recibe un documento y devuelve las palabras clave encontradas
@app.post("/extract-keywords")
def extract_keywords(document: Document):

    # Convierte el objeto Document en un diccionario
    # y lo envía al pipeline de extracción
    result = process_data(document.model_dump())
    return result


# Ejecutar la API desde la terminal:
# python -m uvicorn api:app --reload

# Documentación automática de FastAPI:
# http://127.0.0.1:8000/docs
