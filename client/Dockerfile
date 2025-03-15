FROM node:23-alpine AS base
WORKDIR /app/client
COPY package*.json .
RUN npm install --frozen-lockfile
COPY . .
RUN npm run build

FROM base AS dev
WORKDIR /app/client
EXPOSE 5173
CMD ["npm", "run", "dev"]
