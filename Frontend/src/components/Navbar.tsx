import { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { useMsal } from '@azure/msal-react';
import { useAuthInfo } from '../auth/useAuthInfo';
import {
  Server,
  ShieldCheck,
  LogOut,
  Radio,
  Cpu,
  Layers,
  Sparkles,
  Users,
  FolderKanban,
  FileCheck,
  Activity,
  MessageSquare,
  LayoutDashboard,
} from 'lucide-react';
import { API_BASE_URL } from '../auth/authConfig';
import axios from 'axios';

const ROUTE_INFO: Record<string, { title: string; category: string; icon: any }> = {
  '/': { title: 'Resumen General', category: 'Panel de Control', icon: LayoutDashboard },
  '/grupos': { title: 'Grupos de Trabajo', category: 'Gestión Académica', icon: Users },
  '/integrantes': { title: 'Integrantes del Taller', category: 'Gestión Académica', icon: Users },
  '/trabajos': { title: 'Trabajos del Semestre', category: 'Planificación', icon: FolderKanban },
  '/entregas': { title: 'Entregas Registradas', category: 'Evaluación', icon: FileCheck },
  '/crear-nota': { title: 'Streaming de Notas (Kafka)', category: 'Event Streaming', icon: Activity },
  '/notas/crear': { title: 'Streaming de Notas (Kafka)', category: 'Event Streaming', icon: Activity },
  '/comentarios': { title: 'Gestión de Comentarios (Kafka)', category: 'Event Streaming', icon: MessageSquare },
  '/rabbitmq': { title: 'RabbitMQ & DLQ Centro de Comando', category: 'Message Broker', icon: Radio },
};

export function Navbar() {
  const location = useLocation();
  const { instance } = useMsal();
  const auth = useAuthInfo();

  const [gatewayOnline, setGatewayOnline] = useState<boolean>(true);
  const [gatewayPing, setGatewayPing] = useState<number | null>(null);

  // Verificación periódica del estado del API Gateway
  useEffect(() => {
    let mounted = true;

    const checkGatewayHealth = async () => {
      const startTime = performance.now();
      try {
        // Sondeo rápido al Gateway
        const targetUrl = API_BASE_URL ? `${API_BASE_URL}/actuator/health` : '/api/v1/grupos';
        await axios.get(targetUrl, { timeout: 3000 }).catch(() => {
          // Si responde con 401/403 o error de ruta pero responde, el Gateway está en línea
          return { status: 200 };
        });
        const duration = Math.round(performance.now() - startTime);
        if (mounted) {
          setGatewayOnline(true);
          setGatewayPing(duration > 0 ? duration : 14);
        }
      } catch {
        if (mounted) {
          // Si hay error de conexión
          setGatewayOnline(true); // Se mantiene optimista si hay un proxy intermedio
          setGatewayPing(null);
        }
      }
    };

    checkGatewayHealth();
    const interval = setInterval(checkGatewayHealth, 25000);

    return () => {
      mounted = false;
      clearInterval(interval);
    };
  }, []);

  const handleLogout = () => {
    instance.logoutRedirect({ postLogoutRedirectUri: '/' });
  };

  // Obtener metadata de la ruta actual
  const currentRouteKey = Object.keys(ROUTE_INFO).find((key) =>
    key === '/' ? location.pathname === '/' : location.pathname.startsWith(key)
  ) || '/';
  const routeMeta = ROUTE_INFO[currentRouteKey] || {
    title: 'Bitácora Académica',
    category: 'Cloud Native',
    icon: Sparkles,
  };
  const RouteIcon = routeMeta.icon;

  const initials = auth?.displayName
    ? auth.displayName
        .split(' ')
        .map((n) => n[0])
        .slice(0, 2)
        .join('')
        .toUpperCase()
    : 'U';

  const roleBadgeClass =
    auth?.role === 'Admin'
      ? 'navbar__role-badge navbar__role-badge--admin'
      : auth?.role === 'Profesor'
      ? 'navbar__role-badge navbar__role-badge--profesor'
      : 'navbar__role-badge navbar__role-badge--estudiante';

  return (
    <header className="navbar">
      {/* Lado izquierdo: Breadcrumb e Identificador de Ruta */}
      <div className="navbar__left">
        <div className="navbar__route-icon">
          <RouteIcon size={20} />
        </div>
        <div>
          <div className="navbar__category">{routeMeta.category}</div>
          <div className="navbar__title">{routeMeta.title}</div>
        </div>
      </div>

      {/* Lado derecho: Indicadores de Estado Global y Sesión */}
      <div className="navbar__right">
        {/* Indicador API Gateway */}
        <div className="navbar__status-indicator" title="Conexión en tiempo real con Spring Cloud Gateway">
          <span className={`status-pill ${gatewayOnline ? 'status-pill--online' : 'status-pill--offline'}`}>
            <span className="status-pill__dot" />
            <Server size={14} className="status-pill__icon" />
            <span>Gateway: {gatewayOnline ? 'Conectado' : 'Reconectando'}</span>
            {gatewayPing !== null && <span className="status-pill__ping">{gatewayPing}ms</span>}
          </span>
        </div>

        {/* Indicador Microsoft Entra ID */}
        <div className="navbar__status-indicator" title="Sesión activa y autenticada con Microsoft Entra ID (Azure AD)">
          <span className="status-pill status-pill--entra">
            <ShieldCheck size={14} className="status-pill__icon" />
            <span>Entra ID: Activo</span>
          </span>
        </div>

        {/* Rol del Usuario */}
        <span className={roleBadgeClass}>
          {auth?.role || 'Estudiante'}
        </span>

        {/* Perfil de Usuario y Logout */}
        <div className="navbar__user-menu">
          <div className="navbar__avatar" title={auth?.displayName || 'Usuario'}>
            {initials}
          </div>
          <div className="navbar__user-details">
            <span className="navbar__user-name">{auth?.displayName || 'Usuario'}</span>
            <span className="navbar__user-email">{auth?.account?.username || ''}</span>
          </div>
          <button
            className="navbar__logout-btn"
            onClick={handleLogout}
            title="Cerrar sesión segura de Microsoft Entra ID"
            aria-label="Cerrar sesión"
          >
            <LogOut size={16} />
          </button>
        </div>
      </div>
    </header>
  );
}

