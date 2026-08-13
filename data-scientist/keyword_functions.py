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


def load_keyword_catalog(filename: str = "keyword_catalog.json") -> list | dict:
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

def extract_canonical_keywords(
        cleaned_text: str,
        catalog: dict[str, list[str]]
) -> list[str]:
    """
    Extract canonical technical keywords from text.

    Longer and more specific matches have priority over
    shorter matches that overlap with them.
    """

    matches = []

    # 1. Find every possible match
    for canonical_keyword, aliases in catalog.items():

        complete_catalog = [canonical_keyword, *aliases]

        for keyword in complete_catalog:
            pattern = rf"(?<!\w){re.escape(keyword)}(?!\w)"

            for match in re.finditer(pattern, cleaned_text):
                matches.append({
                    "canonical": canonical_keyword,
                    "start": match.start(),
                    "end": match.end()
                })

    # 2. Prioritize longer matches
    matches.sort(
        key=lambda item: item["end"] - item["start"],
        reverse=True
    )

    selected_matches = []

    # 3. Discard matches that overlap with a more specific one
    for match in matches:

        overlaps = any(
            match["start"] < selected["end"]
            and match["end"] > selected["start"]
            for selected in selected_matches
        )

        if not overlaps:
            selected_matches.append(match)

    # 4. Restore the order in which concepts appear in the text
    selected_matches.sort(key=lambda item: item["start"])

    # 5. Return each canonical concept only once
    keywords = []

    for match in selected_matches:
        canonical_keyword = match["canonical"]

        if canonical_keyword not in keywords:
            keywords.append(canonical_keyword)

    return keywords
