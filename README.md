# Wanda Web — Backend (Spring Boot)

Backend em **Spring Boot** com **PostgreSQL** e **Flyway** (migrations automáticas).

---

## ✅ Requisitos

- Java 17+
- Maven (ou `./mvnw`)
- PostgreSQL (via **Docker Compose** ou local)

---

## 📁 Estrutura de Infraestrutura

O Postgres via Docker fica em `infra/` para não confundir com o `Dockerfile` do projeto:

```
infra/
  docker-compose.yml
  .env.docker
```

---

## ⚙️ Variáveis de Ambiente

Crie um arquivo `.env` na **raiz** do projeto baseado no `.env.example`:

```env
ALLOWED_ORIGINS=http://localhost:5173
DB_URL=jdbc:postgresql://localhost:5433/wanda_web
DB_USER=postgres
DB_PASS=postgres
JWT_SECRET=o_valor_que_quiser
PYTHON_BASE_URL=http://localhost:8000/api
```

> **Observação:** o Spring Boot não lê `.env` automaticamente. Carregue as variáveis via sua IDE (Run/Debug env vars) ou exporte no terminal antes de rodar.

---

## 🗄️ Banco de Dados e Flyway

Ao subir a aplicação, o Flyway:

- Cria a tabela `flyway_schema_history`
- Aplica as migrations em `src/main/resources/db/migration`
- Cria tabelas e seeds iniciais (ex.: usuário admin)

---

## 🐳 Opção 1 — Postgres com Docker

### 1. Configurar arquivos do Docker

**`infra/docker-compose.yml`**

```yaml
services:
  postgres:
    image: postgres:16
    container_name: wanda_postgres
    restart: unless-stopped
    env_file:
      - .env.docker
    ports:
      - "${POSTGRES_PORT:-5433}:5432"
    volumes:
      - wanda_pg_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U $$POSTGRES_USER -d $$POSTGRES_DB"]
      interval: 5s
      timeout: 5s
      retries: 10

volumes:
  wanda_pg_data:
```

**`infra/.env.docker`**

```env
POSTGRES_DB=wanda_web
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_PORT=5433
```

### 2. Subir o Postgres

```bash
cd infra
docker compose --env-file .env.docker up -d
```

- Postgres disponível em `localhost:5433`
- Database `wanda_web` criado automaticamente

### 3. Subir o backend

Na raiz do projeto:

```bash
./mvnw spring-boot:run
```

API disponível em `http://localhost:8080`

---

## 🖥️ Opção 2 — Postgres Local (sem Docker)

1. Certifique-se de ter um Postgres rodando (ex.: `localhost:5432`)

2. Crie o banco de dados:

```sql
CREATE DATABASE wanda_web;
```

3. Ajuste o `.env` para apontar para o Postgres local:

```env
DB_URL=jdbc:postgresql://localhost:5432/wanda_web
DB_USER=seu_usuario
DB_PASS=sua_senha
```

4. Rode o backend:

```bash
./mvnw spring-boot:run
```



