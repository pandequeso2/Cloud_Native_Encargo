import { useIsAuthenticated, useMsal } from '@azure/msal-react';
import { Navigate } from 'react-router-dom';
import { loginRequest } from '../auth/authConfig';
import { BookOpen, ShieldCheck, Sparkles } from 'lucide-react';

export function Login() {
  const { instance } = useMsal();
  const isAuthenticated = useIsAuthenticated();

  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  const handleLogin = () => {
    instance.loginRedirect(loginRequest);
  };

  return (
    <div className="login-screen">
      <div className="login-card">
        <div className="login-card__brand">
          <div className="login-card__icon-box">
            <BookOpen size={28} />
          </div>
          <div>
            <div className="login-card__mark">Bitácora</div>
            <div className="login-card__submark">Cloud Native Architecture</div>
          </div>
        </div>

        <div className="login-card__security-badge">
          <ShieldCheck size={14} />
          <span>Autenticación Microsoft Entra ID (Azure AD)</span>
        </div>

        <p className="login-card__tagline">
          Plataforma centralizada de seguimiento académico, mensajería Kafka Streams y colas resilientes RabbitMQ. Inicia sesión con tu cuenta institucional para acceder.
        </p>

        <button className="login-card__button" onClick={handleLogin}>
          {/* Ícono de Microsoft 4 cuadrados */}
          <svg width="20" height="20" viewBox="0 0 21 21" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="1" y="1" width="9" height="9" fill="#F25022"/>
            <rect x="11" y="1" width="9" height="9" fill="#7FBA00"/>
            <rect x="1" y="11" width="9" height="9" fill="#00A4EF"/>
            <rect x="11" y="11" width="9" height="9" fill="#FFB900"/>
          </svg>
          <span>Iniciar sesión con Microsoft</span>
        </button>

        <div className="login-card__footer">
          <span>Duoc UC · Escuela de Informática y Telecomunicaciones</span>
        </div>
      </div>
    </div>
  );
}

export default Login;
