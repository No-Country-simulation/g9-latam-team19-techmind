import React from 'react';

interface RecomendacionCard {
  id: string;
  title: string;
  category: string;
  type: string;
  language: string;
  url: string;
  confidence: number;
  keywords: string[];
}

interface CardModalProps {
  card: RecomendacionCard | null;
  onClose: () => void;
  onSelectCard: (card: RecomendacionCard) => void;
  recomendaciones: RecomendacionCard[];
  getCatColor: (cat: string) => string;
}

export default function CardModal({ card, onClose, onSelectCard, recomendaciones, getCatColor }: CardModalProps) {
  if (!card) return null;

  // Filtrar elementos relacionados para la sección "Recomendado para ti"
  const getRelatedCards = (currentCard: RecomendacionCard) => {
    return recomendaciones
      .filter(r => r.id !== currentCard.id && (r.category === currentCard.category || r.keywords.some(k => currentCard.keywords.includes(k))))
      .slice(0, 2);
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        
        {/* Header con color de categoría y badges */}
        <div className="modal-header-cover" style={{ background: getCatColor(card.category) }}>
          <button className="modal-close-btn" onClick={onClose}>✕</button>
          <div className="modal-badges">
            <span className="modal-badge">{card.type}</span>
            <span className="modal-badge">{card.language}</span>
          </div>
          <span className="modal-letter">{card.category.charAt(0)}</span>
        </div>

        {/* Cuerpo del modal */}
        <div className="modal-body">
          <div className="modal-cat-tag">{card.category.toUpperCase()}</div>
          <h2 className="modal-title">{card.title}</h2>

          {/* Barra de confianza */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div className="conf-track" style={{ flex: 1, height: '6px' }}>
              <div className="conf-fill" style={{ width: `${card.confidence ? card.confidence * 100 : 85}%`, background: getCatColor(card.category) }}></div>
            </div>
            <span className="conf-num" style={{ fontFamily: 'IBM Plex Mono, monospace', fontSize: '12px' }}>
              {card.confidence ? Math.round(card.confidence * 100) : 85}%
            </span>
          </div>

          <p className="modal-desc">
            Recurso técnico seleccionado y clasificado automáticamente por el sistema de IA para potenciar tus conocimientos en <strong>{card.category}</strong>. Haz clic en el botón inferior para acceder directamente al contenido completo.
          </p>

          {/* Botón principal de redirección */}
          <a 
            href={card.url} 
            target="_blank" 
            rel="noopener noreferrer" 
            className="modal-main-btn"
          >
            Ir al {card.type.toLowerCase()} →
          </a>

          {/* Sección "Recomendado para ti" */}
          {getRelatedCards(card).length > 0 && (
            <div className="modal-recs-section">
              <div className="modal-recs-title">Recomendado para ti</div>
              <div className="modal-recs-grid">
                {getRelatedCards(card).map(rel => (
                  <div 
                    key={rel.id} 
                    className="mini-card"
                    onClick={() => onSelectCard(rel)}
                  >
                    <div className="mini-card-badge" style={{ background: getCatColor(rel.category) }}>
                      {rel.category.charAt(0)}
                    </div>
                    <div className="mini-card-info">
                      <h5>{rel.title}</h5>
                      <p>{rel.type} · {rel.category}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

        </div>
      </div>
    </div>
  );
}