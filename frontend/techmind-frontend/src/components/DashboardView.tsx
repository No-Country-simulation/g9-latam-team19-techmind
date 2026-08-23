import React, { useState, useEffect } from 'react';
import CardModal from './CardModal';

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

const CATS = ['Todas', 'Backend', 'Frontend', 'Data Science', 'DevOps', 'Cloud', 'Mobile'];
const TYPES = ["Curso", "Artículo", "Tutorial", "Documentación"];

export default function DashboardView({ activeLang }: { activeLang: string }) {
  const [recomendaciones, setRecomendaciones] = useState<RecomendacionCard[]>([]);
  const [activeCatFilter, setActiveCatFilter] = useState('Todas');
  const [busqueda, setBusqueda] = useState('');
  const [cargando, setCargando] = useState(true);
  const [selectedCard, setSelectedCard] = useState<RecomendacionCard | null>(null);

  useEffect(() => {
    const cargarDatos = async () => {
      try {
        const respuesta = await fetch('http://localhost:8080/api/contenido');
        if (!respuesta.ok) throw new Error('Error al conectar con la API');
        
        const datosAPI = await respuesta.json();
        const listaContenidos = Array.isArray(datosAPI) ? datosAPI : (datosAPI.content || []);
        const recsExtraidas: RecomendacionCard[] = [];

        listaContenidos.forEach((c: any) => {
          const pred = c.prediccion || c.prediction || c;
          const catPadre = pred.category || c.category || 'N/A';
          const confPadre = pred.confidence || c.confidence || 0;
          const rawKws = pred.keywords || pred.palabrasClave || c.keywords || [];
          const kwsPadre = rawKws.map((k: any) => typeof k === 'string' ? k : (k.keyword || k.nombre || ''));

          const listaRecs = c.recomendaciones || c.recommendations || pred.recomendaciones || pred.recommendations || [];

          listaRecs.forEach((r: any, i: number) => {
            const rawLang = r.language || r.idioma || 'ES';
            const langDisplay = (rawLang.toUpperCase().includes('EN') || rawLang.toUpperCase().includes('ING')) ? 'EN' : 'ES';
            
            let tipo = r.type || r.tipo || 'Artículo';
            tipo = tipo.charAt(0).toUpperCase() + tipo.slice(1).toLowerCase();
            if (tipo === 'Documentacion') tipo = 'Documentación';

            recsExtraidas.push({
              id: `rec-${c.id || Math.random()}-${i}`,
              title: r.title || 'Recurso recomendado',
              category: r.categoryRecs || r.category_recs || r.category || catPadre,
              type: tipo,
              language: langDisplay,
              url: r.url || '#',
              confidence: confPadre,
              keywords: kwsPadre
            });
          });
        });

        setRecomendaciones(recsExtraidas);
      } catch (err) {
        console.error('Error al cargar datos:', err);
      } finally {
        setCargando(false);
      }
    };
    cargarDatos();
  }, []);

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

  const filtered = recomendaciones.filter(r => {
    const matchesCat = activeCatFilter === 'Todas' || r.category === activeCatFilter;
    const matchesLang = activeLang === 'Todos' || r.language === activeLang;
    const matchesQ = !busqueda || 
      r.title.toLowerCase().includes(busqueda.toLowerCase()) || 
      r.keywords.some(k => k.toLowerCase().includes(busqueda.toLowerCase()));
    
    return matchesCat && matchesLang && matchesQ;
  });

  return (
    <>
      <div className="wrap dash-controls">
        <div className="controls-row">
          <div className="search-box">
            <span className="mono" style={{ color: 'var(--ink-faint)', fontSize: '13px' }}>⌕</span>
            <input 
              type="text" 
              placeholder="Buscar recomendaciones..." 
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
            />
          </div>
          <div className="filter-chips">
            {CATS.map(c => (
              <button 
                key={c} 
                className={`chip ${c === activeCatFilter ? 'active' : ''}`}
                onClick={() => setActiveCatFilter(c)}
              >
                {c}
              </button>
            ))}
          </div>
        </div>
      </div>

      <div className="wrap shelves">
        {cargando ? (
           <p className="shelf-empty">Cargando catálogo...</p>
        ) : recomendaciones.length === 0 ? (
           <div className="shelf-empty">No hay recomendaciones registradas en el servidor.</div>
        ) : (
          TYPES.map(type => {
            const items = filtered.filter(r => r.type === type);

            return (
              <div className="shelf" key={type}>
                <div className="shelf-head">
                  <h3>{type}s{type === "Documentación" ? "" : ""}</h3>
                  <span className="shelf-count">{items.length} resultado(s)</span>
                </div>
                <div className="shelf-track">
                  {items.length === 0 ? (
                    <div className="shelf-empty">Sin recomendaciones en esta sección.</div>
                  ) : (
                    items.map(r => {
                      const catLimpia = r.category || 'N/A';
                      const confPorcentaje = r.confidence ? (r.confidence * 100).toFixed(0) : 0;
                      
                      return (
                        <div 
                          className="scard" 
                          key={r.id}
                          onClick={() => setSelectedCard(r)}
                        >
                          <div className="scard-cover" style={{ background: getCatColor(catLimpia) }}>
                            <span className="scover-lang">{r.language}</span>
                            <span className="scover-letter">{catLimpia.charAt(0)}</span>
                          </div>
                          <div className="scard-body">
                            <div className="scard-cat">{catLimpia}</div>
                            <h4>{r.title}</h4>
                            <div className="scard-meta">
                              <span>{confPorcentaje}% match</span>
                              <span className="arrow">→</span>
                            </div>
                          </div>
                        </div>
                      );
                    })
                  )}
                </div>
              </div>
            );
          })
        )}
      </div>

      {/* Renderizamos el componente del Modal */}
      <CardModal 
        card={selectedCard}
        onClose={() => setSelectedCard(null)}
        onSelectCard={(card) => setSelectedCard(card)}
        recomendaciones={recomendaciones}
        getCatColor={getCatColor}
      />
    </>
  );
}