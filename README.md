# wanda-java

API principal da plataforma Wanda. Responsável por autenticação, gerenciamento de funções dos alunos, execução de partidas e integração com o wanda-python.

---

## Pré-requisitos

**Para rodar com Docker:**
- Docker
- Docker Compose

**Para rodar manualmente:**
- Java 17
- Maven (ou usar o `./mvnw` incluso no projeto)
- PostgreSQL rodando localmente com o database `wanda_web` criado

---

## Configuração

Copie o arquivo de exemplo e preencha os valores:

```bash
cp .env.example .env
```

### Variáveis de ambiente

| Variável | Descrição | Exemplo |
|---|---|---|
| `JWT_SECRET` | Chave secreta para geração dos tokens JWT | `sua_chave_secreta` |
| `DB_URL` | URL de conexão com o PostgreSQL | `jdbc:postgresql://localhost:5432/wanda_web` |
| `DB_USER` | Usuário do banco de dados | `postgres` |
| `DB_PASS` | Senha do banco de dados | `sua_senha` |
| `ALLOWED_ORIGINS` | Origem permitida pelo CORS | `http://localhost:5173` |
| `PYTHON_BASE_URL` | URL base do wanda-python | `http://localhost:8000/api` |
| `OTEL_ENDPOINT` | Endpoint do OpenTelemetry Collector | `http://localhost:4318` |
| `SPRING_PROFILES_ACTIVE` | Perfil ativo do Spring | `prod` |

> **Sobre o `OTEL_ENDPOINT`:** o endpoint padrão do OpenTelemetry Collector é `http://localhost:4318`. Essa variável não é obrigatória para o funcionamento da aplicação, mas se o collector não estiver rodando, erros de conexão aparecerão no terminal continuamente.

---

## Banco de dados

O projeto usa **Flyway** para gerenciamento do schema. As tabelas são criadas automaticamente na primeira execução — basta ter o database criado no PostgreSQL:

```sql
CREATE DATABASE wanda_web;
```

Após isso, o Flyway aplica todas as migrations automaticamente ao subir a aplicação.

---

## Perfis do Spring

| Perfil | Uso | Banco | Show SQL |
|---|---|---|---|
| `prod` | Produção | PostgreSQL | Não |
| `dev` | Desenvolvimento local | PostgreSQL | Sim |
| `test` | Testes automatizados | H2 (in-memory) | Sim |

> **Dica:** use `SPRING_PROFILES_ACTIVE=dev` localmente para ver as queries SQL no terminal. Em produção use `prod` para evitar ruído nos logs.

---

## Rodando com Docker

```bash
# Sobe o container (builda a imagem na primeira vez)
docker-compose up --build

# Rodar em background
docker-compose up --build -d

# Parar
docker-compose down
```

A aplicação estará disponível em `http://localhost:8088`.

> **Atenção:** o compose usa a rede externa `wanda-network`. Se ela não existir, crie antes:
> ```bash
> docker network create wanda-network
> ```

---

## Rodando manualmente

O projeto usa **dotenv** como dependência — as variáveis são lidas automaticamente do `.env` na raiz do projeto. Basta configurar o `.env` e rodar:

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

> **Atenção:** ao rodar manualmente, a porta é `8080` (padrão do Spring). Com Docker, é `8088` conforme mapeado no compose.
