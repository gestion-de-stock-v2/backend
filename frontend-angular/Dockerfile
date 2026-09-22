# ============================================================
# Frontend Angular : construction puis service des fichiers statiques par nginx
# ============================================================

FROM node:20-alpine AS builder
WORKDIR /build

# package-lock.json est versionne : npm ci garantit des dependances reproductibles.
COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build -- --configuration production

FROM nginx:1.27-alpine AS runtime
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /build/dist/frontend/browser /usr/share/nginx/html
EXPOSE 80
