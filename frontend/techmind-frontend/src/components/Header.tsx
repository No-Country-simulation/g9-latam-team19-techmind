import React from 'react';

interface HeaderProps {
  mostrandoResultados: boolean;
  onReiniciar: () => void;
}

export default function Header({ mostrandoResultados, onReiniciar }: HeaderProps) {
  return (
    <header className="app-header">
      {mostrandoResultados ? (
        <button className="btn-example" onClick={onReiniciar}>
          ← Analizar otro contenido
        </button>
      ) : (
        <span className="badge">✧ Organización inteligente</span>
      )}
    </header>
  );
}