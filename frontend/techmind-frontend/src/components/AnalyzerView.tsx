import { useState } from 'react';

interface DataScienceResponse {
  category: string;
  confidence: number;
  keywords: string[];
}

export default function AnalyzerView() {
  const [analyzerLang, setAnalyzerLang] = useState('ES');
  const [titulo, setTitulo] = useState('Introducción a Spring Boot');
  const [contenido, setContenido] = useState('En este contenido se presentan los conceptos básicos para la creación de APIs REST utilizando Java y Spring Boot.');
  const [cargando, setCargando] = useState(false);
  const [resultado, setResultado] = useState<DataScienceResponse | null>(null);

  const getCatColor = (cat: string) => {
    switch(cat) {
      case 'Backend': return 'var(--cat-backend)';
      case 'Frontend': return 'var(--cat-frontend)';
      case 'Data Science': return 'var(--cat-data)';
      case 'DevOps': return 'var(--cat-devops)';
      case 'Cloud': return 'var(--cat-cloud)';
      case 'Mobile': return 'var(--cat-mobile)';
      default: return '#999';
    }
  };

  const analizar = async () => {
    if (!titulo || !contenido) return;
    setCargando(true);
    try {
      const respuesta = await fetch('http://localhost:8080/api/contenido/procesar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title: titulo, text: contenido })
      });
      if (respuesta.ok) {
        const r = await respuesta.json();
        setResultado(r);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setCargando(false);
    }
  };

  return (
    <div className="wrap analyzer-wrap">
      <div className="an-head">
        <h2>Analizador de contenido</h2>
        <p>Ingresa un fragmento técnico. El modelo estima su categoría, la probabilidad asociada y las palabras clave relevantes.</p>
      </div>

      <div className="scanner-layout">
        <div className="scanner">
          <div className="scanner-label"><span>Ficha de entrada</span><span>POST /api/contenido/procesar</span></div>

          <div className="lang-toggle">
            <button 
              className={`lang-pill ${analyzerLang === 'ES' ? 'active' : ''}`}
              onClick={() => setAnalyzerLang('ES')}
            >Español</button>
            <button 
              className={`lang-pill ${analyzerLang === 'EN' ? 'active' : ''}`}
              onClick={() => setAnalyzerLang('EN')}
            >English</button>
          </div>

          <input 
            className="scanner-input-title mono" 
            type="text" 
            placeholder="Título del contenido" 
            value={titulo}
            onChange={(e) => setTitulo(e.target.value)}
          />
          <textarea 
            placeholder="Pega aquí un fragmento de documentación, artículo o apunte..."
            value={contenido}
            onChange={(e) => setContenido(e.target.value)}
          />
          
          <div className="scanner-actions">
            <button className="btn btn-primary" onClick={analizar} disabled={cargando}>
              {cargando ? 'Analizando...' : 'Analizar contenido'}
            </button>
            <span className="hint">TF-IDF + similitud de texto</span>
          </div>

          {resultado && (
            <div className="result show">
              <div className="result-row">
                <span className="cat-badge" style={{ background: getCatColor(resultado.category) }}>
                  {resultado.category}
                </span>
                <div className="conf-track">
                  <div className="conf-fill" style={{ width: `${Math.round(resultado.confidence * 100)}%`, background: getCatColor(resultado.category) }}></div>
                </div>
                <span className="conf-num">{Math.round(resultado.confidence * 100)}%</span>
              </div>
              <div className="kw-row">
                {(resultado.keywords || []).map((kw, i) => (
                  <span key={i} className="kw-chip">{kw}</span>
                ))}
              </div>
            </div>
          )}
        </div>

        <div className="why-card">
          <div className="k">Por qué importa</div>
          <p>La probabilidad es <strong>confianza</strong>, no certeza. Un modelo con 89% acierta casi siempre — pero conviene revisar el resto.</p>
          <div className="k" style={{ marginTop: '8px' }}>Prueba esto</div>
          <p>Cambia el texto por algo de Docker o de React y observa cómo se mueve la categoría.</p>
        </div>
      </div>
    </div>
  );
}