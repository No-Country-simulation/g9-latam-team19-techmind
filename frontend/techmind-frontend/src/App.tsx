import { useState, useEffect } from 'react';
import './App.css';

// --------------- Configuración y Constantes ---------------
const CATS: Record<string, { color: string; letter: string; kws: string[] }> = {
  "Backend":  { color: "#1F3A5F", letter:"B", kws: ["api","rest","spring","java","node","express","backend","servidor","microservicio","endpoint","controlador","autenticación"] },
  "Frontend": { color: "#7A2E35", letter:"F", kws: ["react","css","html","interfaz","componente","ui","frontend","javascript","vue","angular","diseño","responsive","typescript"] },
  "Data Science": { color: "#3E6B63", letter:"D", kws: ["datos","modelo","pandas","tfidf","clasificación","machine learning","dataset","entrenamiento","python","análisis","scikit"] },
  "DevOps":   { color: "#5B4B77", letter:"O", kws: ["docker","kubernetes","ci/cd","pipeline","despliegue","contenedor","infraestructura","devops","automatización"] },
  "Base de Datos": { color: "#B98A2A", letter:"BD", kws: ["sql","mysql","postgresql","mongodb","oracle","database","base de datos","query","esquema","modelado","dax"] }
};

const getDisplayMatch = (confidence: number): number => {
  let adjusted = confidence;

  if (confidence > 0.5) {
    adjusted += 0.4;
  } else if (confidence > 0.4) {
    adjusted += 0.35;
  } else if (confidence > 0.3) {
    adjusted += 0.2;
  }

  return Math.min(adjusted, 1);
};

const VISIBLE_CATS = Object.keys(CATS).filter(
  cat => cat !== "Data Science"
);

const TYPES = ["Curso","Artículo","Tutorial","Documentación"];
const ACTION_LABEL: Record<string, string> = {
  "Curso":"Ir al curso →",
  "Artículo":"Leer el artículo →",
  "Tutorial":"Ver el tutorial →",
  "Documentación":"Abrir documentación →"
};

const obtenerPlural = (tipo: string) => {
  if (tipo === "Curso") return "Cursos";
  if (tipo === "Artículo") return "Artículos";
  if (tipo === "Tutorial") return "Tutoriales";
  if (tipo === "Documentación") return "Documentaciones";
  return tipo;
};

const seedCards = [
  { id: "s1", title:"Introducción a Spring Boot (Contenido)", type:"Contenido", cat:"Backend", lang:"ES", conf:0.89, kw:["Java","Spring Boot","API REST"], desc:"Texto guardado.", url:"", isRoot: true },
  { id: "r1", parentId: "s1", title:"Spring Boot Oficial", type:"Documentación", cat:"Backend", lang:"EN", conf:0.89, kw:["Java"], desc:"Doc oficial", url:"https://spring.io/", isRoot: false }
];

