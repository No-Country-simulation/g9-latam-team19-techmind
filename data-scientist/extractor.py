
# Import helper functions from keyword_functions.py
from keyword_functions import (
    extract_text,
    clean_text,
    load_keyword_catalog,
    extract_canonical_keywords,
    extract_fallback_keywords
)


# ==========================
# Main pipeline
# ==========================


def process_data(data: dict) -> dict:
    """
    Main pipeline for keyword extraction.

    Input:
        data (dict): JSON-like object containing "title" and "text".

    Output:
        dict: Extracted keywords in JSON format.
    """

    raw_text = extract_text(data)

    cleaned_text = clean_text(raw_text)

    catalog = load_keyword_catalog("keyword_catalog_v2.json")

    keywords = extract_canonical_keywords(cleaned_text, catalog)
    if not keywords:
        keywords = extract_fallback_keywords(cleaned_text)
    output = {
        "category" : "prueba",
        "confidence" : 9.5,
        "keywords": keywords
    }

    #save_keywords_json(output)

    return output
    

# ==========================
# Local testing
# ==========================
# This block runs only when this file is executed directly.
# It allows testing the pipeline locally without affecting
# other modules that import process_data().


if __name__ == "__main__":

    data = {
        "title": "Angular: Controla la navegación",
        "text": "Aprende a crear componentes reutilizables con spring boot y python"
    }

    result = process_data(data)

    print(result)
