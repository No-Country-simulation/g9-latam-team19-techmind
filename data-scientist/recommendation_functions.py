
import json
import oci


# ==========================
# Knowledge base loading
# ==========================


def load_knowledge_base(
    config_file="~/.oci/config",
    profile="DEFAULT",
    namespace="-",
    bucket_name="-",
    object_name="-"
):
    """
    Load the knowledge base JSON file from OCI Object Storage.

    Parameters:
    - config_file: path to the OCI configuration file.
    - profile: OCI configuration profile.
    - namespace: OCI Object Storage namespace.
    - bucket_name: name of the Object Storage bucket.
    - object_name: name/path of the JSON object.

    Returns:
    - knowledge_base: knowledge base as a Python object.
    """

    # Carga las credenciales y configuración de OCI.
    config = oci.config.from_file(
        config_file,
        profile
    )

    # Crea el cliente para acceder a Object Storage.
    object_storage = oci.object_storage.ObjectStorageClient(
        config
    )

    # Descarga el archivo JSON desde Object Storage.
    response = object_storage.get_object(
        namespace_name=namespace,      # Aquí va el namespace de OCI
        bucket_name=bucket_name,       # Aquí va el nombre del bucket
        object_name=object_name        # Aquí va el nombre/ruta del archivo JSON
    )

    # Convierte el contenido descargado de bytes a un objeto JSON de Python.
    knowledge_base = json.loads(
        response.data.content.decode("utf-8")
    )

    return knowledge_base


# ==========================
# Category selection
# ==========================


def categories_for_recommendation(confidence, second_confidence, category, second_category, threshold=0.1):
    """
    Determine which categories should be considered for recommendations
    based on the confidence difference between the two predictions.
    """

    # Calcula la diferencia entre la confianza de la categoría principal
    # y la segunda categoría.
    difference = confidence - second_confidence

    # Si la diferencia es menor o igual al threshold,
    # se consideran ambas categorías para las recomendaciones.
    if difference <= threshold:
        categories = [category, second_category]
    else:
        # Si la diferencia es mayor, solo se considera la categoría principal.
        categories = [category]

    return categories


# ==========================
# Knowledge base filtering
# ==========================


def filter_by_category(knowledge_base, categories):
    """
    Filter the knowledge base to keep only items belonging
    to the selected categories.
    """

    # Conserva únicamente los recursos cuya categoría
    # se encuentre entre las categorías seleccionadas.
    filtered_items = [
        item
        for item in knowledge_base
        if item["category"] in categories
    ]

    return filtered_items


# ==========================
# Keyword matching
# ==========================


def calculate_keyword_matches(filtered_items, keywords):
    """
    Calculate the keyword matches between the user's keywords
    and the keywords associated with each knowledge base item.
    """

    results_matches = []

    for item in filtered_items:

        # Convierte el string de keywords de la base de conocimientos
        # en una lista para poder compararlo con las keywords del usuario.
        item_keywords = item["keywords"].split(",")

        # Compara las keywords del usuario con las keywords del recurso
        # y obtiene únicamente las coincidencias.
        matches = set(keywords) & set(item_keywords)

        results_matches.append({
            "item": item,
            "matches": matches,
            "match_count": len(matches)
        })

    return results_matches


# ==========================
# Result ranking
# ==========================


def rank_by_matches(results_matches):
    """
    Filter out items without keyword matches and rank the remaining
    items by the number of matching keywords.
    """

    # Elimina los recursos que no tienen ninguna keyword coincidente.
    ranked_results = [
        result
        for result in results_matches
        if result["match_count"] > 0
    ]

    # Ordena los recursos de mayor a menor número de coincidencias.
    return sorted(
        ranked_results,
        key=lambda x: x["match_count"],
        reverse=True
    )


# ==========================
# Recommendation selection
# ==========================


def select_recommendations(ranked_results, categories, max_recommendations=4):
    """
    Select the final recommendations based on the detected categories.

    If there is only one category, return up to 4 recommendations.

    If there are two categories:
    - Return up to 3 recommendations from the primary category.
    - Return up to 1 recommendation from the secondary category.

    Recommendations with no keyword matches have already been
    removed during the ranking step.
    """

    # ---------------------------------------------------------
    # CASO 1: Solo se detectó una categoría
    # ---------------------------------------------------------
    if len(categories) == 1:

        # ranked_results ya está ordenado de mayor a menor
        # número de coincidencias.
        #
        # [:4] toma los primeros 4 resultados.
        # Si hay menos de 4 resultados, Python devuelve todos los disponibles.
        recommendations = ranked_results[:max_recommendations]

    # ---------------------------------------------------------
    # CASO 2: Se detectaron dos categorías
    # ---------------------------------------------------------
    else:

        # categories[0] corresponde a la categoría principal
        # y categories[1] corresponde a la segunda categoría.
        #
        # Separamos los resultados para garantizar que la segunda
        # categoría también pueda aportar una recomendación.
        primary_results = [
            result
            for result in ranked_results
            if result["item"]["category"] == categories[0]
        ]

        secondary_results = [
            result
            for result in ranked_results
            if result["item"]["category"] == categories[1]
        ]

        # Toma hasta 3 de los mejores resultados de la categoría principal.
        #
        # Los resultados ya están ordenados por número de coincidencias,
        # por lo que los primeros 3 son los que tienen más coincidencias.
        primary_recommendations = primary_results[:3]

        # Toma hasta 1 de los mejores resultados de la segunda categoría.
        #
        # Si no existen resultados para esta categoría,
        # se obtiene una lista vacía.
        secondary_recommendations = secondary_results[:1]

        # Combina las recomendaciones de ambas categorías.
        recommendations = primary_recommendations + secondary_recommendations

    return recommendations


# ==========================
# Recommendation formatting
# ==========================


def format_recommendations(recommendations):
    """
    Format the selected recommendations by keeping only
    the information that will be included in the final output.
    """

    formatted_recommendations = []

    for recommendation in recommendations:
        # Obtiene la información original del recurso recomendado.
        item = recommendation["item"]

        # Conserva únicamente los campos que se mostrarán
        # en las recomendaciones finales.
        formatted_recommendations.append({
            "title": item["title"],
            "category": item["category"],
            "type": item["type"],
            "level": item["level"],
            "language": item["language"],
            "url": item["url"]
        })

    return formatted_recommendations
