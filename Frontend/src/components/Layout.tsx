import type { ReactNode } from 'react';
import { NavLink } from 'react-router-dom';
import { useMsal } from '@azure/msal-react';
import { useAuthInfo } from '../auth/useAuthInfo';

const NAV_ITEMS = [
  { to: '/', label: 'Resumen', end: true },
  { to: '/grupos', label: 'Grupos' },
  { to: '/integrantes', label: 'Integrantes' },
  { to: '/trabajos', label: 'Trabajos' },
  { to: '/entregas', label: 'Entregas' },
];

export function Layout({ children }: { children: ReactNode }) {
  const { instance } = useMsal();
  const auth = useAuthInfo();

  const handleLogout = () => {
    instance.logoutRedirect({ postLogoutRedirectUri: '/' });
  };

  return (
    <div className="shell">
      <aside className="rail">
        <div className="rail__brand">
          Bitácora
          <span>Seguimiento de encargos</span>
        </div>

        <nav className="rail__nav">
          {NAV_ITEMS.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
              className={({ isActive }) =>
                isActive ? 'rail__link rail__link--active' : 'rail__link'
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>

        {auth && (
          <div className="rail__user">
            <div className="rail__user-name">{auth.displayName}</div>
            <div className="rail__user-roles">
              {auth.roles.length > 0 ? auth.roles.join(' · ') : 'sin rol asignado'}
            </div>
            <button className="rail__logout" onClick={handleLogout}>
              Cerrar sesión
            </button>
          </div>
        )}
      </aside>

      <main className="content">{children}</main>
    </div>
  );
}