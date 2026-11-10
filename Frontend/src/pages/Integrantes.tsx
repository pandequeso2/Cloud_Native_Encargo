import { useEffect, useState } from 'react';
import { integrantesApi } from '../api/services';
import type { Integrante } from '../types/domain';
import { EmptyPanel, ErrorPanel, LoadingPanel } from '../components/AsyncState';
import { NuevoIntegranteModal } from '../components/NuevoIntegranteModal';
import { EditarIntegranteModal } from '../components/EditarIntegranteModal';
import { useAuthInfo } from '../auth/useAuthInfo';
import {
  Users,
  Plus,
  Mail,
  Edit2,
  Trash2,
  Search,
  CheckCircle2,
  Clock,
  AlertCircle,
  Hash,
  Shield,
} from 'lucide-react';

export function Integrantes() {
  const auth = useAuthInfo();
  const [integrantes, setIntegrantes] = useState<Integrante[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [modalAbierto, setModalAbierto] = useState(false);
  const [integranteEditando, setIntegranteEditando] = useState<Integrante | null>(null);
  const [busqueda, setBusqueda] = useState('');

  const cargarIntegrantes = () => {
    integrantesApi
      .listar()
      .then(setIntegrantes)
      .catch((err) => setError(err.message ?? 'No se pudieron cargar los integrantes.'));
  };

  useEffect(() => {
    cargarIntegrantes();
  }, []);

  const handleIntegranteCreado = (nuevo: Integrante) => {
    setIntegrantes((prev) => (prev ? [...prev, nuevo] : [nuevo]));
    setModalAbierto(false);
  };

  const handleIntegranteActualizado = (actualizado: Integrante) => {
    setIntegrantes((prev) =>
      prev ? prev.map((item) => (item.idIntegrante === actualizado.idIntegrante ? actualizado : item)) : [actualizado],
    );
    setIntegranteEditando(null);
  };

  const handleIntegranteEliminado = async (idIntegrante: number) => {
    const confirmado = window.confirm('¿Seguro que deseas eliminar a este integrante del taller?');
    if (!confirmado) return;

    try {
      await integrantesApi.eliminar(idIntegrante);
      setIntegrantes((prev) => prev?.filter((item) => item.idIntegrante !== idIntegrante) ?? null);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo eliminar el integrante.');
    }
  };

  // Filtrado de integrantes por texto de búsqueda
  const integrantesFiltrados = (integrantes ?? []).filter((item) => {
    const texto = `${item.nombre} ${item.apellido} ${item.correoElectronico} ${item.idGrupo}`.toLowerCase();
    return texto.includes(busqueda.toLowerCase());
  });

  const getInitials = (nombre: string, apellido: string) => {
    const n = nombre ? nombre.trim().charAt(0) : '';
    const a = apellido ? apellido.trim().charAt(0) : '';
    return (n + a).toUpperCase() || 'IN';
  };

  // Paleta de gradientes para avatares
  const avatarGradients = [
    'linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%)',
    'linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%)',
    'linear-gradient(135deg, #10b981 0%, #047857 100%)',
    'linear-gradient(135deg, #f59e0b 0%, #b45309 100%)',
    'linear-gradient(135deg, #ec4899 0%, #be185d 100%)',
    'linear-gradient(135deg, #06b6d4 0%, #0e7490 100%)',
  ];

  return (
    <div className="page-container">
      {/* Encabezado Principal */}
      <header className="page-header">
        <div className="page-header__info">
          <div className="page-header__eyebrow">Gestión Académica</div>
          <h1 className="page-header__title">Integrantes del Taller</h1>
          <p className="page-header__subtitle">
            Estudiantes registrados en los grupos, datos de contacto y disponibilidad horaria.
          </p>
        </div>

        {auth?.hasRole('Admin') && (
          <div className="page-header__actions">
            <button className="btn-primary" onClick={() => setModalAbierto(true)}>
              <Plus size={16} />
              <span>Nuevo Integrante</span>
            </button>
          </div>
        )}
      </header>

      {/* Barra de Filtro y Búsqueda */}
      <div className="toolbar-section">
        <div className="search-bar">
          <Search size={18} className="search-bar__icon" />
          <input
            type="text"
            className="search-bar__input"
            placeholder="Buscar por nombre, correo o número de grupo…"
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
          />
        </div>
        <div className="toolbar-stats">
          <span className="badge badge--info">
            {integrantesFiltrados.length} {integrantesFiltrados.length === 1 ? 'integrante' : 'integrantes'}
          </span>
        </div>
      </div>

      {error && <ErrorPanel message={error} onRetry={cargarIntegrantes} />}
      {!error && !integrantes && <LoadingPanel label="Cargando integrantes desde el microservicio…" />}

      {integrantes && integrantes.length === 0 && (
        <EmptyPanel
          message="No hay integrantes registrados en el taller."
          subtitle="Comienza agregando integrantes y asignándolos a sus respectivos grupos de trabajo."
          action={
            auth?.hasRole('Admin') ? (
              <button className="btn-primary" onClick={() => setModalAbierto(true)}>
                <Plus size={16} />
                <span>Agregar Primer Integrante</span>
              </button>
            ) : null
          }
        />
      )}

      {/* Grilla de Tarjetas Modernas de Integrantes */}
      {integrantes && integrantes.length > 0 && (
        <div className="grid-cards">
          {integrantesFiltrados.map((integrante, index) => {
            const initials = getInitials(integrante.nombre, integrante.apellido);
            const gradient = avatarGradients[index % avatarGradients.length];

            return (
              <div key={integrante.idIntegrante} className="data-card data-card--hover">
                <div className="data-card__body">
                  <div className="data-card__top">
                    <div className="user-avatar-initials" style={{ background: gradient }}>
                      {initials}
                    </div>
                    <div className="data-card__chips">
                      <span className="badge badge--purple">
                        <Hash size={12} /> Grupo #{integrante.idGrupo}
                      </span>
                    </div>
                  </div>

                  <h3 className="data-card__title">
                    {integrante.nombre} {integrante.apellido}
                  </h3>

                  <div className="data-card__meta-item">
                    <Mail size={14} className="data-card__meta-icon" />
                    <span className="data-card__meta-text" title={integrante.correoElectronico}>
                      {integrante.correoElectronico}
                    </span>
                  </div>

                  <div className="data-card__meta-item">
                    <Clock size={14} className="data-card__meta-icon" />
                    <span className="data-card__meta-text">
                      Disponibilidad: <strong>{integrante.disponibilidad || 'No especificada'}</strong>
                    </span>
                  </div>
                </div>

                {auth?.hasRole('Admin') && (
                  <div className="data-card__footer">
                    <button
                      type="button"
                      className="btn-action btn-action--edit"
                      onClick={() => setIntegranteEditando(integrante)}
                      title="Editar datos del integrante"
                    >
                      <Edit2 size={14} />
                      <span>Editar</span>
                    </button>
                    <button
                      type="button"
                      className="btn-action btn-action--danger"
                      onClick={() => handleIntegranteEliminado(integrante.idIntegrante)}
                      title="Eliminar integrante"
                    >
                      <Trash2 size={14} />
                      <span>Eliminar</span>
                    </button>
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}

      {/* Modales */}
      {modalAbierto && (
        <NuevoIntegranteModal
          onClose={() => setModalAbierto(false)}
          onCreated={handleIntegranteCreado}
        />
      )}

      {integranteEditando && (
        <EditarIntegranteModal
          integrante={integranteEditando}
          onClose={() => setIntegranteEditando(null)}
          onUpdated={handleIntegranteActualizado}
        />
      )}
    </div>
  );
}
