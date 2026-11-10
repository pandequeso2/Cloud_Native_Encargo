import type { ReactNode } from 'react';
import { NavLink } from 'react-router-dom';
import { useMsal } from '@azure/msal-react';
import { useAuthInfo } from '../auth/useAuthInfo';
import { Navbar } from './Navbar';
import {
  LayoutDashboard,
  Users,
  UserCheck,
  FolderKanban,
  FileCheck,
  Activity,
  MessageSquare,
  Radio,
  BookOpen,
  LogOut,
  Shield,
  Layers,
} from 'lucide-react';

const NAV_ITEMS = [
  { to: '/', label: 'Resumen', end: true, icon: LayoutDashboard },
  { to: '/grupos', label: 'Grupos', icon: Users },
  { to: '/integrantes', label: 'Integrantes', icon: UserCheck },
  { to: '/trabajos', label: 'Trabajos', icon: FolderKanban },
  { to: '/entregas', label: 'Entregas', icon: FileCheck },
  { to: '/crear-nota', label: 'Streaming Notas (Kafka)', icon: Activity },
  { to: '/comentarios', label: 'Comentarios (Kafka)', icon: MessageSquare },
  { to: '/rabbitmq', label: 'RabbitMQ & DLQ', icon: Radio },
];

export function Layout({ children }: { children: ReactNode }) {
  const { instance } = useMsal();
  const auth = useAuthInfo();

  const handleLogout = () => {
    instance.logoutRedirect({ postLogoutRedirectUri: '/' });
  };

  const initials = auth?.displayName
    ? auth.displayName
        .split(' ')
        .map((n) => n[0])
        .slice(0, 2)
        .join('')
        .toUpperCase()
    : 'U';

  return (
    <div className="shell">
      {/* Sidebar / Rail Moderno */}
      <aside className="rail">
        <div className="rail__brand">
          <div className="rail__brand-icon">
            <BookOpen size={22} />
          </div>
          <div className="rail__brand-text">
            <span className="rail__brand-name">Bitácora</span>
            <span className="rail__brand-subtitle">Cloud Native Encargo</span>
          </div>
        </div>

        <div className="rail__section-title">Navegación Principal</div>

        <nav className="rail__nav">
          {NAV_ITEMS.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.end}
                className={({ isActive }) =>
                  isActive ? 'rail__link rail__link--active' : 'rail__link'
                }
              >
                <Icon size={18} className="rail__link-icon" />
                <span>{item.label}</span>
              </NavLink>
            );
          })}
        </nav>

        {/* Footer del Sidebar con información de Entra ID */}
        {auth && (
          <div className="rail__user">
            <div className="rail__user-card">
              <div className="rail__user-avatar">{initials}</div>
              <div className="rail__user-info">
                <div className="rail__user-name">{auth.displayName}</div>
                <div className="rail__user-role">
                  <Shield size={12} />
                  <span>{auth.role}</span>
                </div>
              </div>
            </div>
            <button className="rail__logout" onClick={handleLogout}>
              <LogOut size={15} />
              <span>Cerrar sesión</span>
            </button>
          </div>
        )}
      </aside>

      {/* Contenedor Principal: Navbar Superior + Contenido */}
      <div className="main-wrapper">
        <Navbar />
        <main className="content">{children}</main>
      </div>
    </div>
  );
}