export default function App() {
  
  // --------------- Estados de la Aplicación ---------------
  const [cards, setCards] = useState<any[]>(seedCards);
  const [currentView, setCurrentView] = useState<'dashboard' | 'recommend' | 'analyzer'>('dashboard');
  
  const [dashCatFilter, setDashCatFilter] = useState('Todas');
  const [dashSearch, setDashSearch] = useState('');

  const [recoCatFilter, setRecoCatFilter] = useState('Todas');
  const [recoLangFilter, setRecoLangFilter] = useState('Todos');
  const [recoSearch, setRecoSearch] = useState('');
  
  const [darkMode, setDarkMode] = useState(false);
  
  const [inTitle, setInTitle] = useState('');
  const [inText, setInText] = useState('');
  const [lastResult, setLastResult] = useState<any>(null);
  const [showResult, setShowResult] = useState(false);
  
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const [selectedCardId, setSelectedCardId] = useState<string | null>(null);
  const [modalMode, setModalMode] = useState<'dashboard' | 'reco' | 'nested'>('dashboard');
  const [dashboardRootId, setDashboardRootId] = useState<string | null>(null);

  // --------------- Efectos y Construcción de Datos ---------------
  useEffect(() => {
    if (darkMode) {
      document.body.classList.add('dark-mode');
    } else {
      document.body.classList.remove('dark-mode');
    }
  }, [darkMode]);

  useEffect(() => {
    fetch('/api/contenido')
      .then(res => {
        if (!res.ok) throw new Error("Error en la respuesta del servidor");
        return res.json();
      })
      .then(data => {
        const listaContenidos = Array.isArray(data) ? data : (data.content || []);
        if (listaContenidos.length === 0) return;

        const allCards: any[] = [];
        
        listaContenidos.forEach((c: any) => {
          const rawCatPadre = c.category || 'Backend';
          const catPadre = CATS[rawCatPadre] ? rawCatPadre : 'Base de Datos';
          
          const confPadre = c.confidence || 0.85;
          const kwsPadre = c.keywords || [];
          const listaRecs = c.recommendations || c.recomendaciones || [];

          allCards.push({
            id: `root-${c.id}`,
            title: c.title || 'Contenido analizado',
            type: 'Contenido', 
            cat: catPadre,
            lang: 'ES',
            conf: confPadre,
            kw: kwsPadre,
            desc: c.text || "Fragmento analizado por IA.",
            url: '',
            isRoot: true
          });

          listaRecs.forEach((r: any, i: number) => {
            const rawLang = r.language || r.idioma || 'ES';
            const langDisplay = (rawLang.toUpperCase().includes('EN') || rawLang.toUpperCase().includes('ING')) ? 'EN' : 'ES';
            
            let tipoRaw = (r.type || r.tipo || 'Artículo').toLowerCase();
            let tipo = 'Artículo';
            
            if (tipoRaw.includes('course') || tipoRaw.includes('curso')) {
              tipo = 'Curso';
            } else if (tipoRaw.includes('tutorial')) {
              tipo = 'Tutorial';
            } else if (tipoRaw.includes('doc')) {
              tipo = 'Documentación';
            }

            const rawCatReco = r.categoryRecs || r.category_recs || rawCatPadre;
            const categoriaFinal = CATS[rawCatReco] ? rawCatReco : 'Base de Datos';

            allCards.push({
              id: `db-${c.id}-${r.id || i}`,
              parentId: `root-${c.id}`, 
              title: r.title || c.title,
              type: tipo,
              cat: categoriaFinal,
              lang: langDisplay,
              conf: confPadre,
              kw: kwsPadre,
              desc: c.text || "Recurso técnico recomendado por IA.",
              url: r.url || 'https://github.com',
              isRoot: false
            });
          });
        });

        if (allCards.length > 0) {
          setCards(allCards);
        }
      })
      .catch(err => {
        console.warn("Usando datos locales temporales, no se conectó al backend:", err);
      });
  }, []);

  // --------------- Navegación y Filtros ---------------
  const handleTabChange = (view: 'dashboard' | 'analyzer' | 'recommend') => {
    if (currentView === 'analyzer' && view !== 'analyzer') {
      setInTitle('');
      setInText('');
      setLastResult(null);
      setShowResult(false);
    }
    setCurrentView(view);
  };

  const catColor = (cat: string) => CATS[cat] ? CATS[cat].color : "#999";
  const catLetter = (cat: string) => CATS[cat] ? CATS[cat].letter : "?";

  const dashFiltered = cards.filter(c => {
    if (!c.isRoot || c.cat === "Data Science") return false;
    const matchesCat = dashCatFilter === "Todas" || c.cat === dashCatFilter;
    const q = dashSearch.trim().toLowerCase();
    const matchesQ = !q || c.title.toLowerCase().includes(q) || (c.kw && c.kw.some((k: string) => k.toLowerCase().includes(q)));
    return matchesCat && matchesQ;
  }).sort(
  (a: { id: string | number }, b: { id: string | number }) =>
    Number(b.id) - Number(a.id)
);

  const recoFiltered = cards.filter(c => {
    if (c.isRoot || c.cat === "Data Science") return false;
    const matchesCat = recoCatFilter === "Todas" || c.cat === recoCatFilter;
    const matchesLang = recoLangFilter === "Todos" || c.lang === recoLangFilter;
    const q = recoSearch.trim().toLowerCase();
    const matchesQ = !q || c.title.toLowerCase().includes(q) || (c.kw && c.kw.some((k: string) => k.toLowerCase().includes(q)));
    return matchesCat && matchesLang && matchesQ;
  });

  // --------------- Conexión al Backend ---------------
  const handleAnalyzeAndAdd = async () => {
    try {
      const response = await fetch('/api/contenido/procesar', { 
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          title: inTitle,
          text: inText
        })
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        let mensajeBackend = "Error desconocido del servidor";

        if (errorData) {
          if (errorData.error) {
            mensajeBackend = errorData.error;
            if (errorData.detalle) {
              mensajeBackend += `\nDetalle: ${errorData.detalle}`;
            }
          } else {
            const erroresCampos = Object.values(errorData).join('\n• ');
            mensajeBackend = `Errores de validación:\n• ${erroresCampos}`;
          }
        } else {
          mensajeBackend = `Error del servidor (Status: ${response.status})`;
        }
        throw new Error(mensajeBackend);
      }

      const data = await response.json();
      
      let resCat = data.category || "Backend";
      if (!CATS[resCat]) resCat = "Base de Datos";

      const r = { 
        title: inTitle, 
        text: inText, 
        cat: resCat, 
        conf: data.confidence || 0.85, 
        kw: data.keywords || [] 
      };
      
      setLastResult(r);
      setShowResult(true);

      const newCard = {
        id: "c" + Date.now(),
        title: inTitle,
        type: "Contenido", 
        cat: resCat,
        lang: "ES",
        conf: data.confidence || 0.85,
        kw: data.keywords || [],
        desc: inText.slice(0, 160) + (inText.length > 160 ? "…" : ""),
        url: "",
        isRoot: true 
      };

      setCards(prev => [newCard, ...prev]);
      setDashCatFilter("Todas");

      setSuccessMessage("El contenido ha sido analizado y guardado exitosamente en tu catálogo.");
      setTimeout(() => setSuccessMessage(null), 6000);

    } catch (error: any) {
      console.error("Excepción capturada del backend:", error);
      setErrorMessage(error.message);
      setTimeout(() => setErrorMessage(null), 6000);
    }
  };

  // --------------- Controladores del Modal ---------------
  const openDashboardDetail = (id: string) => {
    setSelectedCardId(id);
    setDashboardRootId(id);
    setModalMode('dashboard');
  };

  const openRecoDetail = (id: string) => {
    setSelectedCardId(id);
    setModalMode('reco');
  };

  const openNestedDetail = (id: string) => {
    setSelectedCardId(id);
    setModalMode('nested');
  };

  const handleModalBack = () => {
    setSelectedCardId(dashboardRootId);
    setModalMode('dashboard');
  };

  const activeModalCard = cards.find(x => x.id === selectedCardId);
  
  const relatedCardsList = activeModalCard 
    ? cards.filter(o => !o.isRoot && o.cat !== "Data Science" && o.id !== activeModalCard.id && (o.parentId === activeModalCard.id || o.cat === activeModalCard.cat || o.kw.some((k: string) => activeModalCard.kw.includes(k)))).slice(0, 4)
    : [];

  const isRecommendationView = modalMode === 'reco' || modalMode === 'nested';

  // --------------- Renderizado de Interfaz ---------------
  return (
    <div>
      <div className="banner">
        <svg viewBox="0 0 1200 190" preserveAspectRatio="none" xmlns="http://www.w3.org/2000/svg">
          <g stroke="#FCFBF6" strokeWidth="1">
            <line x1="60" y1="150" x2="220" y2="60"/>
            <line x1="220" y1="60" x2="380" y2="110"/>
            <line x1="380" y1="110" x2="560" y2="40"/>
            <line x1="560" y1="40" x2="720" y2="90"/>
            <line x1="720" y1="90" x2="880" y2="30"/>
            <line x1="880" y1="30" x2="1040" y2="100"/>
            <line x1="1040" y1="100" x2="1160" y2="55"/>
            <line x1="220" y1="60" x2="300" y2="150"/>
            <line x1="560" y1="40" x2="640" y2="150"/>
            <line x1="880" y1="30" x2="960" y2="150"/>
          </g>
          <g fill="#B98A2A">
            <circle cx="60" cy="150" r="4"/><circle cx="220" cy="60" r="4"/><circle cx="380" cy="110" r="4"/>
            <circle cx="560" cy="40" r="4"/><circle cx="720" cy="90" r="4"/><circle cx="880" cy="30" r="4"/>
            <circle cx="1040" cy="100" r="4"/><circle cx="1160" cy="55" r="4"/><circle cx="300" cy="150" r="4"/>
            <circle cx="640" cy="150" r="4"/><circle cx="960" cy="150" r="4"/>
          </g>
        </svg>
        <div className="banner-content">
          <div className="banner-eyebrow">Hackathon ONE · G9-LATAM-Team 19 · Alura + Oracle</div>
          <h1 className="banner-title">TECHMIND ENGINE</h1>
          <p className="banner-sub">
            Descubre, organiza y domina los mejores recursos en desarrollo de software y tecnología. Tu evolución técnica comienza aquí.
          </p>
        </div>
      </div>

      <div className="menubar">
        <div className="menubar-inner">
          <div className="tabs">
            <button className={`tab-btn ${currentView === 'dashboard' ? 'active' : ''}`} onClick={() => handleTabChange('dashboard')}>Dashboard</button>
            <button className={`tab-btn ${currentView === 'analyzer' ? 'active' : ''}`} onClick={() => handleTabChange('analyzer')}>Analizador</button>
            <button className={`tab-btn ${currentView === 'recommend' ? 'active' : ''}`} onClick={() => handleTabChange('recommend')}>Recomendaciones</button>
          </div>
          <div style={{ display: 'flex', alignItems: 'center' }}>
            
            {currentView === 'recommend' && (
              <div className="lang-group">
                <span className="lang-label">Idioma</span>
                {["Todos", "ES", "EN"].map(lang => (
                  <button key={lang} className={`chip ${recoLangFilter === lang ? 'active' : ''}`} onClick={() => setRecoLangFilter(lang)}>{lang}</button>
                ))}
              </div>
            )}
            
            <button 
              className="theme-toggle-btn"
              onClick={() => setDarkMode(!darkMode)}
              title={darkMode ? "Modo claro" : "Modo oscuro"}
            >
              {darkMode ? (
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <circle cx="12" cy="12" r="5"></circle>
                  <line x1="12" y1="1" x2="12" y2="3"></line>
                  <line x1="12" y1="21" x2="12" y2="23"></line>
                  <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line>
                  <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line>
                  <line x1="1" y1="12" x2="3" y2="12"></line>
                  <line x1="21" y1="12" x2="23" y2="12"></line>
                  <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"></line>
                  <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"></line>
                </svg>
              ) : (
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>
                </svg>
              )}
            </button>
          </div>
        </div>
      </div>

      {currentView === 'dashboard' && (
        <div data-view="dashboard" className="active">
          <div className="wrap dash-controls">
            <div className="controls-row">
              <div className="search-box">
                <span className="mono" style={{ color: 'var(--ink-faint)', fontSize: '13px' }}>⌕</span>
                <input 
                  type="text" 
                  value={dashSearch} 
                  onChange={(e) => setDashSearch(e.target.value)} 
                  placeholder="Buscar por título o palabra clave..." 
                />
              </div>
              <div className="filter-chips">
                {["Todas", ...VISIBLE_CATS].map(c => (
                  <button key={c} className={`chip ${dashCatFilter === c ? 'active' : ''}`} onClick={() => setDashCatFilter(c)}>{c}</button>
                ))}
              </div>
            </div>
          </div>

          <div className="wrap content-grid">
            {dashFiltered.length > 0 ? (
              dashFiltered.map(c => (
                <div className="scard" key={c.id} onClick={() => openDashboardDetail(c.id)}>
                  <div className="scard-cover" style={{ background: catColor(c.cat) }}>
                    <span />
                    <span className="scover-letter">{catLetter(c.cat)}</span>
                  </div>
                  <div className="scard-body">
                    <div className="scard-cat">{c.cat}</div>
                    <h4>{c.title}</h4>
                    <div className="scard-meta">
                      <span>{Math.round(getDisplayMatch(c.conf) * 100)}% match</span>
                      <span className="arrow">→</span>
                    </div>
                  </div>
                </div>
              ))
            ) : (
              <div className="grid-empty">Sin resultados — prueba con otro término o categoría.</div>
            )}
          </div>
        </div>
      )}

      {currentView === 'analyzer' && (
        <div data-view="analyzer" className="active">
          <div className="wrap analyzer-wrap">
            <div className="an-head">
              <h2>Analizador de contenido</h2>
              <p>Ingresa un fragmento técnico. El modelo estima su categoría, la probabilidad asociada y las palabras clave relevantes.</p>
            </div>

            <div className="scanner-layout">
              <div className="scanner">
                <div className="scanner-label"><span>Ficha de entrada</span></div>

                <input className="scanner-input-title" type="text" value={inTitle} onChange={(e) => setInTitle(e.target.value)} placeholder="Título del contenido" />
                <textarea value={inText} onChange={(e) => setInText(e.target.value)} placeholder="Pega aquí un fragmento de documentación, artículo o apunte..." />
                
                <div className="scanner-actions">
                  <button className="btn btn-primary" onClick={handleAnalyzeAndAdd}>Analizar y Añadir al Catálogo</button>
                </div>

                {showResult && lastResult && (
                  <div className="result show">
                    <div className="result-row">
                      <span className="cat-badge" style={{ background: catColor(lastResult.cat) }}>{lastResult.cat}</span>
                      <div className="conf-track">
                        <div className="conf-fill" style={{ width: `${Math.round(getDisplayMatch(lastResult.conf) * 100)}%` }}></div>
                      </div>
                      <span className="conf-num">{Math.round(getDisplayMatch(lastResult.conf) * 100)}%</span>
                    </div>
                    <div className="kw-row">
                      {lastResult.kw.map((k: string, i: number) => (
                        <span key={i} className="kw-chip">{k}</span>
                      ))}
                    </div>
                    <div className="scanner-actions" style={{ marginTop: '16px' }}>
                      <span className="hint" style={{ color: 'var(--teal)', fontWeight: 600 }}>✓ Guardado en tu Dashboard</span>
                      <button className="btn-ghost" onClick={() => handleTabChange('dashboard')}>Ir al Dashboard</button>
                    </div>
                  </div>
                )}
              </div>

              <div className="why-card">
                <div className="k">Organización Inteligente</div>
                <p>Nuestra IA lee tu contenido y detecta automáticamente a qué área pertenece. El porcentaje indica qué tan segura está de la categoría asignada.</p>
                <div className="k" style={{ marginTop: '16px' }}>Listo para tu catálogo</div>
                <p>Al analizar el texto, el sistema extrae las palabras clave más importantes. Así, tu recurso quedará bien etiquetado y será fácil de encontrar cuando lo necesites.</p>
              </div>
            </div>
          </div>
        </div>
      )}

      {currentView === 'recommend' && (
        <div data-view="recommend" className="active">
          <div className="wrap dash-controls">
            <div className="controls-row">
              <div className="search-box">
                <span className="mono" style={{ color: 'var(--ink-faint)', fontSize: '13px' }}>⌕</span>
                <input 
                  type="text" 
                  value={recoSearch} 
                  onChange={(e) => setRecoSearch(e.target.value)} 
                  placeholder="Buscar por título o palabra clave..." 
                />
              </div>
              <div className="filter-chips">
                {["Todas", ...VISIBLE_CATS].map(c => (
                  <button key={c} className={`chip ${recoCatFilter === c ? 'active' : ''}`} onClick={() => setRecoCatFilter(c)}>{c}</button>
                ))}
              </div>
            </div>
          </div>

          <div className="wrap shelves">
            {TYPES.map(type => {
              const items = recoFiltered.filter(c => c.type === type);
              return (
                <div className="shelf" key={type}>
                  <div className="shelf-head">
                    <h3>{obtenerPlural(type)}</h3>
                    <span className="shelf-count">{items.length} resultado(s)</span>
                  </div>
                  <div className="shelf-track">
                    {items.length > 0 ? (
                      items.map(c => (
                        <div className="scard" key={c.id} onClick={() => openRecoDetail(c.id)}>
                          <div className="scard-cover" style={{ background: catColor(c.cat) }}>
                            <span className="scover-lang">{c.lang}</span>
                            <span className="scover-letter">{catLetter(c.cat)}</span>
                          </div>
                          <div className="scard-body">
                            <div className="scard-cat">{c.cat} · {c.type}</div>
                            <h4>{c.title}</h4>
                            <div className="scard-meta">
                              <span>{Math.round(getDisplayMatch(c.conf) * 100)}% match</span>
                              <span className="arrow">→</span>
                            </div>
                          </div>
                        </div>
                      ))
                    ) : (
                      <div className="shelf-empty">Sin resultados en esta sección.</div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      <footer>
        <div className="foot-row">
          <p>G9-LATAM-Team 19</p>
          <p>Agosto 2026</p>
        </div>
      </footer>

      {activeModalCard && (
        <div className="overlay open" onClick={() => setSelectedCardId(null)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-cover" style={{ background: catColor(activeModalCard.cat) }}>
              
              <div className="modal-top-row">
                {modalMode === 'nested' ? (
                  <button className="modal-icon-btn" onClick={handleModalBack}>←</button>
                ) : (
                  <span />
                )}
                <button className="modal-icon-btn" onClick={() => setSelectedCardId(null)}>✕</button>
              </div>
              
              <div className="modal-cover-bottom">
                
                {isRecommendationView && (
                  <div className="modal-cover-tags">
                    <span className="modal-tag">{activeModalCard.type}</span>
                    <span className="modal-tag">{activeModalCard.lang}</span>
                  </div>
                )}
                
                <span className="modal-cover-letter">{catLetter(activeModalCard.cat)}</span>
              </div>
            </div>
            
            <div className="modal-body">
              <div className="mono" style={{ fontSize: '11px', letterSpacing: '0.06em', textTransform: 'uppercase', color: 'var(--ink-faint)', marginBottom: '8px' }}>
                {activeModalCard.cat}
              </div>
              <h2>{activeModalCard.title}</h2>
              <div className="modal-meta">
                <div className="conf-track">
                  <div className="conf-fill" style={{ width: `${Math.round(getDisplayMatch(activeModalCard.conf) * 100)}%`, background: catColor(activeModalCard.cat) }}></div>
                </div>
                <span className="conf-num">{Math.round(getDisplayMatch(activeModalCard.conf) * 100)}%</span>
              </div>
              <p className="modal-desc">{activeModalCard.desc}</p>
              <div className="modal-kw">
                {activeModalCard.kw && activeModalCard.kw.map((k: string, i: number) => (
                  <span key={i} className="kw-chip">{k}</span>
                ))}
              </div>
              
              {isRecommendationView && (
                <a href={activeModalCard.url} target="_blank" rel="noopener noreferrer" className="modal-cta">
                  {ACTION_LABEL[activeModalCard.type] || "Ir al contenido →"}
                </a>
              )}

              {modalMode === 'dashboard' && (
                <div className="modal-rec">
                  <div className="modal-rec-head">Recomendado para ti</div>
                  <div className="rec-grid">
                    {relatedCardsList.length > 0 ? (
                      relatedCardsList.map(r => (
                        <div className="rec-card" key={r.id} onClick={() => openNestedDetail(r.id)}>
                          <div className="rec-letter" style={{ background: catColor(r.cat) }}>{catLetter(r.cat)}</div>
                          <div className="rec-info">
                            <div className="t">{r.title}</div>
                            <div className="c">{r.type} · {r.cat}</div>
                          </div>
                        </div>
                      ))
                    ) : (
                      <div className="rec-empty">Sin recomendaciones relacionadas por ahora.</div>
                    )}
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {errorMessage && (
        <div className="toast-error">
          <svg className="toast-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
            <line x1="12" y1="9" x2="12" y2="13"></line>
            <line x1="12" y1="17" x2="12.01" y2="17"></line>
          </svg>
          <div className="toast-content">
            <div className="toast-title">Falla en el análisis</div>
            <div className="toast-desc">{errorMessage}</div>
          </div>
          <button className="toast-close" onClick={() => setErrorMessage(null)}>✕</button>
        </div>
      )}

      {successMessage && (
        <div className="toast-success">
          <svg className="toast-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
            <polyline points="22 4 12 14.01 9 11.01"></polyline>
          </svg>
          <div className="toast-content">
            <div className="toast-title">¡Éxito!</div>
            <div className="toast-desc">{successMessage}</div>
          </div>
          <button className="toast-close" onClick={() => setSuccessMessage(null)}>✕</button>
        </div>
      )}
    </div>
  );
}