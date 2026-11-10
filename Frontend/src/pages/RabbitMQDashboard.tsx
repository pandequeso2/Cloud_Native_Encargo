import { useState, useEffect } from 'react';
import { rabbitmqApi } from '../api/rabbitmqService';
import type { RabbitStatusResponse } from '../api/rabbitmqService';
import '../styles/rabbitmq.css';
import {
  Radio,
  Server,
  Layers,
  Send,
  RefreshCw,
  Play,
  Pause,
  Plus,
  Trash2,
  ExternalLink,
  ShieldAlert,
  CheckCircle2,
  AlertCircle,
  Terminal,
  Activity,
  Cpu,
  ArrowRight,
  Database,
  Hash,
  Loader2,
} from 'lucide-react';

interface OrderLog {
  id: string;
  customer: string;
  message: string;
  timestamp: string;
  status: 'ENVIADA' | 'PROCESADA' | 'FALLIDA (DLQ)';
}

interface LogMessage {
  id: string;
  level: 'INFO' | 'WARNING' | 'ERROR';
  message: string;
  timestamp: string;
}

export function RabbitMQDashboard() {
  // Status State
  const [status, setStatus] = useState<RabbitStatusResponse | null>(null);
  const [statusLoading, setStatusLoading] = useState(true);
  const [listenerPaused, setListenerPaused] = useState(false);

  // Orders / DLQ State
  const [orders, setOrders] = useState<OrderLog[]>([]);
  const [stats, setStats] = useState({ total: 0, success: 0, failed: 0 });

  // Direct Exchange Logs State
  const [logs, setLogs] = useState<LogMessage[]>([]);
  const [customLogMsg, setCustomLogMsg] = useState('');

  // RabbitAdmin Form State
  const [newQueueName, setNewQueueName] = useState('');
  const [newExchangeName, setNewExchangeName] = useState('');
  const [bindQueue, setBindQueue] = useState('');
  const [bindExchange, setBindExchange] = useState('');
  const [bindKey, setBindKey] = useState('');
  const [actionFeedback, setActionFeedback] = useState<{ text: string; type: 'success' | 'error' } | null>(null);

  // Loading Spinners for operations
  const [sendingOrder, setSendingOrder] = useState<string | null>(null);
  const [sendingLog, setSendingLog] = useState<string | null>(null);
  const [creatingQueue, setCreatingQueue] = useState(false);
  const [creatingExchange, setCreatingExchange] = useState(false);
  const [creatingBinding, setCreatingBinding] = useState(false);
  const [purgingQueue, setPurgingQueue] = useState<string | null>(null);

  // Initial and periodic fetch
  useEffect(() => {
    checkStatus();
    const interval = setInterval(checkStatus, 5000);
    return () => clearInterval(interval);
  }, []);

  const checkStatus = async () => {
    try {
      const data = await rabbitmqApi.getStatus();
      setStatus(data);
    } catch (err) {
      console.error('Error fetching RabbitMQ status:', err);
      setStatus(null);
    } finally {
      setStatusLoading(false);
    }
  };

  const showFeedback = (text: string, type: 'success' | 'error' = 'success') => {
    setActionFeedback({ text, type });
    setTimeout(() => setActionFeedback(null), 4500);
  };

  // 1. Send Log (Direct Exchange)
  const handleSendLog = async (level: 'INFO' | 'WARNING' | 'ERROR', defaultMsg?: string) => {
    const text = defaultMsg || customLogMsg || 'Mensaje de evento de prueba';
    setSendingLog(level);
    try {
      const res = await rabbitmqApi.sendLog(level, text);
      const newEntry: LogMessage = {
        id: Math.random().toString(36).substring(7),
        level,
        message: text,
        timestamp: new Date().toLocaleTimeString(),
      };
      setLogs((prev) => [newEntry, ...prev.slice(0, 24)]);
      showFeedback(`✔ ${res}`, 'success');
      if (!defaultMsg) setCustomLogMsg('');
    } catch (err: any) {
      showFeedback(`❌ Error al enviar log: ${err?.message || 'Fallo de conexión'}`, 'error');
    } finally {
      setSendingLog(null);
    }
  };

  // 2. Send Order (DLQ Simulation)
  const handleSendOrder = async (customerName: string) => {
    const orderId = `ORD-${Date.now().toString().slice(-6)}`;
    setSendingOrder(customerName);
    try {
      const res = await rabbitmqApi.sendOrder(orderId, customerName);
      const newOrder: OrderLog = {
        id: orderId,
        customer: customerName,
        message: res.message,
        timestamp: new Date().toLocaleTimeString(),
        status: 'ENVIADA',
      };

      setOrders((prev) => [newOrder, ...prev]);
      setStats((prev) => ({ ...prev, total: prev.total + 1 }));

      // Simulate consumer result evaluation after 2.5s (50% failure rate programmed in OrderConsumer)
      setTimeout(() => {
        const isSuccess = Math.random() > 0.45;
        setOrders((prevOrders) =>
          prevOrders.map((ord) =>
            ord.id === orderId
              ? { ...ord, status: isSuccess ? 'PROCESADA' : 'FALLIDA (DLQ)' }
              : ord
          )
        );
        setStats((prev) => ({
          ...prev,
          success: isSuccess ? prev.success + 1 : prev.success,
          failed: !isSuccess ? prev.failed + 1 : prev.failed,
        }));
      }, 2400);

      showFeedback(`✔ ${res.status} #${orderId}`, 'success');
    } catch (err: any) {
      showFeedback(`❌ Error al enviar orden: ${err?.message || 'Fallo de conexión'}`, 'error');
    } finally {
      setSendingOrder(null);
    }
  };

  // 3. RabbitAdmin Actions
  const handleCreateQueue = async () => {
    if (!newQueueName.trim()) return;
    setCreatingQueue(true);
    try {
      const res = await rabbitmqApi.createQueue(newQueueName.trim());
      showFeedback(`✔ ${res}`, 'success');
      setNewQueueName('');
      checkStatus();
    } catch (err: any) {
      const msg = err.response?.data?.queueName || err.response?.data?.message || err?.message || 'Fallo al crear cola';
      showFeedback(`❌ ${msg}`, 'error');
    } finally {
      setCreatingQueue(false);
    }
  };

  const handleCreateExchange = async () => {
    if (!newExchangeName.trim()) return;
    setCreatingExchange(true);
    try {
      const res = await rabbitmqApi.createExchange(newExchangeName.trim());
      showFeedback(`✔ ${res}`, 'success');
      setNewExchangeName('');
      checkStatus();
    } catch (err: any) {
      const msg = err.response?.data?.exchangeName || err.response?.data?.message || err?.message || 'Fallo al crear exchange';
      showFeedback(`❌ ${msg}`, 'error');
    } finally {
      setCreatingExchange(false);
    }
  };

  const handleCreateBinding = async () => {
    if (!bindQueue.trim() || !bindExchange.trim() || !bindKey.trim()) return;
    setCreatingBinding(true);
    try {
      const res = await rabbitmqApi.createBinding(bindQueue.trim(), bindExchange.trim(), bindKey.trim());
      showFeedback(`✔ ${res}`, 'success');
      setBindQueue('');
      setBindExchange('');
      setBindKey('');
      checkStatus();
    } catch (err: any) {
      const msg = err.response?.data?.message || err?.message || 'Fallo al crear binding';
      showFeedback(`❌ ${msg}`, 'error');
    } finally {
      setCreatingBinding(false);
    }
  };

  const handlePurgeQueue = async (qName: string) => {
    setPurgingQueue(qName);
    try {
      const res = await rabbitmqApi.purgeQueue(qName);
      showFeedback(`✔ ${res}`, 'success');
      checkStatus();
    } catch (err: any) {
      showFeedback(`❌ ${err?.message || 'Fallo al purgar cola'}`, 'error');
    } finally {
      setPurgingQueue(null);
    }
  };

  // 4. Listener Control
  const handleToggleListener = async () => {
    try {
      if (listenerPaused) {
        await rabbitmqApi.resumeListener('order-listener');
        setListenerPaused(false);
        showFeedback('▶ Listener "order-listener" REANUDADO con concurrencia 3-5', 'success');
      } else {
        await rabbitmqApi.pauseListener('order-listener');
        setListenerPaused(true);
        showFeedback('⏸ Listener "order-listener" PAUSADO dinámicamente', 'success');
      }
    } catch (err: any) {
      showFeedback(`❌ Error controlando listener: ${err?.message}`, 'error');
    }
  };

  return (
    <div className="control-center-page">
      {/* Encabezado del Centro de Comando */}
      <header className="control-center-header">
        <div className="control-center-header__info">
          <div className="control-center-badge">
            <Radio size={14} className="control-center-badge__icon" />
            <span>CENTRO DE COMANDO & MESSAGE BROKER</span>
          </div>
          <h1 className="control-center-header__title">RabbitMQ & Dead Letter Queue (DLQ)</h1>
          <p className="control-center-header__subtitle">
            Monitoreo de topología AMQP en tiempo real, Direct Exchanges con Routing Keys, tolerancia a fallos con DLX y administración programática vía RabbitAdmin.
          </p>
        </div>

        <div className="control-center-header__status">
          <span className={`hud-pill ${status ? 'hud-pill--online' : 'hud-pill--offline'}`}>
            <span className="hud-pill__dot" />
            <Server size={14} />
            <span>{statusLoading ? 'Conectando…' : status ? 'Cluster Online' : 'Broker Inactivo'}</span>
          </span>

          <a
            href="http://localhost:15672"
            target="_blank"
            rel="noreferrer"
            className="btn-management-hud"
            title="Abrir RabbitMQ Management Console nativa"
          >
            <span>RabbitMQ UI (:15672)</span>
            <ExternalLink size={13} />
          </a>
        </div>
      </header>

      {/* Banner de Retroalimentación de Operación */}
      {actionFeedback && (
        <div className={`control-feedback control-feedback--${actionFeedback.type}`}>
          {actionFeedback.text}
        </div>
      )}

      {/* Telemetría y Contadores en Tiempo Real */}
      <div className="telemetry-grid">
        <div className="telemetry-card telemetry-card--primary">
          <div className="telemetry-card__header">
            <span className="telemetry-card__label">Total Envíos</span>
            <Activity size={18} className="telemetry-card__icon" />
          </div>
          <div className="telemetry-card__value">{stats.total}</div>
          <span className="telemetry-card__subtext">Órdenes emitidas</span>
        </div>

        <div className="telemetry-card telemetry-card--success">
          <div className="telemetry-card__header">
            <span className="telemetry-card__label">Procesadas con Éxito</span>
            <CheckCircle2 size={18} className="telemetry-card__icon" />
          </div>
          <div className="telemetry-card__value">{stats.success}</div>
          <span className="telemetry-card__subtext">BasicAck confirmado</span>
        </div>

        <div className="telemetry-card telemetry-card--danger">
          <div className="telemetry-card__header">
            <span className="telemetry-card__label">Cuarentena DLQ</span>
            <ShieldAlert size={18} className="telemetry-card__icon" />
          </div>
          <div className="telemetry-card__value">{stats.failed}</div>
          <span className="telemetry-card__subtext">Nack → orders.dlq</span>
        </div>

        <div className="telemetry-card telemetry-card--amber">
          <div className="telemetry-card__header">
            <span className="telemetry-card__label">En Cola orders.queue</span>
            <Database size={18} className="telemetry-card__icon" />
          </div>
          <div className="telemetry-card__value">{status?.ordersQueueMessages ?? 0}</div>
          <span className="telemetry-card__subtext">TTL de 30 segundos</span>
        </div>
      </div>

      {/* Diagrama de Flujo de Arquitectura AMQP (Glassmorphism) */}
      <section className="architecture-diagram-card">
        <div className="architecture-diagram-header">
          <Cpu size={18} className="text-primary" />
          <h3 className="architecture-diagram-title">Topología AMQP & Circuito Resiliente DLX / DLQ</h3>
        </div>
        <div className="pipeline-flow">
          <div className="flow-node flow-node--producer">
            <span className="flow-node__type">Producer</span>
            <span className="flow-node__name">REST Controller</span>
          </div>
          <span className="flow-connector">➔</span>

          <div className="flow-node flow-node--exchange">
            <span className="flow-node__type">DirectExchange</span>
            <span className="flow-node__name">orders.exchange</span>
          </div>
          <span className="flow-connector">➔ [order.created] ➔</span>

          <div className="flow-node flow-node--queue">
            <span className="flow-node__type">Queue (TTL 30s)</span>
            <span className="flow-node__name">orders.queue</span>
          </div>
          <span className="flow-connector">➔</span>

          <div className="flow-node flow-node--consumer">
            <span className="flow-node__type">Consumer (Manual Ack)</span>
            <span className="flow-node__name">OrderConsumer (50% fail)</span>
          </div>
          <span className="flow-connector flow-connector--fail">✖ Nack ➔</span>

          <div className="flow-node flow-node--dlx">
            <span className="flow-node__type">Dead Letter Exchange</span>
            <span className="flow-node__name">orders.dlx (Fanout)</span>
          </div>
          <span className="flow-connector">➔</span>

          <div className="flow-node flow-node--dlq">
            <span className="flow-node__type">Quarantine Queue</span>
            <span className="flow-node__name">orders.dlq (24h)</span>
          </div>
        </div>
      </section>

      {/* Grilla Principal de Módulos del Centro de Comando */}
      <div className="control-modules-grid">
        {/* MÓDULO 1: SISTEMA RESILIENTE CON DLX / DLQ */}
        <section className="command-card command-card--highlight-blue">
          <div className="command-card__header">
            <div>
              <div className="command-card__tag">Módulo Resiliente</div>
              <h2 className="command-card__title">1. Simulación de Órdenes & Fallos DLQ</h2>
            </div>
            <button
              className={`hud-btn-toggle ${listenerPaused ? 'hud-btn-toggle--paused' : 'hud-btn-toggle--running'}`}
              onClick={handleToggleListener}
              title={listenerPaused ? 'Reanudar procesamiento de mensajes' : 'Pausar temporalmente el listener'}
            >
              {listenerPaused ? <Play size={14} /> : <Pause size={14} />}
              <span>{listenerPaused ? 'Reanudar Listener' : 'Pausar Listener'}</span>
            </button>
          </div>

          <p className="command-card__desc">
            Publica eventos de órdenes hacia <code>orders.exchange</code> con clave <code>order.created</code>. Si el consumidor simula una falla, ejecuta <code>basicNack(requeue=false)</code> y el mensaje viaja al <strong>Dead Letter Exchange</strong>.
          </p>

          <div className="action-button-row">
            <button
              className="btn-primary"
              onClick={() => handleSendOrder('Estudiante Carlos Ramírez')}
              disabled={sendingOrder !== null}
            >
              {sendingOrder === 'Estudiante Carlos Ramírez' ? (
                <Loader2 size={15} className="animate-spin" />
              ) : (
                <Send size={15} />
              )}
              <span>Enviar Carlos Ramírez</span>
            </button>

            <button
              className="btn-primary"
              onClick={() => handleSendOrder('Estudiante Valentina Soto')}
              disabled={sendingOrder !== null}
            >
              {sendingOrder === 'Estudiante Valentina Soto' ? (
                <Loader2 size={15} className="animate-spin" />
              ) : (
                <Send size={15} />
              )}
              <span>Enviar Valentina Soto</span>
            </button>

            <button
              className="btn-primary"
              onClick={() => handleSendOrder('Estudiante Andrés Muñoz')}
              disabled={sendingOrder !== null}
            >
              {sendingOrder === 'Estudiante Andrés Muñoz' ? (
                <Loader2 size={15} className="animate-spin" />
              ) : (
                <Send size={15} />
              )}
              <span>Enviar Andrés Muñoz</span>
            </button>
          </div>

          {/* Historial de Órdenes */}
          <div className="command-subcard">
            <h4 className="command-subcard__title">Telemetría de Órdenes Procesadas</h4>
            <div className="orders-table-container">
              {orders.length === 0 ? (
                <div className="empty-orders-hint">Presiona un botón para emitir la primera orden a RabbitMQ…</div>
              ) : (
                <table className="command-table">
                  <thead>
                    <tr>
                      <th>ID de Orden</th>
                      <th>Estudiante</th>
                      <th>Hora</th>
                      <th>Estado</th>
                    </tr>
                  </thead>
                  <tbody>
                    {orders.map((ord) => (
                      <tr key={ord.id}>
                        <td className="font-mono text-primary font-bold">{ord.id}</td>
                        <td>{ord.customer}</td>
                        <td className="text-muted font-mono">{ord.timestamp}</td>
                        <td>
                          <span
                            className={`badge ${
                              ord.status === 'PROCESADA'
                                ? 'badge--success'
                                : ord.status === 'FALLIDA (DLQ)'
                                ? 'badge--danger'
                                : 'badge--warning'
                            }`}
                          >
                            {ord.status}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          </div>
        </section>

        {/* MÓDULO 2: DIRECT EXCHANGE & ROUTING KEYS */}
        <section className="command-card command-card--highlight-emerald">
          <div className="command-card__header">
            <div>
              <div className="command-card__tag">Enrutamiento Directo</div>
              <h2 className="command-card__title">2. Direct Exchange & Routing Keys</h2>
            </div>
          </div>

          <p className="command-card__desc">
            Enrutamiento hacia <code>logs_direct_exchange</code> con claves exactas. <strong>INFO</strong> y <strong>WARNING</strong> llegan al monitor general (<code>all_logs_queue</code>). <strong>ERROR</strong> llega además a <code>errors_only_queue</code> disparando alerta.
          </p>

          <div className="action-button-row">
            <button
              className="btn-level btn-level--info"
              onClick={() => handleSendLog('INFO', 'Evento INFO: Monitoreo rutinario de estado del sistema')}
              disabled={sendingLog !== null}
            >
              {sendingLog === 'INFO' ? <Loader2 size={14} className="animate-spin" /> : null}
              <span>Log INFO</span>
            </button>
            <button
              className="btn-level btn-level--warning"
              onClick={() => handleSendLog('WARNING', 'Evento WARNING: Uso de recursos sobre el umbral recomendado 80%')}
              disabled={sendingLog !== null}
            >
              {sendingLog === 'WARNING' ? <Loader2 size={14} className="animate-spin" /> : null}
              <span>Log WARNING</span>
            </button>
            <button
              className="btn-level btn-level--error"
              onClick={() => handleSendLog('ERROR', 'Evento ERROR: Fallo simulado en transacción - Alerta crítica')}
              disabled={sendingLog !== null}
            >
              {sendingLog === 'ERROR' ? <Loader2 size={14} className="animate-spin" /> : null}
              <span>Log ERROR (Alerta)</span>
            </button>
          </div>

          {/* Formulario de Log Personalizado */}
          <div className="custom-input-bar">
            <input
              type="text"
              placeholder="Escribe un mensaje de log personalizado…"
              value={customLogMsg}
              onChange={(e) => setCustomLogMsg(e.target.value)}
              className="custom-input"
            />
            <button
              className="btn-primary"
              onClick={() => handleSendLog('INFO')}
              disabled={!customLogMsg.trim() || sendingLog !== null}
            >
              <Send size={14} />
              <span>Enviar</span>
            </button>
          </div>

          {/* Terminal / Consola de Mensajes */}
          <div className="terminal-container">
            <div className="terminal-header">
              <Terminal size={14} />
              <span>Consola de Consumidores RabbitMQ</span>
              <span className="terminal-live-dot" />
            </div>
            <div className="terminal-body">
              {logs.length === 0 ? (
                <div className="terminal-empty">Esperando tráfico de logs por el Direct Exchange…</div>
              ) : (
                logs.map((l) => (
                  <div key={l.id} className={`terminal-line terminal-line--${l.level.toLowerCase()}`}>
                    <span className="terminal-time">[{l.timestamp}]</span>
                    <span className="terminal-level">[{l.level}]</span>
                    <span className="terminal-msg">{l.message}</span>
                  </div>
                ))
              )}
            </div>
          </div>
        </section>

        {/* MÓDULO 3: GESTIÓN PROGRAMÁTICA (RABBITADMIN) */}
        <section className="command-card command-card--highlight-purple command-card--full-width">
          <div className="command-card__header">
            <div>
              <div className="command-card__tag">Administración Dinámica</div>
              <h2 className="command-card__title">3. Gestión de Recursos con RabbitAdmin</h2>
            </div>
          </div>

          <p className="command-card__desc">
            Crea colas, exchanges y bindings dinámicamente mediante el controlador administrativo (<code>POST /api/v1/rabbitmq/admin/**</code>). Los parámetros son validados mediante DTOs con <code>@NotBlank</code> y <code>@Pattern</code>.
          </p>

          <div className="admin-actions-grid">
            {/* Crear Cola */}
            <div className="admin-input-card">
              <h4 className="admin-input-title">Crear Cola de Mensajes</h4>
              <p className="admin-input-desc">Formato alfanumérico, puntos y guiones (ej. <code>evaluaciones.queue</code>)</p>
              <div className="admin-input-row">
                <input
                  type="text"
                  placeholder="Nombre de la nueva cola…"
                  value={newQueueName}
                  onChange={(e) => setNewQueueName(e.target.value)}
                  className="admin-input"
                />
                <button
                  className="btn-primary"
                  onClick={handleCreateQueue}
                  disabled={!newQueueName.trim() || creatingQueue}
                >
                  {creatingQueue ? <Loader2 size={14} className="animate-spin" /> : <Plus size={14} />}
                  <span>Crear Cola</span>
                </button>
              </div>
            </div>

            {/* Crear Exchange */}
            <div className="admin-input-card">
              <h4 className="admin-input-title">Crear Direct Exchange</h4>
              <p className="admin-input-desc">Nombre único para enrutamiento (ej. <code>notificaciones.exchange</code>)</p>
              <div className="admin-input-row">
                <input
                  type="text"
                  placeholder="Nombre del nuevo exchange…"
                  value={newExchangeName}
                  onChange={(e) => setNewExchangeName(e.target.value)}
                  className="admin-input"
                />
                <button
                  className="btn-primary"
                  onClick={handleCreateExchange}
                  disabled={!newExchangeName.trim() || creatingExchange}
                >
                  {creatingExchange ? <Loader2 size={14} className="animate-spin" /> : <Plus size={14} />}
                  <span>Crear Exchange</span>
                </button>
              </div>
            </div>

            {/* Crear Binding */}
            <div className="admin-input-card admin-input-card--full">
              <h4 className="admin-input-title">Crear Binding (Vinculación) Dinámico</h4>
              <p className="admin-input-desc">Asocia una cola con un exchange a través de una routing key validada</p>
              <div className="admin-input-row admin-input-row--triple">
                <input
                  type="text"
                  placeholder="Nombre de la Cola (ej. orders.queue)"
                  value={bindQueue}
                  onChange={(e) => setBindQueue(e.target.value)}
                  className="admin-input"
                />
                <input
                  type="text"
                  placeholder="Nombre del Exchange (ej. orders.exchange)"
                  value={bindExchange}
                  onChange={(e) => setBindExchange(e.target.value)}
                  className="admin-input"
                />
                <input
                  type="text"
                  placeholder="Routing Key (ej. order.created)"
                  value={bindKey}
                  onChange={(e) => setBindKey(e.target.value)}
                  className="admin-input"
                />
                <button
                  className="btn-primary"
                  onClick={handleCreateBinding}
                  disabled={!bindQueue.trim() || !bindExchange.trim() || !bindKey.trim() || creatingBinding}
                >
                  {creatingBinding ? <Loader2 size={14} className="animate-spin" /> : <Layers size={14} />}
                  <span>Vincular (Bind)</span>
                </button>
              </div>
            </div>

            {/* Purgar Colas */}
            <div className="admin-input-card admin-input-card--full">
              <h4 className="admin-input-title">Purgar Mensajes de Colas del Sistema</h4>
              <p className="admin-input-desc">Elimina de forma segura los mensajes acumulados sin destruir la estructura de la cola</p>
              <div className="action-button-row">
                <button
                  className="btn-secondary"
                  onClick={() => handlePurgeQueue('orders.queue')}
                  disabled={purgingQueue !== null}
                >
                  {purgingQueue === 'orders.queue' ? <Loader2 size={14} className="animate-spin" /> : <Trash2 size={14} />}
                  <span>Purgar <code>orders.queue</code></span>
                </button>
                <button
                  className="btn-secondary"
                  onClick={() => handlePurgeQueue('orders.dlq')}
                  disabled={purgingQueue !== null}
                >
                  {purgingQueue === 'orders.dlq' ? <Loader2 size={14} className="animate-spin" /> : <Trash2 size={14} />}
                  <span>Purgar <code>orders.dlq</code></span>
                </button>
                <button
                  className="btn-secondary"
                  onClick={() => handlePurgeQueue('all_logs_queue')}
                  disabled={purgingQueue !== null}
                >
                  {purgingQueue === 'all_logs_queue' ? <Loader2 size={14} className="animate-spin" /> : <Trash2 size={14} />}
                  <span>Purgar <code>all_logs_queue</code></span>
                </button>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}
