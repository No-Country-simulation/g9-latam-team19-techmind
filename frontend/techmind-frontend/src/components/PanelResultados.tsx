import React from 'react';

interface PanelProps {
  resultado: {
    category: string;
    confidence: number;
    keywords?: string[];
  };
  titulo: string;
  contenido: string;
}

export default function PanelResultados({ resultado, titulo, contenido }: PanelProps) {
  const palabrasClave = resultado.keywords || [];
  const probabilidadPorcentaje = resultado.confidence ? (resultado.confidence * 100).toFixed(0) : 0;

  const jsonVisual = `{
  "category": "${resultado.category}",
  "confidence": ${resultado.confidence},
  "keywords": [
    ${palabrasClave.map((tag: string) => `"${tag}"`).join(',\n    ')}
  ]
}`;

  return (
    <div className="result-grid" style={{ gridColumn: 'span 2' }}>
      
      <div className="card">
        <span className="section-label">Categoría Detectada</span>
        <h2 style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>{resultado.category}</h2>
        <span className="badge">Nivel de Confianza: {probabilidadPorcentaje}%</span>
      </div>

      <div className="card">
        <span className="section-label">Contenido Analizado</span>
        <h3 style={{ marginBottom: '0.5rem' }}>{titulo}</h3>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>{contenido}</p>
      </div>

      <div className="card">
        <span className="section-label">Palabras Clave Extraídas</span>
        <div>
          {palabrasClave.map((tag: string, i: number) => (
            <span key={i} className="tag">{tag}</span>
          ))}
        </div>
      </div>

      <div className="card">
        <span className="section-label">respuesta.json (MVP)</span>
        <pre className="json-box">{jsonVisual}</pre>
      </div>

    </div>
  );
}