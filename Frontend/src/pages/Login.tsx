import { useIsAuthenticated, useMsal } from '@azure/msal-react';
import { Navigate } from 'react-router-dom';
import { loginRequest } from '../auth/authConfig';

export function Login() {
  const { instance } = useMsal();
  const isAuthenticated = useIsAuthenticated();

  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  // loginRedirect navega la pestaña completa a Microsoft y vuelve; no hay
  // popup que monitorear, así que no hay estado de carga que gestionar acá:
  // la página se va antes de que este componente vuelva a renderizar.
  const handleLogin = () => {
    instance.loginRedirect(loginRequest);
  };

  return (
    <div className="login-screen">
      <div className="login-card">
        <div className="login-card__mark">Bitácora</div>
        <p className="login-card__tagline">
          Registro de grupos, trabajos y entregas del taller. Inicia sesión con tu
          cuenta institucional para continuar.
        </p>
        <button className="login-card__button" onClick={handleLogin}>
          Iniciar sesión con Microsoft
        </button>
      </div>
    </div>
  );
}