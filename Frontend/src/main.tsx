import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { MsalProvider } from '@azure/msal-react';
import { EventType } from '@azure/msal-browser';
import './styles/global.css';
import App from './App.tsx';
import { msalInstance } from './auth/msalInstance';

// La instancia debe estar inicializada antes de renderizar el árbol.
// No llamamos handleRedirectPromise() aquí: MsalProvider ya lo hace
// automáticamente al montarse. Llamarlo dos veces (aquí y en el Provider)
// provoca "no_token_request_cache_error", porque la segunda llamada no
// encuentra el estado temporal que la primera ya consumió.
msalInstance
  .initialize()
  .then(() => {
    // Si ya hay una cuenta en caché, la fijamos como cuenta activa al iniciar.
    const accounts = msalInstance.getAllAccounts();
    if (accounts.length > 0 && !msalInstance.getActiveAccount()) {
      msalInstance.setActiveAccount(accounts[0]);
    }

    createRoot(document.getElementById('root')!).render(
      <StrictMode>
        <MsalProvider instance={msalInstance}>
          <BrowserRouter>
            <App />
          </BrowserRouter>
        </MsalProvider>
      </StrictMode>,
    );
  })
  .catch((error) => {
    console.error('[MSAL] Error al inicializar MSAL:', error);
    createRoot(document.getElementById('root')!).render(
      <div style={{ padding: '2rem', fontFamily: 'system-ui, sans-serif' }}>
        <h2 style={{ color: '#d32f2f' }}>Error al iniciar la aplicación</h2>
        <p>Ocurrió un error al inicializar el proveedor de autenticación con Microsoft Entra ID.</p>
        <pre style={{ background: '#f5f5f5', padding: '1rem', borderRadius: '4px', overflowX: 'auto' }}>
          {String(error)}
        </pre>
      </div>,
    );
  });

// Solo para dejar constancia de los eventos de MSAL relevantes durante el
// desarrollo (útil al depurar el flujo de login/logout).
msalInstance.addEventCallback((event) => {
  if (event.eventType === EventType.ACQUIRE_TOKEN_FAILURE) {
    console.warn('[MSAL]', event.eventType, event.error);
  }
});