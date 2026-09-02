import { useState } from 'react';
import { useIsAuthenticated, useMsal } from '@azure/msal-react';
import { Navigate } from 'react-router-dom';
import { loginRequest } from '../auth/authConfig';

export function Login() {
  const { instance } = useMsal();
  const isAuthenticated = useIsAuthenticated();
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  const handleLogin = async () => {
    setError(null);
    setLoading(true);
    try {
      await instance.loginPopup(loginRequest);
    } catch {
      setError('No se pudo iniciar sesión. Intenta nuevamente.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-screen">
      <div className="login-card">
        <div className="login-card__mark">Bitácora</div>
        <p className="login-card__tagline">
          Registro de grupos, trabajos y entregas del taller. Inicia sesión con tu
          cuenta institucional para continuar.
        </p>
        <button className="login-card__button" onClick={handleLogin} disabled={loading}>
          {loading ? 'Abriendo sesión…' : 'Iniciar sesión con Microsoft'}
        </button>
        {error && <p className="login-card__error">{error}</p>}
      </div>
    </div>
  );
}
