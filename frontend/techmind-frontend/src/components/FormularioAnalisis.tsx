import React, { useState } from 'react';

interface FormularioProps {
  onAnalisisCompletado: (resultado: any, datos: { titulo: string; contenido: string }) => void;
}

export default function FormularioAnalisis({ onAnalisisCompletado }: FormularioProps) {
  const [titulo, setTitulo] = useState<string>('');
  const [contenido, setContenido] = useState<string>('');
  const [cargando, setCargando] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const cargarEjemplo = () => {
    setTitulo("Introducción a Spring Boot");
    setContenido("En este contenido se presentan los conceptos básicos para la creación de APIs REST utilizando Java y Spring Boot...");
  };

  const procesarContenido = async () => {
    if (!titulo || !contenido) return;
    
    setCargando(true);
    setError(null);
    
    try {
      const respuesta = await fetch('http://localhost:8080/contenido', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          title: titulo,
          text: contenido
        })
      });

      if (!respuesta.ok) {
        throw new Error('Error en la respuesta del servidor');
      }

      const datosAPI = await respuesta.json();
      onAnalisisCompletado(datosAPI, { titulo, contenido });

    } catch (err) {
      console.error(err);
      setError('Hubo un problema al procesar el contenido. Verifica que el backend esté ejecutándose.');
    } finally {
      setCargando(false);
    }
  };

  return (
    <div>
      <h1 className="title-main">Convierte texto técnico en conocimiento estructurado</h1>
      <p className="subtitle">Pega un artículo, documentación o anotación de estudio y TechMind lo clasifica...</p>
      
      <div className="card">
        <label className="section-label">Título</label>
        <input 
          type="text" 
          className="input-field" 
          value={titulo}
          onChange={(e: React.ChangeEvent<HTMLInputElement>) => setTitulo(e.target.value)}
          placeholder="Ej. Introducción a Spring Boot"
        />

        <label className="section-label">Contenido</label>
        <textarea 
          className="textarea-field" 
          rows={6}
          value={contenido}
          onChange={(e: React.ChangeEvent<HTMLTextAreaElement>) => setContenido(e.target.value)}
          placeholder="Pega aquí el texto técnico..."
        ></textarea>

        <p className="section-label">O prueba con un ejemplo:</p>
        <div>
          <button onClick={cargarEjemplo} className="btn-example">Introducción a Spring Boot</button>
        </div>

        {error && <p style={{color: '#ef4444', fontSize: '0.875rem', marginTop: '1rem'}}>{error}</p>}

        <button 
          onClick={procesarContenido} 
          disabled={cargando || !titulo || !contenido}
          className="btn-primary"
          style={{ marginTop: '1rem' }}
        >
          {cargando ? 'Procesando modelo...' : 'Clasificar contenido →'}
        </button>
      </div>
    </div>
  );
}