import { useState, useEffect } from 'react';
import { rabbitmqApi } from '../api/rabbitmqService';
import type { RabbitStatusResponse } from '../api/rabbitmqService';
import '../styles/rabbitmq.css';

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
  const [actionFeedback, setActionFeedback] = useState<string | null>(null);

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

  const showFeedback = (msg: string) => {
    setActionFeedback(msg);
    setTimeout(() => setActionFeedback(null), 4000);
  };

  // 1. Send Log (Direct Exchange)
  const handleSendLog = async (level: 'INFO' | 'WARNING' | 'ERROR', defaultMsg?: string) => {
    const text = defaultMsg || customLogMsg || 'Mensaje de evento de prueba';
    try {
      const res = await rabbitmqApi.sendLog(level, text);
      const newEntry: LogMessage = {
        id: Math.random().toString(36).substring(7),
        level,
        message: text,
        timestamp: new Date().toLocaleTimeString(),
      };
      setLogs((prev) => [newEntry, ...prev.slice(0, 19)]);
      showFeedback(`✔ ${res}`);
      if (!defaultMsg) setCustomLogMsg('');
    } catch (err: any) {
      showFeedback(`❌ Error al enviar log: ${err?.message || 'Fallo de conexión'}`);
    }
  };

  // 2. Send Order (DLQ Simulation)
  const handleSendOrder = async (customerName: string) => {
    const orderId = `ORD-${Date.now().toString().slice(-6)}`;
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

      // Simulate consumer result evaluation after 2.5s
      setTimeout(() => {
        const isSuccess = Math.random() > 0.4;
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
      }, 2500);
    } catch (err: any) {
      showFeedback(`❌ Error al enviar orden: ${err?.message || 'Fallo de conexión'}`);
    }
  };

  // 3. RabbitAdmin Actions
  const handleCreateQueue = async () => {
    if (!newQueueName) return;
    try {
      const res = await rabbitmqApi.createQueue(newQueueName);
      showFeedback(res);
      setNewQueueName('');
    } catch (err: any) {
      showFeedback(`❌ ${err?.message || 'Fallo al crear cola'}`);
    }
  };

  const handleCreateExchange = async () => {
    if (!newExchangeName) return;
    try {
      const res = await rabbitmqApi.createExchange(newExchangeName);
      showFeedback(res);
      setNewExchangeName('');
    } catch (err: any) {
      showFeedback(`❌ ${err?.message || 'Fallo al crear exchange'}`);
    }
  };

  const handleCreateBinding = async () => {
    if (!bindQueue || !bindExchange || !bindKey) return;
    try {
      const res = await rabbitmqApi.createBinding(bindQueue, bindExchange, bindKey);
      showFeedback(res);
      setBindQueue('');
      setBindExchange('');
      setBindKey('');
    } catch (err: any) {
      showFeedback(`❌ ${err?.message || 'Fallo al crear binding'}`);
    }
  };

  const handlePurgeQueue = async (qName: string) => {
    try {
      const res = await rabbitmqApi.purgeQueue(qName);
      showFeedback(res);
      checkStatus();
    } catch (err: any) {
      showFeedback(`❌ ${err?.message || 'Fallo al purgar cola'}`);
    }
  };

  // 4. Listener Control
  const handleToggleListener = async () => {
    try {
      if (listenerPaused) {
        await rabbitmqApi.resumeListener('order-listener');
        setListenerPaused(false);
        showFeedback('▶ Listener "order-listener" REANUDADO');
      } else {
        await rabbitmqApi.pauseListener('order-listener');
        setListenerPaused(true);
        showFeedback('⏸ Listener "order-listener" PAUSADO');
      }
    } catch (err: any) {
      showFeedback(`❌ Error controlando listener: ${err?.message}`);
    }
  };

  return (
    <div className="rabbitmq-page">
      {/* Header */}
      <div className="rabbitmq-header">
        <div>
          <h1>RabbitMQ Cluster & Event Broker</h1>
          <p className="rabbitmq-subtitle">
            Monitoreo en tiempo real de Mensajería, Direct Exchanges, Dead Letter Queues (DLQ), y RabbitAdmin.
          </p>
        </div>
        <div className="rabbitmq-status-group">
          <span className={`status-badge ${status ? 'status-badge--online' : 'status-badge--offline'}`}>
            {statusLoading ? 'Cargando...' : status ? '🟢 RabbitMQ Online' : '🔴 RabbitMQ Offline'}
          </span>
          <a
            href="http://localhost:15672"
            target="_blank"
            rel="noreferrer"
            className="btn-management-ui"
          >
            💻 Management UI (:15672)
          </a>
        </div>
      </div>

      {actionFeedback && <div className="feedback-banner">{actionFeedback}</div>}

      {/* Stats Cards Banner */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-value">{stats.total}</div>
          <div className="stat-label">Total Órdenes / Entregas</div>
        </div>
        <div className="stat-card stat-card--success">
          <div className="stat-value">{stats.success}</div>
          <div className="stat-label">Procesadas Exitosamente</div>
        </div>
        <div className="stat-card stat-card--error">
          <div className="stat-value">{stats.failed}</div>
          <div className="stat-label">En Cuarentena DLQ</div>
        </div>
        <div className="stat-card stat-card--info">
          <div className="stat-value">{status?.ordersQueueMessages ?? 0}</div>
          <div className="stat-label">Mensajes en orders.queue</div>
        </div>
      </div>

      {/* Grid Layout for Main Content */}
      <div className="rabbitmq-grid">
        {/* SECTION 1: DIRECT EXCHANGE LOGGING */}
        <div className="rabbitmq-card">
          <h2>1. Direct Exchange & Routing Keys</h2>
          <p className="card-desc">
            Prueba de enrutamiento con <code>DirectExchange</code>. Los logs de <strong>INFO</strong> y{' '}
            <strong>WARNING</strong> van a <code>all_logs_queue</code>. Los de <strong>ERROR</strong> van a ambas colas y disparan una alerta crítica.
          </p>

          <div className="button-group">
            <button
              className="btn btn--info"
              onClick={() => handleSendLog('INFO', 'Hola, este es un log de prueba INFO para monitoreo del sistema')}
            >
              Enviar Log INFO
            </button>
            <button
              className="btn btn--warning"
              onClick={() => handleSendLog('WARNING', 'Este es un log de advertencia WARNING: Se alcanzó el 80% de uso de memoria')}
            >
              Enviar Log WARNING
            </button>
            <button
              className="btn btn--error"
              onClick={() => handleSendLog('ERROR', 'Este es un Error simulado: No se pudo procesar la orden de entrega')}
            >
              Enviar Log ERROR (Alerta)
            </button>
          </div>

          <div className="custom-log-input">
            <input
              type="text"
              placeholder="Mensaje de log personalizado..."
              value={customLogMsg}
              onChange={(e) => setCustomLogMsg(e.target.value)}
              className="input-field"
            />
            <button className="btn btn--secondary" onClick={() => handleSendLog('INFO')}>
              Enviar
            </button>
          </div>

          <h3>Terminal de Consola de Mensajes:</h3>
          <div className="log-console">
            {logs.length === 0 ? (
              <span className="log-placeholder">Esperando envíos de las colas de mensajes...</span>
            ) : (
              logs.map((l) => (
                <div key={l.id} className={`log-line log-line--${l.level.toLowerCase()}`}>
                  <span className="log-time">[{l.timestamp}]</span>
                  <span className="log-level font-bold">[{l.level}]</span>
                  <span className="log-msg">{l.message}</span>
                </div>
              ))
            )}
          </div>
        </div>

        {/* SECTION 2: RESILIENT DLX / DLQ SIMULATION */}
        <div className="rabbitmq-card">
          <div className="card-header-flex">
            <h2>2. Resiliencia, Reintentos y DLQ</h2>
            <button
              className={`btn btn--sm ${listenerPaused ? 'btn--success' : 'btn--warning'}`}
              onClick={handleToggleListener}
            >
              {listenerPaused ? '▶ Reanudar Listener' : '⏸ Pausar Listener'}
            </button>
          </div>
          <p className="card-desc">
            Las órdenes pasan a <code>orders.queue</code> con TTL (30s) y reintentos automáticos. Si fallan definitivamente, son enviadas por el <strong>Dead Letter Exchange (DLX)</strong> a la <code>orders.dlq</code>.
          </p>

          <div className="button-group">
            <button className="btn btn--primary" onClick={() => handleSendOrder('Estudiante Carlos Ramirez')}>
              Enviar Entrega Carlos Ramirez
            </button>
            <button className="btn btn--primary" onClick={() => handleSendOrder('Estudiante Valentina Soto')}>
              Enviar Entrega Valentina Soto
            </button>
            <button className="btn btn--primary" onClick={() => handleSendOrder('Estudiante Andres Muñoz')}>
              Enviar Entrega Andrés Muñoz 
            </button>
          </div>

          <h3>Historial de Órdenes / Entregas:</h3>
          <div className="orders-table-wrapper">
            {orders.length === 0 ? (
              <p className="empty-msg">No hay órdenes procesadas aún....</p>
            ) : (
              <table className="custom-table">
                <thead>
                  <tr>
                    <th>ID Orden</th>
                    <th>Estudiante</th>
                    <th>Hora</th>
                    <th>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  {orders.map((ord) => (
                    <tr key={ord.id}>
                      <td className="font-bold">{ord.id}</td>
                      <td>{ord.customer}</td>
                      <td className="text-muted">{ord.timestamp}</td>
                      <td>
                        <span
                          className={`badge ${
                            ord.status === 'PROCESADA'
                              ? 'badge--success'
                              : ord.status === 'FALLIDA (DLQ)'
                              ? 'badge--error'
                              : 'badge--pending'
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
      </div>

      {/* SECTION 3: RABBITADMIN DYNAMIC MANAGEMENT */}
      <div className="rabbitmq-card margin-top-lg">
        <h2>3. Gestión Programática de Recursos (RabbitAdmin)</h2>
        <p className="card-desc">
          Permite crear y gestionar colas de mensajes, exchanges y bindings dinámicamente en tiempo de ejecución sin reiniciar la aplicación.
        </p>

        <div className="admin-grid">
          {/* Create Queue */}
          <div className="admin-box">
            <h4>Crear Cola de mensajes dinamica</h4>
            <div className="form-inline">
              <input
                type="text"
                placeholder="Nombre de la cola..."
                value={newQueueName}
                onChange={(e) => setNewQueueName(e.target.value)}
                className="input-field"
              />
              <button className="btn btn--primary" onClick={handleCreateQueue}>
                Crear Cola de mensajes
              </button>
            </div>
          </div>

          {/* Create Exchange */}
          <div className="admin-box">
            <h4>Crear Exchange Dinámico</h4>
            <div className="form-inline">
              <input
                type="text"
                placeholder="Nombre del exchange..."
                value={newExchangeName}
                onChange={(e) => setNewExchangeName(e.target.value)}
                className="input-field"
              />
              <button className="btn btn--primary" onClick={handleCreateExchange}>
                Crear Exchange
              </button>
            </div>
          </div>

          {/* Create Binding */}
          <div className="admin-box admin-box--full">
            <h4>Crear Binding Dinámico</h4>
            <div className="form-inline">
              <input
                type="text"
                placeholder="Nombre de la Cola"
                value={bindQueue}
                onChange={(e) => setBindQueue(e.target.value)}
                className="input-field"
              />
              <input
                type="text"
                placeholder="Nombre del Exchange"
                value={bindExchange}
                onChange={(e) => setBindExchange(e.target.value)}
                className="input-field"
              />
              <input
                type="text"
                placeholder="Routing Key"
                value={bindKey}
                onChange={(e) => setBindKey(e.target.value)}
                className="input-field"
              />
              <button className="btn btn--primary" onClick={handleCreateBinding}>
                Vincular (Bind)
              </button>
            </div>
          </div>

          {/* Quick Purge */}
          <div className="admin-box admin-box--full">
            <h4>Purgar Colas del Sistema</h4>
            <div className="button-group">
              <button className="btn btn--secondary" onClick={() => handlePurgeQueue('orders.queue')}>
                Purgar <code>orders.queue</code>
              </button>
              <button className="btn btn--secondary" onClick={() => handlePurgeQueue('orders.dlq')}>
                Purgar <code>orders.dlq</code>
              </button>
              <button className="btn btn--secondary" onClick={() => handlePurgeQueue('all_logs_queue')}>
                Purgar <code>all_logs_queue</code>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
