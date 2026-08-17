
from sentence_transformers import SentenceTransformer

from keyword_functions import (
    extract_text,
    clean_text,
    load_keyword_catalog,
    extract_canonical_keywords,
    extract_fallback_keywords
)

from model_functions import (
    load_model,
    generate_embedding,
    predict_category
)

from model_functions import (
    load_model,
    generate_embedding,
    predict_category
)

from recommendation_functions import (
    load_knowledge_base,
    categories_for_recommendation,
    filter_by_category,
    calculate_keyword_matches,
    rank_by_matches,
    select_recommendations,
    format_recommendations
)

# ==========================
# Model loading
# ==========================

# Carga el modelo pre-entrenado utilizado para generar embeddings.
embedding_model = SentenceTransformer("all-MiniLM-L6-v2")

# Carga el clasificador y encoder desde OCI Object Storage.
best_model, encoder = load_model(
    config_file="-",    # Aquí va la ruta del archivo de configuración de OCI
    profile="-",        # Aquí va el perfil de OCI
    namespace="-",      # Aquí va el namespace de OCI
    bucket_name="-",    # Aquí va el nombre del bucket
    object_name="-"     # Aquí va el nombre/ruta de best_model.pkl
)

# ==========================
# Keyword extraction
# ==========================


def process_keywords (data: dict) -> tuple:

    """
    Main pipeline for keyword extraction.

    Input:
        data (dict): JSON-like object containing "title" and "text".

    Output:
        list: Keywords extracted from the input text.
    """

    # Extrae el título y el texto y los combina en una sola entrada.
    raw_text = extract_text(data)

    # Normaliza el texto para facilitar la búsqueda de keywords.
    cleaned_text = clean_text(raw_text)

    # Carga el catálogo de keywords predefinido.
    catalog = load_keyword_catalog()

    # Busca las keywords del catálogo que aparecen en el texto.
    keywords = extract_keywords(cleaned_text, catalog)

    return cleaned_text, keywords


# ==========================
# Category classification
# ==========================


def process_model(cleaned_text: str) -> tuple:
    """
    Main pipeline for category classification.

    Input:
        cleaned_text (str): Preprocessed text used for classification.

    Output:
        tuple: Primary category, primary confidence,
        secondary category, and secondary confidence.
    """

    # Genera el embedding a partir del texto limpio.
    embedding = generate_embedding(
        cleaned_text,
        embedding_model
    )

    # Predice las categorías y sus respectivas confianzas.
    category, confidence, second_category, second_confidence = predict_category(
        embedding,
        best_model,
        encoder
    )

    return category, confidence, second_category, second_confidence


# ==========================
# Recommendation generation
# ==========================


def generate_recommendations(keywords, category, confidence, second_category, second_confidence):
    """
    Generate recommendations by combining the predicted categories
    and the keywords extracted from the user's text.
    """

    # Determina si se utilizará únicamente la categoría principal
    # o también la segunda categoría.
    categories = categories_for_recommendation(
        confidence,
        second_confidence,
        category,
        second_category
    )

    # Carga la base de conocimientos desde OCI Object Storage.
    knowledge_base = load_knowledge_base()

    # Filtra los recursos de la base según las categorías seleccionadas.
    filtered_items = filter_by_category(
        knowledge_base,
        categories
    )

    # Calcula cuántas keywords del usuario coinciden con cada recurso.
    results_matches = calculate_keyword_matches(
        filtered_items,
        keywords
    )

    # Elimina recursos sin coincidencias y ordena los restantes
    # de acuerdo con el número de keywords coincidentes.
    ranked_results = rank_by_matches(results_matches)

    # Selecciona la cantidad final de recomendaciones según
    # las categorías detectadas.
    recommendations = select_recommendations(
        ranked_results,
        categories
    )

    # Conserva únicamente la información que se incluirá
    # en el resultado final.
    formatted_recommendations = format_recommendations(
        recommendations
    )

    return formatted_recommendations


# ==========================
# Main pipeline
# ==========================


def process_data(data: dict) -> dict:
    """
    Main pipeline for processing the user's input.

    Input:
        data (dict): JSON-like object containing "title" and "text".

    Output:
        dict: Final JSON containing the detected category, confidence,
        keywords, and recommendations.
    """

    # Ejecuta todo el proceso de extracción de keywords.
    cleaned_text, keywords = process_keywords(data)

    # Ejecuta todo el proceso de predicción de categorías.
    category, confidence, second_category, second_confidence = process_model(
        cleaned_text
    )

    # Ejecuta todo el proceso de generación de recomendaciones.
    formatted_recommendations = generate_recommendations(
        keywords,
        category,
        confidence,
        second_category,
        second_confidence
    )

    # Construye el JSON final que será enviado a Backend.
    output = {
        "category": category,
        "confidence": confidence,
        "keywords": keywords,
        "recommendations": formatted_recommendations
    }

    return output
  

# ==========================
# Local testing
# ==========================


# Este bloque se ejecuta únicamente cuando este archivo se ejecuta directamente.
# Permite probar el pipeline completo de forma local
# sin afectar otros módulos que importen process_data().


if __name__ == "__main__":

    data = {
        "title": "Angular: Controla la navegación",
        "text": "Aprende a crear componentes reutilizables con spring boot y python"
    }

    # Ejecuta el pipeline completo con el documento de prueba.
    result = process_data(data)

    # Muestra el resultado final.
    print(result)
