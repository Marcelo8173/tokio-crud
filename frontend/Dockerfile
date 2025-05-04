# Etapa 1: build da aplicação Angular
FROM node:18-alpine as build
WORKDIR /app
COPY . .
RUN npm install && npm run build --prod

# Etapa 2: Nginx serve os arquivos
FROM nginx:alpine
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
