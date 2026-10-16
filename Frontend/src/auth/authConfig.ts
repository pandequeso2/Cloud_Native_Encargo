import type { Configuration, RedirectRequest } from '@azure/msal-browser';
import { LogLevel } from '@azure/msal-browser';

// Valores hardcodeados a propósito (decisión del equipo) para no tener que
// recrear el .env cada vez que se cambia de computador. Como contrapartida,
// estos IDs quedan visibles en el repositorio de GitHub. No son secretos
// como una contraseña (un tenant/client ID no permite autenticarse por sí
// solo), pero sí identifican públicamente el tenant de Entra ID del equipo.

// Valores actualizados al entorno Microsoft Entra ID
// Permite leer variables de entorno Vite o usar los valores por defecto de Azure
const clientId = import.meta.env.VITE_ENTRA_CLIENT_ID || 'be1f94f2-51ba-4a74-ab8d-b258e2123e6e'; // ID de cloudnative-frontend (SPA)
const tenantId = import.meta.env.VITE_ENTRA_TENANT_ID || '551dc2ab-db79-43ed-97de-ec90a21f3e0c'; // ID de tu Tenant (Directorio)
const apiClientId = import.meta.env.VITE_ENTRA_API_CLIENT_ID || 'cdde4875-2f04-4a9c-b27c-2e1ad98c9366'; // ID de cloudnative-backend (API)

// Detecta automáticamente en qué IP o dominio está corriendo la página web
const redirectUri = window.location.origin;

// Asegúrate de poner la IP pública de tu instancia BACKEND (la del Gateway) aquí
//const apiBaseUrl = import.meta.env.VITE_API_BASE_URL && !import.meta.env.VITE_API_BASE_URL.includes("localhost") 
//    ? import.meta.env.VITE_API_BASE_URL 
//    : 'http://98.95.159.57:8095'; 

// ✅ CÓDIGO NUEVO:
// Usa la variable del .env o cae a localhost si no está definida
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8095';

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