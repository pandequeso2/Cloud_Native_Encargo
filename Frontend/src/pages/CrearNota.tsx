import { useState, useEffect, type FormEvent } from 'react';
import { notasApi, kafkaApi, type AlertaAcademicaDto, type NotaResponse } from '../api/services';
import '../styles/crearNota.css';

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
      // Si kafka_service aún no está activo o no hay conexión directa, no interrumpimos la vista
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
        ? `Nota ${response.nota} registrada en MySQL y enviada a Kafka (Topic: academic-grades-input). ¡Atención: Nota < 4.0 procesada por el pipeline de Kafka Streams para generar Alerta Temprana!`
        : `Nota ${response.nota} guardada en MySQL y publicada en Kafka exitosamente. (Aprobatoria >= 4.0, no requiere alerta crítica).`;

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
    <>
      <header className="content__header">
        <div className="content__eyebrow">Event-Driven Architecture & Kafka Streams</div>
        <h1>Registro de Notas & Detección de Alertas</h1>
        <p className="content__subtitle">
          Ingresa calificaciones académicas. Cada nota es persistida en MySQL a través del API Gateway
          e inmediatamente emitida a Apache Kafka. El pipeline de Kafka Streams analiza los eventos en
          tiempo real para detectar calificaciones deficientes (&lt; 4.0).
        </p>
      </header>

      {/* Diagrama interactivo del flujo de eventos */}
      <section className="pipeline-diagram">
        <div className="pipeline-diagram__title">Flujo de Arquitectura Cloud Native & Streaming</div>
        <div className="pipeline-steps">
          <div className="pipeline-step pipeline-step--active">
            <span className="pipeline-step__tag">Client</span>
            <span className="pipeline-step__name">React SPA</span>
          </div>
          <span className="pipeline-arrow">➔</span>
          <div className="pipeline-step pipeline-step--active">
            <span className="pipeline-step__tag">Port :8095</span>
            <span className="pipeline-step__name">Spring Cloud Gateway</span>
          </div>
          <span className="pipeline-arrow">➔</span>
          <div className="pipeline-step pipeline-step--active">
            <span className="pipeline-step__tag">Microservice</span>
            <span className="pipeline-step__name">notas_service (MySQL)</span>
          </div>
          <span className="pipeline-arrow">➔</span>
          <div className="pipeline-step pipeline-step--active">
            <span className="pipeline-step__tag">Kafka Topic</span>
            <span className="pipeline-step__name">academic-grades-input</span>
          </div>
          <span className="pipeline-arrow">➔</span>
          <div className="pipeline-step pipeline-step--alert">
            <span className="pipeline-step__tag">Kafka Streams</span>
            <span className="pipeline-step__name">academic-pipeline-app (&lt; 4.0)</span>
          </div>
          <span className="pipeline-arrow">➔</span>
          <div className="pipeline-step pipeline-step--alert">
            <span className="pipeline-step__tag">Kafka Topic</span>
            <span className="pipeline-step__name">academic-alerts-output</span>
          </div>
        </div>
      </section>

      {/* Banner de feedback */}
      {feedback && (
        <div className={`notification-banner notification-banner--${feedback.type}`}>
          <div>
            <strong>{feedback.type === 'success' ? '✓ Operación exitosa:' : '⚠ Error:'}</strong>{' '}
            {feedback.message}
            {feedback.nota && (
              <div style={{ marginTop: '0.35rem', fontSize: '0.8rem', opacity: 0.9 }}>
                ID Nota: <strong>#{feedback.nota.idNota}</strong> | Estudiante:{' '}
                <strong>{feedback.nota.idIntegrante}</strong> | Calificación: <strong>{feedback.nota.nota}</strong>
              </div>
            )}
          </div>
          <button
            onClick={() => setFeedback(null)}
            style={{ background: 'none', border: 'none', cursor: 'pointer', fontWeight: 'bold' }}
          >
            ✕
          </button>
        </div>
      )}

      <div className="crear-nota-grid">
        {/* Formulario de Registro */}
        <section className="card-panel">
          <h2 className="card-panel__title">Nueva Calificación Académica</h2>
          <p className="card-panel__description">
            Envía los datos de la nota hacia el Gateway (<code>POST /api/v1/notas</code>).
          </p>

          <form onSubmit={handleSubmit}>
            <div className="form-field">
              <label htmlFor="idEstudiante">ID del Estudiante / Integrante *</label>
              <input
                id="idEstudiante"
                type="number"
                min="1"
                required
                value={idEstudiante}
                onChange={(e) => setIdEstudiante(e.target.value === '' ? '' : Number(e.target.value))}
                placeholder="Ej. 101"
              />
              <span className="form-field__hint">
                Se utiliza como Key en Kafka para garantizar el orden de los eventos por partición.
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
              <span className="form-field__hint">
                En Chile: &lt; 4.0 es nota deficiente (reprobatoria). &gt;= 4.0 es aprobatoria.
              </span>
            </div>

            {/* Vista previa de evaluación en vivo */}
            {calificacion !== '' && (
              <div
                className={`alert-badge-preview ${
                  esDeficientePreview ? 'alert-badge-preview--fail' : 'alert-badge-preview--pass'
                }`}
              >
                <span>{esDeficientePreview ? '⚠️' : '✓'}</span>
                <div>
                  <strong>{esDeficientePreview ? 'Alerta Crítica Automática' : 'Calificación Aprobatoria'}</strong>
                  <div>
                    {esDeficientePreview
                      ? 'La nota es menor a 4.0. El pipeline de Kafka Streams emitirá un evento a academic-alerts-output.'
                      : 'La nota cumple el estándar mínimo (>= 4.0). Se registra en el stream sin generar alerta crítica.'}
                  </div>
                </div>
              </div>
            )}

            <div className="form-field">
              <label htmlFor="idPonderacion">ID Ponderación</label>
              <select
                id="idPonderacion"
                value={idPonderacion}
                onChange={(e) => setIdPonderacion(Number(e.target.value))}
              >
                <option value={1}>Ponderación 1 (Evaluación Parcial)</option>
                <option value={2}>Ponderación 2 (Evaluación Final)</option>
              </select>
            </div>

            <button type="submit" className="btn-primary" disabled={submitting} style={{ width: '100%', marginTop: '0.5rem' }}>
              {submitting ? 'Procesando en Gateway & Kafka…' : 'Registrar Nota & Publicar en Kafka'}
            </button>
          </form>

          {/* Historial de la sesión actual */}
          {notasRegistradas.length > 0 && (
            <div style={{ marginTop: '2rem', borderTop: '1px solid var(--line, #e2e8f0)', paddingTop: '1rem' }}>
              <div style={{ fontSize: '0.82rem', fontWeight: 600, color: 'var(--ink-soft, #64748b)', marginBottom: '0.6rem' }}>
                Notas registradas en esta sesión:
              </div>
              <ul style={{ listStyle: 'none', padding: 0, margin: 0, fontSize: '0.8rem' }}>
                {notasRegistradas.map((n) => (
                  <li
                    key={n.idNota}
                    style={{
                      display: 'flex',
                      justifyContent: 'space-between',
                      padding: '0.35rem 0',
                      borderBottom: '1px dashed #e2e8f0',
                    }}
                  >
                    <span>
                      Estudiante <strong>#{n.idIntegrante}</strong> ({n.asignatura ?? 'General'})
                    </span>
                    <strong style={{ color: n.nota < 4.0 ? '#dc2626' : '#16a34a' }}>
                      {n.nota.toFixed(1)}
                    </strong>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </section>

        {/* Panel de Alertas en Tiempo Real de Kafka Streams */}
        <section className="card-panel">
          <div className="alerts-header">
            <div>
              <h2 className="card-panel__title" style={{ margin: 0 }}>
                Alertas en Tiempo Real (Kafka Streams)
              </h2>
              <div style={{ fontSize: '0.78rem', color: 'var(--ink-soft, #64748b)', marginTop: '0.2rem' }}>
                Tópico de salida: <code>academic-alerts-output</code>
              </div>
            </div>
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <button
                type="button"
                className="btn-secondary"
                onClick={cargarAlertas}
                disabled={loadingAlertas}
                title="Actualizar alertas"
                style={{ fontSize: '0.75rem', padding: '0.3rem 0.6rem' }}
              >
                {loadingAlertas ? '…' : '↻ Actualizar'}
              </button>
              {alertas.length > 0 && (
                <button
                  type="button"
                  className="btn-secondary"
                  onClick={handleLimpiarAlertas}
                  title="Limpiar alertas"
                  style={{ fontSize: '0.75rem', padding: '0.3rem 0.6rem' }}
                >
                  Limpiar
                </button>
              )}
            </div>
          </div>

          <p className="card-panel__description">
            Eventos generados por <code>PipelineTopology.java</code> al detectar notas menores a 4.0.
          </p>

          <div className="alerts-list">
            {alertas.length === 0 ? (
              <div className="empty-state">
                <div>☕ No hay alertas críticas registradas por el momento.</div>
                <div style={{ fontSize: '0.78rem', marginTop: '0.4rem', color: '#94a3b8' }}>
                  Ingresa una calificación menor a <strong>4.0</strong> para disparar el flujo en tiempo real.
                </div>
              </div>
            ) : (
              alertas.map((alerta) => (
                <article key={alerta.idAlerta} className="alert-card">
                  <div className="alert-card__top">
                    <span className="alert-card__tag">{alerta.nivelAlerta ?? 'CRITICA'}</span>
                    <span className="alert-card__time">
                      {alerta.fechaAlerta ? new Date(alerta.fechaAlerta).toLocaleTimeString() : 'En vivo'}
                    </span>
                  </div>
                  <div className="alert-card__msg">{alerta.mensaje}</div>
                  <div className="alert-card__meta">
                    <span>Estudiante: #{alerta.idEstudiante}</span>
                    <span>Asignatura: {alerta.asignatura}</span>
                    <span>
                      Nota: <strong style={{ color: '#dc2626' }}>{alerta.calificacion?.toFixed(1)}</strong>
                    </span>
                  </div>
                </article>
              ))
            )}
          </div>
        </section>
      </div>
    </>
  );
}
export default CrearNota;

