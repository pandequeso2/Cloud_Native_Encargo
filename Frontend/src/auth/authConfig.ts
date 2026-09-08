import type { Configuration, RedirectRequest } from '@azure/msal-browser';
import { LogLevel } from '@azure/msal-browser';

// Valores hardcodeados a propósito (decisión del equipo) para no tener que
// recrear el .env cada vez que se cambia de computador. Como contrapartida,
// estos IDs quedan visibles en el repositorio de GitHub. No son secretos
// como una contraseña (un tenant/client ID no permite autenticarse por sí
// solo), pero sí identifican públicamente el tenant de Entra ID del equipo.
const clientId = 'be1f94f2-51ba-4a74-ab8d-b258e2123e6e';
const tenantId = '551dc2ab-db79-43ed-97de-ec90a21f3e0c';
const apiClientId = 'cdde4875-2f04-4a9e-b27c-2e1ad93c9366';
const apiBaseUrl = 'http://localhost:8095';
const redirectUri = 'http://localhost:5173';

export const msalConfig: Configuration = {
  auth: {
    clientId,
    authority: `https://login.microsoftonline.com/${tenantId}`,
    redirectUri,
    postLogoutRedirectUri: '/',
  },
  cache: {
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

export const loginRequest: RedirectRequest = {
  scopes: ['openid', 'profile'],
};

export const apiRequest: RedirectRequest = {
  scopes: [`api://${apiClientId}/access_as_user`],
};

export const API_BASE_URL = apiBaseUrl;