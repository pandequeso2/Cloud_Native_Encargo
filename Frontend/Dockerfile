# Etapa 1: build de la SPA con Vite
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .

# Argumentos opcionales para el build de produccion (AWS). Si no se pasan,
# Vite simplemente no define estas variables y el codigo cae en sus
# valores por defecto de localhost (ver authConfig.ts).
ARG VITE_API_BASE_URL
ARG VITE_REDIRECT_URI
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
ENV VITE_REDIRECT_URI=$VITE_REDIRECT_URI

RUN npm run build

# Etapa 2: servir los archivos estaticos con Nginx
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80