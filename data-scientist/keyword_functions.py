
import json

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


def load_keyword_catalog(filename: str ="keyword_catalog.json") -> list:
  """
  Import the keyword catalog JSON file that contains a list of keywords from the knowledge base
  """
  with open(filename, "r", encoding="utf-8") as file:
      catalog = json.load(file)

  return catalog


def extract_keywords(cleaned_text: str, catalog: list) -> list:
  """
  Extract keywords from text based on a our predefined knowledge-base catalog.
  """
  keywords = []

  for keyword in catalog:
      if keyword in cleaned_text:
          keywords.append(keyword)

  return keywords


# ==========================
# JSON output
# ==========================


def save_keywords_json(keywords: list, filename: str = "keywords.json") -> None:
    """
    Save extracted keywords into a JSON file.
    """
    output = {
        "keywords": keywords
    }

    with open(filename, "w", encoding="utf-8") as file:
        json.dump(
            output,
            file,
            indent=4,
            ensure_ascii=False
        )
