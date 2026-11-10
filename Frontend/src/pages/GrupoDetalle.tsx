import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { gruposApi } from '../api/services';
import type { GrupoConIntegrantes, GrupoConTrabajo } from '../types/domain';
import { EmptyPanel, ErrorPanel, LoadingPanel } from '../components/AsyncState';
import {
  ArrowLeft,
  Users,
  FolderKanban,
  Calendar,
  Mail,
  Clock,
  CheckCircle2,
  AlertCircle,
  Hash,
  Layers,
  Award,
} from 'lucide-react';

export function GrupoDetalle() {
  const { idGrupo } = useParams<{ idGrupo: string }>();
  const id = Number(idGrupo);

  const [conIntegrantes, setConIntegrantes] = useState<GrupoConIntegrantes | null>(null);
  const [conTrabajo, setConTrabajo] = useState<GrupoConTrabajo | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([gruposApi.conIntegrantes(id), gruposApi.conTrabajo(id)])
      .then(([integrantes, trabajo]) => {
        setConIntegrantes(integrantes);
        setConTrabajo(trabajo);
      })
      .catch((err) => setError(err.message ?? 'No se pudo cargar el grupo.'));
  }, [id]);

  if (error) return <ErrorPanel message={error} />;
  if (!conIntegrantes) return <LoadingPanel label="Cargando información del grupo de trabajo…" />;

  const getInitials = (nombre: string, apellido: string) => {
    const n = nombre ? nombre.trim().charAt(0) : '';
    const a = apellido ? apellido.trim().charAt(0) : '';
    return (n + a).toUpperCase() || 'IN';
  };

  const avatarGradients = [
    'linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%)',
    'linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%)',
    'linear-gradient(135deg, #10b981 0%, #047857 100%)',
    'linear-gradient(135deg, #f59e0b 0%, #b45309 100%)',
    'linear-gradient(135deg, #ec4899 0%, #be185d 100%)',
  ];

  return (
    <div className="page-container">
      {/* Botón Volver y Encabezado */}
      <header className="page-header">
        <div className="page-header__info">
          <Link to="/grupos" className="btn-action btn-action--edit" style={{ marginBottom: '0.75rem' }}>
            <ArrowLeft size={14} />
            <span>Volver a Grupos</span>
          </Link>
          <div className="page-header__eyebrow">Detalle de Equipo</div>
          <h1 className="page-header__title">{conIntegrantes.nombreGrupo}</h1>
          <p className="page-header__subtitle">
            Capacidad: <strong>{conIntegrantes.capacidadMaxima} integrantes</strong> · Creado el {conIntegrantes.fechaCreacion || 'Fecha no registrada'}
          </p>
        </div>

        <div className="page-header__actions">
          {conIntegrantes.grupoLleno ? (
            <span className="badge badge--danger" style={{ fontSize: '0.85rem', padding: '0.4rem 0.8rem' }}>
              <AlertCircle size={14} /> Cupo Lleno
            </span>
          ) : (
            <span className="badge badge--success" style={{ fontSize: '0.85rem', padding: '0.4rem 0.8rem' }}>
              <CheckCircle2 size={14} /> Con Cupo Disponible ({conIntegrantes.capacidadMaxima - conIntegrantes.integrantes.length} vacantes)
            </span>
          )}
        </div>
      </header>

      {/* Sección 1: Integrantes del Grupo */}
      <section style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Users size={18} className="text-primary" />
          <h3 style={{ fontSize: '1.15rem', fontWeight: 700, margin: 0 }}>
            Integrantes Asignados ({conIntegrantes.integrantes.length}/{conIntegrantes.capacidadMaxima})
          </h3>
        </div>

        {conIntegrantes.integrantes.length === 0 ? (
          <EmptyPanel
            message="Este grupo aún no tiene integrantes registrados."
            subtitle="Los estudiantes asignados a este grupo aparecerán aquí con sus correos y disponibilidad."
          />
        ) : (
          <div className="grid-cards">
            {conIntegrantes.integrantes.map((integrante, index) => {
              const initials = getInitials(integrante.nombre, integrante.apellido);
              const gradient = avatarGradients[index % avatarGradients.length];

              return (
                <div key={integrante.idIntegrante} className="data-card">
                  <div className="data-card__body">
                    <div className="data-card__top">
                      <div className="user-avatar-initials" style={{ background: gradient }}>
                        {initials}
                      </div>
                      <span className="badge badge--purple">
                        <Hash size={12} /> ID #{integrante.idIntegrante}
                      </span>
                    </div>

                    <h4 className="data-card__title">
                      {integrante.nombre} {integrante.apellido}
                    </h4>

                    <div className="data-card__meta-item">
                      <Mail size={14} className="data-card__meta-icon" />
                      <span className="data-card__meta-text">{integrante.correoElectronico}</span>
                    </div>

                    <div className="data-card__meta-item">
                      <Clock size={14} className="data-card__meta-icon" />
                      <span className="data-card__meta-text">
                        Disponibilidad: <strong>{integrante.disponibilidad || 'No especificada'}</strong>
                      </span>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </section>

      {/* Sección 2: Trabajo Asignado al Grupo */}
      <section style={{ display: 'flex', flexDirection: 'column', gap: '1rem', marginTop: '1rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <FolderKanban size={18} className="text-primary" />
          <h3 style={{ fontSize: '1.15rem', fontWeight: 700, margin: 0 }}>Trabajo Académico Asignado</h3>
        </div>

        {conTrabajo?.trabajo ? (
          <div className="data-card" style={{ maxWidth: '38rem' }}>
            <div className="data-card__body">
              <div className="data-card__top">
                <span className="badge badge--info">
                  {conTrabajo.trabajo.tipoTrabajo || 'ENCARGO'}
                </span>
                <span
                  className={`badge ${
                    conTrabajo.trabajo.estado === 'EVALUADO'
                      ? 'badge--success'
                      : conTrabajo.trabajo.estado === 'ENTREGADO'
                      ? 'badge--info'
                      : 'badge--warning'
                  }`}
                >
                  {conTrabajo.trabajo.estado}
                </span>
              </div>

              <h4 className="data-card__title">{conTrabajo.trabajo.nombreTrabajo}</h4>

              <div className="data-card__meta-item">
                <Calendar size={14} className="data-card__meta-icon" />
                <span className="data-card__meta-text">
                  Semestre {conTrabajo.trabajo.semestre} · Ponderación: <strong>{conTrabajo.trabajo.porcentajeNota}% de la nota final</strong>
                </span>
              </div>

              <div className="data-card__meta-item">
                <Award size={14} className="data-card__meta-icon" />
                <span className="data-card__meta-text">
                  Fecha límite: <strong>{conTrabajo.trabajo.fechaEntrega || 'Por definir'}</strong>
                </span>
              </div>
            </div>
          </div>
        ) : (
          <EmptyPanel
            message="Este grupo no tiene ningún encargo o trabajo vinculado actualmente."
            subtitle="Asigna un trabajo del semestre a este grupo desde la vista de Trabajos."
          />
        )}
      </section>
    </div>
  );
}

export default GrupoDetalle;
