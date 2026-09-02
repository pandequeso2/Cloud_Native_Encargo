import axios from 'axios';
import { InteractionRequiredAuthError } from '@azure/msal-browser';
import { msalInstance } from '../auth/msalInstance';
import { apiRequest } from '../auth/authConfig';

const baseURL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8095';

export const httpClient = axios.create({ baseURL });

/**
 * Equivalente al MsalInterceptor de msal-angular: antes de cada request,
 * intenta obtener el access token en silencio (desde caché o refresh
 * silencioso vía iframe). Si la sesión expiró o requiere interacción del
 * usuario (MFA, consentimiento, etc.), cae a un popup de login.
 */
httpClient.interceptors.request.use(async (config) => {
  const account = msalInstance.getActiveAccount();
  if (!account) {
    throw new Error('No hay una sesión activa. Inicia sesión antes de llamar al backend.');
  }

  try {
    const result = await msalInstance.acquireTokenSilent({
      ...apiRequest,
      account,
    });
    config.headers.Authorization = `Bearer ${result.accessToken}`;
  } catch (error) {
    if (error instanceof InteractionRequiredAuthError) {
      const result = await msalInstance.acquireTokenPopup(apiRequest);
      config.headers.Authorization = `Bearer ${result.accessToken}`;
    } else {
      throw error;
    }
  }

  return config;
});

// Si el Gateway responde 401 (token inválido/expirado) o 403 (sin permiso
// para ese recurso), lo dejamos pasar como error tipado para que cada
// pantalla decida cómo mostrarlo, en vez de fallar en silencio.
httpClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (axios.isAxiosError(error) && error.response) {
      const { status } = error.response;
      if (status === 401) {
        error.message = 'Tu sesión expiró o el token no es válido. Vuelve a iniciar sesión.';
      } else if (status === 403) {
        error.message = 'No tienes permisos suficientes para esta acción.';
      }
    }
    return Promise.reject(error);
  },
);
