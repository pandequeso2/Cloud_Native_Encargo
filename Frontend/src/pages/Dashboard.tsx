import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { entregasApi, gruposApi, trabajosApi } from '../api/services';
import { ErrorPanel, LoadingPanel } from '../components/AsyncState';
import { useAuthInfo } from '../auth/useAuthInfo';
import {
  Users,
  FolderKanban,
  FileCheck,
  TrendingUp,
  Clock,
  CheckCircle2,
  AlertCircle,
  ArrowRight,
  Activity,
  Radio,
  Sparkles,
  Calendar,
  Layers,
  Award,
  RefreshCw,
} from 'lucide-react';

interface SummaryData {
  grupos: number;
  gruposLlenos: number;
  totalTrabajos: number;
  trabajosPendientes: number;
  trabajosEntregados: number;
  trabajosEvaluados: number;
  entregasRecientes: number;
  avancePorcentaje: number;
}

export function Dashboard() {
  const auth = useAuthInfo();
  const [summary, setSummary] = useState<SummaryData | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  const loadData = () => {
    setLoading(true);
    setError(null);

    Promise.all([gruposApi.listar(), trabajosApi.listar(), entregasApi.listar()])
      .then(([grupos, trabajos, entregas]) => {
        const totalTrabajos = trabajos.length;
        const pendientes = trabajos.filter((t) => t.estado === 'PENDIENTE').length;
        const entregados = trabajos.filter((t) => t.estado === 'ENTREGADO').length;
        const evaluados = trabajos.filter((t) => t.estado === 'EVALUADO').length;
        const gruposLlenos = grupos.filter((g) => g.grupoLleno).length;

        // Porcentaje de avance: trabajos que ya fueron entregados o evaluados vs total
        const avance = totalTrabajos > 0 ? Math.round(((totalTrabajos - pendientes) / totalTrabajos) * 100) : 0;

        setSummary({
          grupos: grupos.length,
          gruposLlenos,
          totalTrabajos,
          trabajosPendientes: pendientes,
          trabajosEntregados: entregados,
          trabajosEvaluados: evaluados,
          entregasRecientes: entregas.length,
          avancePorcentaje: avance,
        });
      })
      .catch((err) => {
        setError(err.message ?? 'No se pudo cargar el resumen de métricas.');
      })
      .finally(() => {
        setLoading(false);
      });
  };

  useEffect(() => {
    loadData();
  }, []);

  const firstName = auth?.displayName.split(' ')[0] ?? 'Colega';

  return (
    <div className="dashboard-container">
      {/* Banner de Bienvenida y Encabezado */}
      <header className="dashboard-header">
        <div className="dashboard-header__welcome">
          <div className="dashboard-header__badge">
            <Sparkles size={14} className="dashboard-header__badge-icon" />
            <span>Taller de Arquitectura Cloud Native · 2026</span>
          </div>
          <h1 className="dashboard-header__title">¡Hola, {firstName}!</h1>
          <p className="dashboard-header__subtitle">
            Monitoreo en tiempo real de avance académico, grupos de trabajo, mensajería Kafka y colas RabbitMQ.
          </p>
        </div>

        <div className="dashboard-header__actions">
          <button className="btn-secondary dashboard-header__refresh" onClick={loadData} disabled={loading}>
            <RefreshCw size={15} className={loading ? 'animate-spin' : ''} />
            <span>Actualizar Métricas</span>
          </button>
        </div>
      </header>

      {error && <ErrorPanel message={error} onRetry={loadData} />}
      {loading && !summary && <LoadingPanel label="Calculando estadísticas del semestre…" />}

      {summary && (
        <>
          {/* Grilla de 4 Tarjetas de Estadísticas Principales */}
          <div className="metrics-grid">
            {/* Tarjeta 1: Grupos Registrados */}
            <Link to="/grupos" className="metric-card metric-card--blue">
              <div className="metric-card__header">
                <span className="metric-card__label">Grupos de Trabajo</span>
                <div className="metric-card__icon-box metric-card__icon-box--blue">
                  <Users size={22} />
                </div>
              </div>
              <div className="metric-card__value">{summary.grupos}</div>
              <div className="metric-card__footer">
                <span className="metric-card__hint">
                  <strong>{summary.grupos - summary.gruposLlenos}</strong> con cupo disponible · {summary.gruposLlenos} llenos
                </span>
                <ArrowRight size={16} className="metric-card__arrow" />
              </div>
            </Link>

            {/* Tarjeta 2: Trabajos Pendientes */}
            <Link to="/trabajos" className="metric-card metric-card--amber">
              <div className="metric-card__header">
                <span className="metric-card__label">Trabajos Pendientes</span>
                <div className="metric-card__icon-box metric-card__icon-box--amber">
                  <Clock size={22} />
                </div>
              </div>
              <div className="metric-card__value">{summary.trabajosPendientes}</div>
              <div className="metric-card__footer">
                <span className="metric-card__hint">
                  de <strong>{summary.totalTrabajos}</strong> encargos totales del semestre
                </span>
                <ArrowRight size={16} className="metric-card__arrow" />
              </div>
            </Link>

            {/* Tarjeta 3: Entregas Registradas */}
            <Link to="/entregas" className="metric-card metric-card--emerald">
              <div className="metric-card__header">
                <span className="metric-card__label">Entregas Realizadas</span>
                <div className="metric-card__icon-box metric-card__icon-box--emerald">
                  <FileCheck size={22} />
                </div>
              </div>
              <div className="metric-card__value">{summary.entregasRecientes}</div>
              <div className="metric-card__footer">
                <span className="metric-card__hint">
                  registradas con retroalimentación docente
                </span>
                <ArrowRight size={16} className="metric-card__arrow" />
              </div>
            </Link>

            {/* Tarjeta 4: Cumplimiento y Evaluación */}
            <div className="metric-card metric-card--purple">
              <div className="metric-card__header">
                <span className="metric-card__label">Avance General</span>
                <div className="metric-card__icon-box metric-card__icon-box--purple">
                  <Award size={22} />
                </div>
              </div>
              <div className="metric-card__value">{summary.avancePorcentaje}%</div>
              <div className="metric-card__footer">
                <span className="metric-card__hint">
                  <strong>{summary.trabajosEvaluados}</strong> evaluados · <strong>{summary.trabajosEntregados}</strong> en revisión
                </span>
                <span className="metric-badge metric-badge--success">Al día</span>
              </div>
            </div>
          </div>

          {/* Barra de Progreso Visual de Avance del Semestre */}
          <div className="progress-section">
            <div className="progress-section__header">
              <div>
                <h3 className="progress-section__title">
                  <TrendingUp size={18} className="progress-section__title-icon" />
                  Progreso Global de Entregas del Semestre
                </h3>
                <p className="progress-section__subtitle">
                  Porcentaje de trabajos en fase entregada o evaluada respecto al total de encargos del periodo académico.
                </p>
              </div>
              <div className="progress-section__rate">
                <span className="progress-section__rate-number">{summary.avancePorcentaje}%</span>
                <span className="progress-section__rate-text">Completado</span>
              </div>
            </div>

            {/* Barra de Progreso Moderna con Relleno Gradiente */}
            <div className="progress-bar-track">
              <div
                className="progress-bar-fill"
                style={{ width: `${Math.min(100, Math.max(5, summary.avancePorcentaje))}%` }}
              >
                <div className="progress-bar-glow" />
              </div>
            </div>

            {/* Desglose de Estado de Trabajos en Chips */}
            <div className="progress-breakdown">
              <div className="progress-chip progress-chip--evaluado">
                <CheckCircle2 size={15} />
                <span><strong>{summary.trabajosEvaluados}</strong> Evaluados</span>
              </div>
              <div className="progress-chip progress-chip--entregado">
                <FileCheck size={15} />
                <span><strong>{summary.trabajosEntregados}</strong> Entregados</span>
              </div>
              <div className="progress-chip progress-chip--pendiente">
                <Clock size={15} />
                <span><strong>{summary.trabajosPendientes}</strong> Pendientes</span>
              </div>
              <div className="progress-chip progress-chip--total">
                <Layers size={15} />
                <span><strong>{summary.totalTrabajos}</strong> Trabajos Totales</span>
              </div>
            </div>
          </div>

          {/* Accesos Rápidos del Centro de Comando */}
          <div className="quick-access-section">
            <h3 className="quick-access-title">Accesos Rápidos del Taller</h3>
            <div className="quick-access-grid">
              <Link to="/crear-nota" className="quick-access-card quick-access-card--kafka">
                <div className="quick-access-card__icon">
                  <Activity size={24} />
                </div>
                <div className="quick-access-card__content">
                  <h4>Streaming Notas (Kafka)</h4>
                  <p>Publicación de calificaciones en tiempo real y detección de alertas académicas mediante Kafka Streams.</p>
                </div>
                <ArrowRight size={18} className="quick-access-card__arrow" />
              </Link>

              <Link to="/rabbitmq" className="quick-access-card quick-access-card--rabbitmq">
                <div className="quick-access-card__icon">
                  <Radio size={24} />
                </div>
                <div className="quick-access-card__content">
                  <h4>Centro de Comando RabbitMQ</h4>
                  <p>Simulación de órdenes, reintentos con Dead Letter Exchange (DLX) y administración dinámica de colas.</p>
                </div>
                <ArrowRight size={18} className="quick-access-card__arrow" />
              </Link>

              <Link to="/comentarios" className="quick-access-card quick-access-card--comentarios">
                <div className="quick-access-card__icon">
                  <MessageSquare size={24} />
                </div>
                <div className="quick-access-card__content">
                  <h4>Comentarios & Event Sourcing</h4>
                  <p>Registro y sincronización de retroalimentación docente desacoplada a través de tópicos Kafka.</p>
                </div>
                <ArrowRight size={18} className="quick-access-card__arrow" />
              </Link>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
