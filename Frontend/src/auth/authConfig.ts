import type { Configuration, RedirectRequest } from '@azure/msal-browser';
import { LogLevel } from '@azure/msal-browser';

// Valores hardcodeados a propósito (decisión del equipo) para no tener que
// recrear el .env cada vez que se cambia de computador. Como contrapartida,
// estos IDs quedan visibles en el repositorio de GitHub. No son secretos
// como una contraseña (un tenant/client ID no permite autenticarse por sí
// solo), pero sí identifican públicamente el tenant de Entra ID del equipo.
// Valores actualizados a tu entorno CloudNativeApi
const clientId = '47099009-530d-4953-9c13-8453c7369262'; // ID de bitacora-frontend-spa
const tenantId = '120aafaf-ea47-4c03-b1b6-68ef7c7c9dce'; // ID de tu Tenant (Directorio)
const apiClientId = 'aec497bb-c720-40cc-9e4f-87f811226d6f'; // ID de bitacora-backend-api
// Detecta automáticamente en qué IP o dominio está corriendo la página web
const redirectUri = window.location.origin;

// Asegúrate de poner la IP pública de tu instancia BACKEND (la del Gateway) aquí
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL && !import.meta.env.VITE_API_BASE_URL.includes("localhost") 
    ? import.meta.env.VITE_API_BASE_URL 
    : 'http://98.95.159.578095'; // <- Reemplaza esto con la IP de tu EC2 Backend
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