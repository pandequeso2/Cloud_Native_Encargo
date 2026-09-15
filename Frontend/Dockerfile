# Etapa 1: build de la SPA con Vite
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .

# Argumentos para el build de produccion
ARG VITE_ENTRA_TENANT_ID
ARG VITE_ENTRA_AUDIENCE
ARG VITE_REDIRECT_URI
ARG VITE_API_BASE_URL

ENV VITE_ENTRA_TENANT_ID=$VITE_ENTRA_TENANT_ID
ENV VITE_ENTRA_AUDIENCE=$VITE_ENTRA_AUDIENCE
ENV VITE_REDIRECT_URI=$VITE_REDIRECT_URI
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

RUN npm run build

# Etapa 2: servir los archivos estaticos con Nginx
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80