import type { Configuration, RedirectRequest } from '@azure/msal-browser';
import { LogLevel } from '@azure/msal-browser';

// Variables de entorno (definir en .env, ver .env.example).
// VITE_ENTRA_CLIENT_ID: Application (client) ID del App Registration del SPA.
// VITE_ENTRA_TENANT_ID: Directory (tenant) ID.
// VITE_ENTRA_API_CLIENT_ID: client-id del App Registration del backend
//   (el mismo que usa el Gateway como ENTRA_AUDIENCE).
const clientId = import.meta.env.VITE_ENTRA_CLIENT_ID as string;
const tenantId = import.meta.env.VITE_ENTRA_TENANT_ID as string;
const apiClientId = import.meta.env.VITE_ENTRA_API_CLIENT_ID as string;

export const msalConfig: Configuration = {
  auth: {
    clientId,
    authority: `https://login.microsoftonline.com/${tenantId}`,
    // Con flujo de redirect, esta URL debe ser la raíz de la app (donde
    // React Router y ProtectedRoute puedan procesar el resultado), no una
    // página en blanco separada como se necesitaba para popups.
    redirectUri: import.meta.env.VITE_REDIRECT_URI ?? window.location.origin,
    postLogoutRedirectUri: '/',
  },
  cache: {
    // sessionStorage evita que el token sobreviva entre pestañas/sesiones distintas.
    cacheLocation: 'sessionStorage',
  },
  system: {
    loggerOptions: {
      loggerCallback: (level, message, containsPii) => {
        if (containsPii) return;
        if (level === LogLevel.Error) console.error(message);
      },
    },
  },
};

// Scope de login: solo identidad básica.
export const loginRequest: RedirectRequest = {
  scopes: ['openid', 'profile'],
};

// Scope para llamar al Gateway (backend). Debe coincidir con el scope
// expuesto en "Expose an API" del App Registration del backend.
export const apiRequest: RedirectRequest = {
  scopes: [`api://${apiClientId}/access_as_user`],
};