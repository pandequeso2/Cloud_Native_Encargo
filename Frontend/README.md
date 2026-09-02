# Bitácora — Frontend

SPA en React + TypeScript (Vite) que consume el Gateway de microservicios
del backend (`Backend/gateway`), con autenticación mediante **Microsoft
Entra ID** vía `@azure/msal-react`.

## Requisitos previos

- Node.js 20+
- El backend corriendo (mínimo: `eureka-server`, `gateway`, y los
  microservicios que quieras probar: `grupos_service`, `trabajos_service`,
  `entregas_service`, `comentarios_service`, `integrantes_service`).
- Un App Registration de **SPA** y otro de **API** en Entra ID (ver
  `Backend/gateway/ENTRA_ID_SETUP.md`).

## Configuración

```bash
cp .env.example .env
```

Completa `.env` con los valores reales de tu App Registration:

```
VITE_ENTRA_CLIENT_ID=<client-id-del-SPA>
VITE_ENTRA_TENANT_ID=<tenant-id>
VITE_ENTRA_API_CLIENT_ID=<client-id-del-backend>
VITE_API_BASE_URL=http://localhost:8095
VITE_REDIRECT_URI=http://localhost:5173
```

`VITE_REDIRECT_URI` debe coincidir **exactamente** con un Redirect URI
registrado en la plataforma "Single-page application" del App Registration
del SPA en el portal de Azure.

## Ejecutar en desarrollo

```bash
npm install
npm run dev
```

Abre `http://localhost:5173`. Sin sesión, cualquier ruta redirige a
`/login`; el botón dispara `loginPopup` contra Entra ID.

## Build de producción

```bash
npm run build
npm run preview
```

## Estructura

```
src/
├── auth/            # Config MSAL, instancia, hook de roles/claims
├── api/              # Cliente HTTP con interceptor de token + servicios por dominio
├── routes/           # Guard de rutas protegidas
├── components/       # Layout (rail + shell) y estados async/status
├── pages/            # Login, Dashboard, Grupos, GrupoDetalle, Trabajos, Entregas
├── types/            # Tipos de dominio alineados a las entidades del backend
└── styles/           # Tokens de diseño y estilos globales
```

## Cómo se cumple cada punto de la rúbrica

| Requisito | Dónde |
|---|---|
| MSAL integrado y operativo | `src/auth/msalInstance.ts`, `src/main.tsx` (`MsalProvider`) |
| Login / logout funcionan | `src/pages/Login.tsx` (`loginPopup`), `src/components/Layout.tsx` (`logoutPopup`) |
| Guards operan sin fallas | `src/routes/ProtectedRoute.tsx` |
| Interceptor adjunta el token | `src/api/httpClient.ts` (`acquireTokenSilent` + fallback `acquireTokenPopup`) |
| Se obtienen los tokens necesarios | `src/auth/authConfig.ts` (`loginRequest`, `apiRequest`) |
| Se leen roles/scopes desde los claims | `src/auth/useAuthInfo.ts`, mostrado en `Layout.tsx` |
