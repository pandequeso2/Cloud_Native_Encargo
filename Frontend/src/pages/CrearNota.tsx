import { useState, useEffect, type FormEvent } from 'react';
import { notasApi, kafkaApi, type AlertaAcademicaDto, type NotaResponse } from '../api/services';
import '../styles/crearNota.css';
import {
  Activity,
  Send,
  Radio,
  Server,
  CheckCircle2,
  AlertTriangle,
  RefreshCw,
  Trash2,
  Cpu,
  Layers,
  Loader2,
  Database,
  ArrowRight,
  ShieldAlert,
  Hash,
} from 'lucide-react';

export function CrearNota() {
  const [idEstudiante, setIdEstudiante] = useState<number | ''>(1);
  const [asignatura, setAsignatura] = useState('Arquitectura de Software');
  const [calificacion, setCalificacion] = useState<number | ''>(3.5);
  const [idPonderacion, setIdPonderacion] = useState<number>(1);

  const [submitting, setSubmitting] = useState(false);
  const [feedback, setFeedback] = useState<{ type: 'success' | 'error'; message: string; nota?: NotaResponse } | null>(null);

  // Lista de alertas capturadas por Kafka Streams
  const [alertas, setAlertas] = useState<AlertaAcademicaDto[]>([]);
  const [loadingAlertas, setLoadingAlertas] = useState(false);
  const [notasRegistradas, setNotasRegistradas] = useState<NotaResponse[]>([]);

  // Función para cargar alertas del tópico academic-alerts-output vía kafka_service
  const cargarAlertas = async () => {
    try {
      setLoadingAlertas(true);
      const data = await kafkaApi.obtenerAlertas();
      setAlertas(data);
    } catch {
      // Si kafka_service aún no está activo, no interrumpimos la vista
    } finally {
      setLoadingAlertas(false);
    }
  };

  // Carga inicial y sondeo periódico de alertas en tiempo real
  useEffect(() => {
    cargarAlertas();
    const timer = setInterval(() => {
      cargarAlertas();
    }, 4000);
    return () => clearInterval(timer);
  }, []);

  const handleLimpiarAlertas = async () => {
    try {
      await kafkaApi.limpiarAlertas();
      setAlertas([]);
    } catch (err) {
      console.error('Error al limpiar alertas', err);
    }
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setFeedback(null);

    if (idEstudiante === '' || Number(idEstudiante) <= 0) {
      setFeedback({ type: 'error', message: 'Ingresa un ID de estudiante válido (mayor a 0).' });
      return;
    }

    if (!asignatura.trim()) {
      setFeedback({ type: 'error', message: 'El nombre de la asignatura no puede estar vacío.' });
      return;
    }

    if (calificacion === '' || Number(calificacion) < 1.0 || Number(calificacion) > 7.0) {
      setFeedback({ type: 'error', message: 'La calificación debe estar entre 1.0 y 7.0 (escala chilena).' });
      return;
    }

    setSubmitting(true);
    try {
      // POST al API Gateway: /api/v1/notas -> notas_service -> Kafka
      const response = await notasApi.crear({
        idIntegrante: Number(idEstudiante),
        asignatura: asignatura.trim(),
        nota: Number(calificacion),
        ponderacion: { idPonderacion: Number(idPonderacion) || 1 },
      });

      const esDeficiente = Number(calificacion) < 4.0;
      const mensajeExito = esDeficiente
        ? `Nota ${response.nota} guardada en MySQL y emitida a Kafka. ¡Calificación < 4.0 procesada por Kafka Streams para emitir Alerta Temprana!`
        : `Nota ${response.nota} guardada en MySQL y publicada en Kafka exitosamente (>= 4.0 aprobatoria).`;

      setFeedback({
        type: 'success',
        message: mensajeExito,
        nota: response,
      });

      setNotasRegistradas((prev) => [response, ...prev.slice(0, 9)]);

      // Cargar alertas inmediatamente después de emitir
      setTimeout(cargarAlertas, 800);
      setTimeout(cargarAlertas, 2000);
    } catch (err) {
      const errorMsg = (err as Error).message ?? 'Ocurrió un error al registrar la nota en el API Gateway.';
      setFeedback({ type: 'error', message: errorMsg });
    } finally {
      setSubmitting(false);
    }
  };

  const calificacionNum = Number(calificacion);
  const esDeficientePreview = calificacion !== '' && calificacionNum < 4.0 && calificacionNum >= 1.0;

  return (
    <div className="control-center-page">
      {/* Encabezado del Centro de Comando Kafka */}
      <header className="control-center-header">
        <div className="control-center-header__info">
          <div className="control-center-badge control-center-badge--kafka">
            <Activity size={14} className="control-center-badge__icon" />
            <span>KAFKA EVENT STREAMING & REAL-TIME ANALYTICS</span>
          </div>
          <h1 className="control-center-header__title">Registro de Notas & Kafka Streams</h1>
          <p className="control-center-header__subtitle">
            Cada calificación se persiste en MySQL vía API Gateway y se emite como evento a Apache Kafka. La topología de Kafka Streams evalúa las notas en tiempo real para generar alertas tempranas cuando la calificación es deficiente (&lt; 4.0).
          </p>
        </div>

        <div className="control-center-header__status">
          <span className="hud-pill hud-pill--online">
            <span className="hud-pill__dot" />
            <Server size={14} />
            <span>Kafka Broker :9092</span>
          </span>
          <span className="hud-pill hud-pill--online">
            <Cpu size={14} />
            <span>KStreams Topology: Activa</span>
          </span>
        </div>
      </header>

      {/* Diagrama Interactivo de Flujo de Eventos (Glassmorphism) */}
      <section className="architecture-diagram-card">
        <div className="architecture-diagram-header">
          <Cpu size={18} className="text-primary" />
          <h3 className="architecture-diagram-title">Pipeline de Procesamiento de Eventos en Tiempo Real</h3>
        </div>
        <div className="pipeline-flow">
          <div className="flow-node flow-node--producer">
            <span className="flow-node__type">Origen / Cliente</span>
            <span className="flow-node__name">React SPA Frontend</span>
          </div>
          <span className="flow-connector">➔ [HTTP] ➔</span>

          <div className="flow-node flow-node--exchange">
            <span className="flow-node__type">API Gateway (:8095)</span>
            <span className="flow-node__name">Spring Cloud Gateway</span>
          </div>
          <span className="flow-connector">➔</span>

          <div className="flow-node flow-node--queue">
            <span className="flow-node__type">Microservicio MySQL</span>
            <span className="flow-node__name">notas_service (:8086)</span>
          </div>
          <span className="flow-connector">➔ [academic-grades-input] ➔</span>

          <div className="flow-node flow-node--consumer">
            <span className="flow-node__type">Kafka Streams Topology</span>
            <span className="flow-node__name">academic-pipeline-app</span>
          </div>
          <span className="flow-connector flow-connector--fail">➔ (&lt; 4.0) ➔</span>

          <div className="flow-node flow-node--dlq">
            <span className="flow-node__type">Tópico de Alertas</span>
            <span className="flow-node__name">academic-alerts-output</span>
          </div>
        </div>
      </section>

      {/* Banner de Feedback de Operación */}
      {feedback && (
        <div className={`control-feedback control-feedback--${feedback.type}`}>
          <div className="feedback-content">
            <strong>{feedback.type === 'success' ? '✓ Operación exitosa:' : '⚠ Atención:'}</strong>{' '}
            {feedback.message}
            {feedback.nota && (
              <div className="feedback-sub">
                ID Nota: <strong>#{feedback.nota.idNota}</strong> · Estudiante: <strong>#{feedback.nota.idIntegrante}</strong> · Nota: <strong>{feedback.nota.nota.toFixed(1)}</strong>
              </div>
            )}
          </div>
          <button onClick={() => setFeedback(null)} className="feedback-close-btn" aria-label="Cerrar">✕</button>
        </div>
      )}

      {/* Grilla Principal: Formulario de Emisión + Consola de Alertas */}
      <div className="control-modules-grid">
        {/* PANEL 1: REGISTRO Y EMISIÓN DE EVENTOS */}
        <section className="command-card command-card--highlight-blue">
          <div className="command-card__header">
            <div>
              <div className="command-card__tag">Publicador de Eventos</div>
              <h2 className="command-card__title">Nueva Calificación Académica</h2>
            </div>
          </div>

          <p className="command-card__desc">
            Emite los datos de la nota hacia el Gateway (<code>POST /api/v1/notas</code>). El microservicio la persiste y produce un evento en el tópico <code>academic-grades-input</code>.
          </p>

          <form onSubmit={handleSubmit} className="command-form">
            <div className="form-field">
              <label htmlFor="idEstudiante">ID del Estudiante / Integrante *</label>
              <input
                id="idEstudiante"
                type="number"
                min="1"
                required
                value={idEstudiante}
                onChange={(e) => setIdEstudiante(e.target.value === '' ? '' : Number(e.target.value))}
                placeholder="Ej. 1"
              />
              <span className="field-hint">
                Se utiliza como Key en Kafka para garantizar el orden estricto de eventos por partición.
              </span>
            </div>

            <div className="form-field">
              <label htmlFor="asignatura">Asignatura / Módulo *</label>
              <input
                id="asignatura"
                type="text"
                required
                value={asignatura}
                onChange={(e) => setAsignatura(e.target.value)}
                placeholder="Ej. Arquitectura Cloud Native"
              />
            </div>

            <div className="form-field">
              <label htmlFor="calificacion">Calificación (Escala 1.0 a 7.0) *</label>
              <input
                id="calificacion"
                type="number"
                step="0.1"
                min="1.0"
                max="7.0"
                required
                value={calificacion}
                onChange={(e) => setCalificacion(e.target.value === '' ? '' : Number(e.target.value))}
                placeholder="Ej. 3.5 o 6.2"
              />
            </div>

            {/* Vista Previa de Evaluación en Vivo */}
            {calificacion !== '' && (
              <div className={`rating-preview-box ${esDeficientePreview ? 'rating-preview-box--alert' : 'rating-preview-box--pass'}`}>
                <div className="rating-preview-box__icon">
                  {esDeficientePreview ? <ShieldAlert size={20} /> : <CheckCircle2 size={20} />}
                </div>
                <div className="rating-preview-box__text">
                  <strong>{esDeficientePreview ? 'Alerta Temprana Automática' : 'Calificación Aprobatoria'}</strong>
                  <div>
                    {esDeficientePreview
                      ? 'La nota es menor a 4.0. El pipeline de Kafka Streams emitirá un evento en academic-alerts-output.'
                      : 'La nota cumple el estándar mínimo (>= 4.0). Se procesa en el stream sin generar alerta crítica.'}
                  </div>
                </div>
              </div>
            )}

            <div className="form-field">
              <label htmlFor="idPonderacion">Ponderación del Encargo</label>
              <select
                id="idPonderacion"
                value={idPonderacion}
                onChange={(e) => setIdPonderacion(Number(e.target.value))}
              >
                <option value={1}>Ponderación 1 (Evaluación Parcial - 30%)</option>
                <option value={2}>Ponderación 2 (Evaluación Sumativa - 40%)</option>
                <option value={3}>Ponderación 3 (Examen Final - 30%)</option>
              </select>
            </div>

            <button type="submit" className="btn-primary" disabled={submitting} style={{ width: '100%', marginTop: '0.5rem' }}>
              {submitting ? (
                <>
                  <Loader2 size={16} className="animate-spin" />
                  <span>Publicando en Kafka & MySQL…</span>
                </>
              ) : (
                <>
                  <Send size={16} />
                  <span>Registrar Nota & Emitir a Kafka</span>
                </>
              )}
            </button>
          </form>

          {/* Historial de la Sesión Actual */}
          {notasRegistradas.length > 0 && (
            <div className="session-history">
              <h4 className="session-history__title">Notas Emitidas en esta Sesión</h4>
              <ul className="session-history__list">
                {notasRegistradas.map((n) => (
                  <li key={n.idNota} className="session-history__item">
                    <span>
                      Estudiante <strong>#{n.idIntegrante}</strong> ({n.asignatura ?? 'General'})
                    </span>
                    <span className={`grade-chip ${n.nota < 4.0 ? 'grade-chip--fail' : 'grade-chip--pass'}`}>
                      {n.nota.toFixed(1)}
                    </span>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </section>

        {/* PANEL 2: CONSOLA DE ALERTAS EN TIEMPO REAL */}
        <section className="command-card command-card--highlight-purple">
          <div className="command-card__header">
            <div>
              <div className="command-card__tag">Kafka Streams Output</div>
              <h2 className="command-card__title">Alertas en Tiempo Real</h2>
            </div>
            <div className="action-button-row">
              <button
                type="button"
                className="btn-secondary"
                onClick={cargarAlertas}
                disabled={loadingAlertas}
                title="Actualizar alertas"
              >
                <RefreshCw size={13} className={loadingAlertas ? 'animate-spin' : ''} />
                <span>Actualizar</span>
              </button>
              {alertas.length > 0 && (
                <button
                  type="button"
                  className="btn-danger"
                  onClick={handleLimpiarAlertas}
                  title="Limpiar registro de alertas"
                >
                  <Trash2 size={13} />
                  <span>Limpiar</span>
                </button>
              )}
            </div>
          </div>

          <p className="command-card__desc">
            Tópico de salida: <code>academic-alerts-output</code>. Eventos filtrados y transformados por <code>PipelineTopology.java</code> ante notas reprobatorias.
          </p>

          <div className="alerts-feed-container">
            {alertas.length === 0 ? (
              <div className="empty-alerts-box">
                <CheckCircle2 size={36} className="empty-alerts-box__icon" />
                <div className="empty-alerts-box__title">Sin alertas críticas registradas</div>
                <div className="empty-alerts-box__subtitle">
                  Ingresa una calificación menor a <strong>4.0</strong> en el panel izquierdo para disparar el flujo de Kafka Streams en tiempo real.
                </div>
              </div>
            ) : (
              alertas.map((alerta) => (
                <article key={alerta.idAlerta} className="alert-radar-card">
                  <div className="alert-radar-card__top">
                    <span className="badge badge--danger">
                      <ShieldAlert size={12} /> {alerta.nivelAlerta ?? 'CRÍTICA'}
                    </span>
                    <span className="alert-radar-card__time font-mono">
                      {alerta.fechaAlerta ? new Date(alerta.fechaAlerta).toLocaleTimeString() : 'En vivo'}
                    </span>
                  </div>
                  <div className="alert-radar-card__message">{alerta.mensaje}</div>
                  <div className="alert-radar-card__meta">
                    <span className="badge badge--neutral">
                      <Hash size={12} /> Estudiante #{alerta.idEstudiante}
                    </span>
                    <span className="badge badge--neutral">{alerta.asignatura}</span>
                    <span className="grade-pill grade-pill--fail">
                      Nota: <strong>{alerta.calificacion?.toFixed(1)}</strong>
                    </span>
                  </div>
                </article>
              ))
            )}
          </div>
        </section>
      </div>
    </div>
  );
}

export default CrearNota;
