import React, { useState } from 'react';
import Header from './components/Header.tsx';
import FormularioAnalisis from './components/FormularioAnalisis.tsx';
import PanelResultados from './components/PanelResultados.tsx';
import './App.css';

interface DatosAnalizados {
  titulo: string;
  contenido: string;
}

export default function App() {
  const [resultado, setResultado] = useState<any>(null);
  const [datosAnalizados, setDatosAnalizados] = useState<DatosAnalizados>({ titulo: '', contenido: '' });

  const reiniciarVista = () => {
    setResultado(null);
    setDatosAnalizados({ titulo: '', contenido: '' });
  };

  return (
    <div className="app-container">
      <div className="max-width-wrapper">
        <Header mostrandoResultados={!!resultado} onReiniciar={reiniciarVista} />
        
        <div className="grid-layout">
          {!resultado ? (
            <FormularioAnalisis 
              onAnalisisCompletado={(res, datos) => {
                setResultado(res);
                setDatosAnalizados(datos);
              }} 
            />
          ) : (
            <PanelResultados 
              resultado={resultado} 
              titulo={datosAnalizados.titulo} 
              contenido={datosAnalizados.contenido} 
            />
          )}
        </div>
      </div>
    </div>
  );
}