import { useEffect, useState } from 'react';
import { entregasApi, gruposApi, trabajosApi } from '../api/services';
import { ErrorPanel, LoadingPanel } from '../components/AsyncState';
import { useAuthInfo } from '../auth/useAuthInfo';

interface Summary {
  grupos: number;
  trabajosPendientes: number;
  entregasRecientes: number;
}

export function Dashboard() {
  const auth = useAuthInfo();
  const [summary, setSummary] = useState<Summary | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    Promise.all([gruposApi.listar(), trabajosApi.listar(), entregasApi.listar()])
      .then(([grupos, trabajos, entregas]) => {
        if (cancelled) return;
        setSummary({
          grupos: grupos.length,
          trabajosPendientes: trabajos.filter((t) => t.estado === 'PENDIENTE').length,
          entregasRecientes: entregas.length,
        });
      })
      .catch((err) => {
        if (!cancelled) setError(err.message ?? 'No se pudo cargar el resumen.');
      });

    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <>
      <header className="content__header">
        <div className="content__eyebrow">panel principal</div>
        <h1>Hola, {auth?.displayName.split(' ')[0] ?? ''}</h1>
        <p className="content__subtitle">
          Esta es la fotografía actual del taller: grupos activos, trabajos por entregar y
          movimiento reciente de entregas.
        </p>
      </header>

      {error && <ErrorPanel message={error} />}
      {!error && !summary && <LoadingPanel label="Cargando resumen…" />}

      {summary && (
        <div className="summary-grid">
          <div className="summary-card">
            <div className="summary-card__value">{summary.grupos}</div>
            <div className="summary-card__label">Grupos registrados</div>
          </div>
          <div className="summary-card">
            <div className="summary-card__value">{summary.trabajosPendientes}</div>
            <div className="summary-card__label">Trabajos pendientes</div>
          </div>
          <div className="summary-card">
            <div className="summary-card__value">{summary.entregasRecientes}</div>
            <div className="summary-card__label">Entregas registradas</div>
          </div>
        </div>
      )}
    </>
  );
}
