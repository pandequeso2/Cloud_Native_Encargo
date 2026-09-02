import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { MsalProvider } from '@azure/msal-react';
import { EventType } from '@azure/msal-browser';
import './styles/global.css';
import App from './App.tsx';
import { msalInstance } from './auth/msalInstance';

// La instancia debe estar inicializada antes de renderizar el árbol.
msalInstance.initialize().then(() => {
  // Maneja el redirect de vuelta desde Entra ID después del login (si se
  // usa el flujo redirect en vez de popup en algún punto de la app).
  msalInstance.handleRedirectPromise().catch((error) => {
    console.error('Error procesando el redirect de autenticación:', error);
  });

  createRoot(document.getElementById('root')!).render(
    <StrictMode>
      <MsalProvider instance={msalInstance}>
        <BrowserRouter>
          <App />
        </BrowserRouter>
      </MsalProvider>
    </StrictMode>,
  );
});

// Solo para dejar constancia de los eventos de MSAL relevantes durante el
// desarrollo (útil al depurar el flujo de login/logout).
msalInstance.addEventCallback((event) => {
  if (event.eventType === EventType.ACQUIRE_TOKEN_FAILURE) {
    console.warn('[MSAL]', event.eventType, event.error);
  }
});
