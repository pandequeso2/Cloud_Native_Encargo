import type { ReactNode } from 'react';
import { useIsAuthenticated, useMsal } from '@azure/msal-react';
import { InteractionStatus } from '@azure/msal-browser';
import { Navigate } from 'react-router-dom';

/**
 * Equivalente al MsalGuard de msal-angular: bloquea el acceso a rutas
 * hijas mientras no exista una cuenta autenticada. Si MSAL todavía está
 * resolviendo el redirect/login en curso, muestra un estado de carga en
 * vez de expulsar prematuramente al usuario a /login.
 */
export function ProtectedRoute({ children }: { children: ReactNode }) {
  const isAuthenticated = useIsAuthenticated();
  const { inProgress } = useMsal();

  if (inProgress !== InteractionStatus.None) {
    return <div className="state-panel">Verificando sesión…</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
}
