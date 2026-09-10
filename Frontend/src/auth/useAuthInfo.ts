import { useMsal } from '@azure/msal-react';

export function useAuthInfo() {
  const { accounts } = useMsal();
  const account = accounts[0];

  const email = account?.username?.toLowerCase().trim() ?? '';
  const displayName = account?.name ?? account?.username ?? 'Usuario';

  let userRole = 'Estudiante';

  if (email === 'ben.arayag@duocuc.cl') {
    userRole = 'Admin';
  } else if (email === 'vi.garridod@duocuc.cl') {
    userRole = 'Profesor';
  } else if (email === 'mat.mirandag@duocuc.cl') {
    userRole = 'Estudiante';
  }

  const hasRole = (requiredRole: string): boolean => {
    return userRole.toLowerCase() === requiredRole.toLowerCase();
  };

  return {
    account,
    displayName,
    role: userRole,
    roles: [userRole],
    hasRole,
  };
}