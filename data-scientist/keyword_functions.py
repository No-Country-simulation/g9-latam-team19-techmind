import json
import re
from pathlib import Path

# ==========================
# Text preprocessing
# ==========================


def extract_text(data: dict) -> str:
    """
    Extract the title and text fields and combine them into a single input string.
    """

    #extraigo titulo y texto por separado del json
    title = data["title"]
    text = data["text"]

    #uno titulo y texto en una sola entrada
    raw_text = f"{title}. {text}"

    return raw_text


def clean_text(raw_text: str) -> str:
    """
    Normalize the input text by applying basic preprocessing steps.
    """
    # Convertimos a minúsculas
    cleaned_text = raw_text.lower()

    # Reemplazar saltos de línea por espacios
    cleaned_text = cleaned_text.replace("\n", " ")

    # Eliminar espacios múltiples. Deja solo un espacio entre palabras y entre párrafos
    cleaned_text = " ".join(cleaned_text.split())

    return cleaned_text


# ==========================
# Keyword extraction
# ==========================


def load_keyword_catalog(filename: str = "keyword_catalog.json") -> list:
    """
    Import the keyword catalog JSON file that contains a list of keywords from the knowledge base
    The path is resolved relative to this module.
    """

    base_dir = Path(__file__).resolve().parent
    path_del_catalogo = base_dir / filename

    with path_del_catalogo.open("r", encoding="utf-8") as file:
        catalog = json.load(file)
    return catalog


def extract_keywords(cleaned_text: str, catalog: list) -> list:
    """
    Extract keywords from text by matching complete words and phrases
    against the predefined keyword catalog.
    """
    keywords = []

    for keyword in catalog:

        # Busca la keyword como una palabra o frase completa.
        # re.escape() evita problemas con caracteres especiales (ej. "node.js").
        pattern = rf"(?<!\w){re.escape(keyword)}(?!\w)"

        if re.search(pattern, cleaned_text) and keyword not in keywords:
            keywords.append(keyword)

    return keywords
