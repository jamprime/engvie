# Frontend Dockerfile
FROM node:20-alpine AS builder

WORKDIR /app

# Копируем package.json и package-lock.json
COPY package*.json ./

# Устанавливаем зависимости
RUN npm ci

# Копируем исходный код
COPY . .

# Собираем проект (вызываем vite напрямую через node, минуя сломанный .bin/tsc на Alpine)
RUN node node_modules/vite/bin/vite.js build

# Production stage
FROM nginx:alpine

# Копируем собранные файлы из builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

# Копируем конфигурацию nginx если нужно
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

