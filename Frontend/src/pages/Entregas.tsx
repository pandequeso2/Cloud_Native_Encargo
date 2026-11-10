import { useEffect, useState } from 'react';
import { comentariosApi, entregasApi } from '../api/services';
import type { Comentario, Entrega } from '../types/domain';
import { EmptyPanel, ErrorPanel, LoadingPanel } from '../components/AsyncState';
import { NuevaEntregaModal } from '../components/NuevaEntregaModal';
import { useAuthInfo } from '../auth/useAuthInfo';
import {
  FileCheck,
  Plus,
  Calendar,
  MessageSquare,
  ChevronDown,
  ChevronUp,
  Hash,
  Users,
  FolderKanban,
  CheckCircle2,
} from 'lucide-react';

export function Entregas() {
  const auth = useAuthInfo();
  const [entregas, setEntregas] = useState<Entrega[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [abierta, setAbierta] = useState<number | null>(null);
  const [comentarios, setComentarios] = useState<Record<number, Comentario[]>>({});
  const [modalAbierto, setModalAbierto] = useState(false);
  const [loadingComentarios, setLoadingComentarios] = useState<number | null>(null);

  const cargarEntregas = () => {
    entregasApi
      .listar()
      .then(setEntregas)
      .catch((err) => setError(err.message ?? 'No se pudieron cargar las entregas.'));
  };

  useEffect(() => {
    cargarEntregas();
  }, []);

  const toggle = async (idEntrega: number) => {
    if (abierta === idEntrega) {
      setAbierta(null);
      return;
    }
    setAbierta(idEntrega);
    if (!comentarios[idEntrega]) {
      try {
        setLoadingComentarios(idEntrega);
        const lista = await comentariosApi.porEntrega(idEntrega);
        setComentarios((prev) => ({ ...prev, [idEntrega]: lista }));
      } catch (err) {
        console.error('Error al cargar comentarios:', err);
      } finally {
        setLoadingComentarios(null);
      }
    }
  };

  const handleEntregaCreada = (nueva: Entrega) => {
    setEntregas((prev) => (prev ? [...prev, nueva] : [nueva]));
    setModalAbierto(false);
  };

  return (
    <div className="page-container">
      {/* Encabezado */}
      <header className="page-header">
        <div className="page-header__info">
          <div className="page-header__eyebrow">Evaluación y Seguimiento</div>
          <h1 className="page-header__title">Entregas Registradas</h1>
          <p className="page-header__subtitle">
            Entregas cargadas por los grupos de trabajo con retroalimentación docente en tiempo real.
          </p>
        </div>

        {auth?.hasRole('Admin') && (
          <div className="page-header__actions">
            <button className="btn-primary" onClick={() => setModalAbierto(true)}>
              <Plus size={16} />
              <span>Nueva Entrega</span>
            </button>
          </div>
        )}
      </header>

      {error && <ErrorPanel message={error} onRetry={cargarEntregas} />}
      {!error && !entregas && <LoadingPanel label="Cargando entregas desde el microservicio…" />}

      {entregas && entregas.length === 0 && (
        <EmptyPanel
          message="No hay entregas registradas aún."
          subtitle="Las entregas que envíen los grupos aparecerán listadas aquí con sus respectivos comentarios y rúbricas."
          action={
            auth?.hasRole('Admin') ? (
              <button className="btn-primary" onClick={() => setModalAbierto(true)}>
                <Plus size={16} />
                <span>Registrar Primera Entrega</span>
              </button>
            ) : null
          }
        />
      )}

      {/* Listado de Entregas con Acordeón Moderno */}
      {entregas && entregas.length > 0 && (
        <div className="entregas-list">
          {entregas.map((entrega) => {
            const isAbierta = abierta === entrega.idEntrega;
            const comentariosEntrega = comentarios[entrega.idEntrega] ?? [];
            const tieneComentarios = comentariosEntrega.length > 0;

            return (
              <div
                key={entrega.idEntrega}
                className={`entrega-card ${isAbierta ? 'entrega-card--expanded' : ''}`}
              >
                <button
                  type="button"
                  className="entrega-card__trigger"
                  onClick={() => toggle(entrega.idEntrega)}
                  aria-expanded={isAbierta}
                >
                  <div className="entrega-card__left">
                    <div className="entrega-card__icon-box">
                      <FileCheck size={20} />
                    </div>
                    <div>
                      <div className="entrega-card__title">
                        Entrega #{entrega.idEntrega}
                      </div>
                      <div className="entrega-card__chips">
                        <span className="badge badge--purple">
                          <Users size={12} /> Grupo #{entrega.idGrupo}
                        </span>
                        <span className="badge badge--amber">
                          <FolderKanban size={12} /> Trabajo #{entrega.idTrabajo}
                        </span>
                        <span className="badge badge--neutral">
                          <Calendar size={12} /> {entrega.fechaEntrega || 'Fecha no registrada'}
                        </span>
                      </div>
                    </div>
                  </div>

                  <div className="entrega-card__right">
                    <span className="badge badge--info">
                      <MessageSquare size={12} />
                      <span>
                        {comentarios[entrega.idEntrega]
                          ? `${comentarios[entrega.idEntrega].length} comentarios`
                          : 'Ver comentarios'}
                      </span>
                    </span>
                    <div className="entrega-card__chevron">
                      {isAbierta ? <ChevronUp size={18} /> : <ChevronDown size={18} />}
                    </div>
                  </div>
                </button>

                {/* Sección Expandible de Comentarios */}
                {isAbierta && (
                  <div className="entrega-card__details">
                    <div className="entrega-card__details-header">
                      <h4 className="entrega-card__details-title">
                        <MessageSquare size={16} /> Retroalimentación y Comentarios Docentes
                      </h4>
                    </div>

                    {loadingComentarios === entrega.idEntrega && (
                      <div className="entrega-comments-loading">
                        <LoadingPanel label="Cargando comentarios asociados a esta entrega…" />
                      </div>
                    )}

                    {loadingComentarios !== entrega.idEntrega && !tieneComentarios && (
                      <div className="entrega-comments-empty">
                        <p>No se han registrado comentarios para esta entrega todavía.</p>
                      </div>
                    )}

                    {loadingComentarios !== entrega.idEntrega && tieneComentarios && (
                      <div className="entrega-comments-list">
                        {comentariosEntrega.map((c) => (
                          <div key={c.idComentario ?? c.id} className="comment-bubble">
                            <div className="comment-bubble__header">
                              <span className="comment-bubble__author">Usuario #{c.usuarioId}</span>
                              <span className="comment-bubble__date">{c.fechaCreacion || 'Reciente'}</span>
                            </div>
                            <div className="comment-bubble__body">{c.texto}</div>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}

      {/* Modal */}
      {modalAbierto && (
        <NuevaEntregaModal
          onClose={() => setModalAbierto(false)}
          onCreated={handleEntregaCreada}
        />
      )}
    </div>
  );
}
