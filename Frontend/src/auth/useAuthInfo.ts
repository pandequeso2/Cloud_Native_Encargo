import { useMsal } from '@azure/msal-react';
import { useMemo } from 'react';

interface EntraClaims {
  name?: string;
  preferred_username?: string;
  roles?: string[];
}

export interface AuthInfo {
  displayName: string;
  email: string;
  roles: string[];
  hasRole: (role: string) => boolean;
}

/**
 * Expone datos derivados de los claims del ID token de la cuenta activa
 * (nombre, correo, roles de aplicación asignados en Entra ID).
 * No requiere una llamada de red: los claims ya vienen en el idTokenClaims
 * que MSAL cachea localmente tras el login.
 */
export function useAuthInfo(): AuthInfo | null {
  const { accounts } = useMsal();
  const account = accounts[0];

  return useMemo(() => {
    if (!account) return null;
    const claims = (account.idTokenClaims ?? {}) as EntraClaims;
    const roles = claims.roles ?? [];

    return {
      displayName: claims.name ?? account.name ?? account.username,
      email: claims.preferred_username ?? account.username,
      roles,
      hasRole: (role: string) => roles.includes(role),
    };
  }, [account]);
}
