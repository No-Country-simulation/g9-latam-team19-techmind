
import io
import joblib
import oci


# ==========================
# Model loading
# ==========================


def load_model(
    config_file="-",
    profile="-",
    namespace="-",
    bucket_name="-",
    object_name="-"
):
    """
    Load the trained classification model and encoder from OCI Object Storage.

    Input:
        config_file (str): Path to the OCI configuration file.
        profile (str): OCI configuration profile.
        namespace (str): OCI Object Storage namespace.
        bucket_name (str): Name of the Object Storage bucket.
        object_name (str): Name/path of the model pickle file.

    Output:
        tuple: Trained classification model and label encoder.
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

    # Descarga el archivo .pkl desde Object Storage.
    response = object_storage.get_object(
        namespace_name=namespace,
        bucket_name=bucket_name,
        object_name=object_name
    )

    # Convierte los datos descargados en un archivo temporal en memoria
    # para que joblib pueda leer el contenido del .pkl.
    model_data = joblib.load(
        io.BytesIO(response.data.content)
    )

    # Separa el modelo y el encoder que fueron guardados juntos.
    best_model = model_data["model"]
    encoder = model_data["encoder"]

    return best_model, encoder


# ==========================
# Embedding generation
# ==========================


def generate_embedding(cleaned_text: str, model):
    """
    Generate an embedding from the preprocessed text.

    Input:
        cleaned_text (str): Preprocessed text.
        model: Pre-trained SentenceTransformer model.

    Output:
        numpy.ndarray: Numerical representation of the text.
    """

    # Convierte el texto en un vector numérico.
    embedding = model.encode(
        [cleaned_text],
        convert_to_numpy=True
    )

    return embedding


# ==========================
# Category prediction
# ==========================


def predict_category(embedding, best_model, encoder):
    """
    Predict the primary and secondary categories from a text embedding.

    Input:
        embedding: Numerical representation of the text.
        best_model: Trained classification model.
        encoder: Label encoder used during model training.

    Output:
        tuple: Primary category, primary confidence,
        secondary category, and secondary confidence.
    """

    # Obtiene las probabilidades de cada categoría.
    probability = best_model.predict_proba(embedding)

    # Obtiene los índices de las dos categorías
    # con mayor probabilidad.
    top_2 = probability[0].argsort()[-2:][::-1]

    # Convierte el índice de la categoría principal
    # a su nombre original.
    category = encoder.inverse_transform([top_2[0]])[0]

    # Obtiene la confianza de la categoría principal.
    confidence = float(probability[0][top_2[0]])

    # Convierte el índice de la segunda categoría
    # a su nombre original.
    second_category = encoder.inverse_transform([top_2[1]])[0]

    # Obtiene la confianza de la segunda categoría.
    second_confidence = float(probability[0][top_2[1]])

    return category, confidence, second_category, second_confidence
