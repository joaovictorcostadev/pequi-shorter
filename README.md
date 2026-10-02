# 🥜 Pequi Shorter

Encurtador de URLs com analytics de acesso, autenticação JWT e sistema RBAC de permissões.

## 🛠️ Tech Stack

| Tecnologia | Versão |
|---|---|
| Kotlin | 2.3.21 |
| Spring Boot | 4.1.0 |
| Java | 21 |
| PostgreSQL | — |
| Flyway | — |
| JWT (jjwt) | 0.12.7 |
| Argon2 (Spring Security) | — |
| MaxMind GeoIP2 | 5.1.0 |
| Docker | Multi-stage build |

## 📦 Funcionalidades

- ✅ Encurtamento de URLs com código customizado ou auto-gerado
- ✅ Redirecionamento via `/r/{code}`
- ✅ Analytics de acesso (IP, país, estado, cidade, browser, OS, device)
- ✅ Autenticação JWT com access + refresh token (HttpOnly cookies)
- ✅ Refresh token rotation com revogação
- ✅ Sistema RBAC (Role-Based Access Control) com Groups e Rules
- ✅ Painel Admin separado do painel User
- ✅ CRUD completo de Users, Groups, Rules e URLs
- ✅ Geolocalização de acessos via MaxMind GeoLite2
- ✅ Senhas hasheadas com Argon2
- ✅ Migrations versionadas com Flyway

## 🚀 Como rodar

### Pré-requisitos

- Java 21+
- PostgreSQL rodando na porta `5432`
- Banco de dados `pequishortdb` criado

### Setup do banco

```sql
CREATE DATABASE pequishortdb;
CREATE USER pequishort WITH PASSWORD 'admin';
GRANT ALL PRIVILEGES ON DATABASE pequishortdb TO pequishort;
```

### Rodando a aplicação

```bash
./gradlew bootRun
```

A aplicação sobe em `http://localhost:8080`.

### Docker

```bash
docker build -t pequi-shorter .
docker run -p 8080:8080 pequi-shorter
```

> ⚠️ O container precisa de acesso a um PostgreSQL. Configure as variáveis de ambiente ou use um `docker-compose`.

## 🔐 Autenticação

A API usa **JWT** com dois tokens:

| Token | Tipo | Expiração | Armazenamento |
|---|---|---|---|
| Access Token | JWT assinado | 15 min | Cookie `access_token` (HttpOnly, Secure) + body |
| Refresh Token | Hash SHA-256 | 15 dias | Cookie `refresh_token` (HttpOnly, Secure) + body |

### Fluxo

```
1. POST /api/user/auth/login     → Recebe access_token + refresh_token
2. Requisições autenticadas      → Header: Authorization: Bearer <access_token>
3. POST /api/user/auth/refresh   → Troca refresh_token por novo par de tokens
4. POST /api/user/auth/logout    → Revoga refresh_token e limpa cookies
```

### Usuários de teste (seed automático)

| Email | Senha | Grupo |
|---|---|---|
| `admin@test.com` | `admin123` | ADMIN |
| `user@test.com` | `user123` | USER |

## 📡 API Endpoints

### Autenticação — User (`/api/user/auth/`)

| Método | Rota | Descrição | Auth |
|---|---|---|---|
| `POST` | `/api/user/auth/save` | Registro de novo usuário | ❌ |
| `POST` | `/api/user/auth/login` | Login (retorna tokens) | ❌ |
| `POST` | `/api/user/auth/logout` | Logout (revoga refresh token) | ❌ |
| `POST` | `/api/user/auth/refresh` | Refresh do access token | ❌ |

### Autenticação — Admin (`/api/admin/user/auth/`)

| Método | Rota | Descrição | Auth |
|---|---|---|---|
| `POST` | `/api/admin/user/auth/login` | Login admin | ❌ |
| `POST` | `/api/admin/user/auth/logout` | Logout admin | ❌ |
| `POST` | `/api/admin/user/auth/refresh` | Refresh admin | ❌ |

### Users (`/api/user/`)

| Método | Rota | Descrição | Permissão |
|---|---|---|---|
| `GET` | `/api/user/{id}` | Buscar usuário por ID | `USER_GET` |
| `PUT` | `/api/user/update/{id}` | Atualizar usuário | `USER_UPDATE` |
| `DELETE` | `/api/user/delete/{id}` | Deletar usuário | `USER_DELETE` |

> 💡 Usuários comuns só podem acessar/editar/deletar **seus próprios dados**.

### Admin Users (`/api/admin/user/`)

| Método | Rota | Descrição | Permissão |
|---|---|---|---|
| `POST` | `/api/admin/user/save` | Criar usuário (qualquer grupo) | `ADMIN_USER_CREATE` |
| `GET` | `/api/admin/user/{id}` | Buscar qualquer usuário | `ADMIN_USER_GET` |
| `PUT` | `/api/admin/user/update/{id}` | Atualizar qualquer usuário | `ADMIN_USER_UPDATE` |
| `DELETE` | `/api/admin/user/delete/{id}` | Deletar qualquer usuário | `ADMIN_USER_DELETE` |

### URLs (`/api/url/`)

| Método | Rota | Descrição | Permissão |
|---|---|---|---|
| `POST` | `/api/url/save` | Criar URL encurtada | `URL_CREATE` |
| `GET` | `/api/url/get` | Listar URLs do usuário logado | `URL_GET` |
| `DELETE` | `/api/url/delete/{id}` | Deletar URL | `URL_DELETE` |
| `GET` | `/r/{name}` | **Redirect** (público) | ❌ |

### Groups (`/api/group/`)

| Método | Rota | Descrição | Permissão |
|---|---|---|---|
| `POST` | `/api/group/save` | Criar grupo | `GROUP_CREATE` |
| `GET` | `/api/group/{id}` | Buscar grupo por ID | `GROUP_GET` |
| `GET` | `/api/group/` | Listar todos os grupos | `GROUP_GET_ALL` |
| `PUT` | `/api/group/update` | Atualizar grupo | `GROUP_UPDATE` |
| `DELETE` | `/api/group/delete/{id}` | Deletar grupo | `GROUP_DELETE` |

### Rules (`/api/rule/`)

| Método | Rota | Descrição | Permissão |
|---|---|---|---|
| `POST` | `/api/rule/save` | Criar regra | `RULE_CREATE` |
| `GET` | `/api/rule/getAll` | Listar todas as regras | `RULE_GET_ALL` |
| `GET` | `/api/rule/{id}` | Buscar regra por ID | `RULE_GET` |
| `PUT` | `/api/rule/update` | Atualizar regra | `RULE_UPDATE` |
| `DELETE` | `/api/rule/delete/{id}` | Deletar regra | `RULE_DELETE` |

## 🏗️ Arquitetura

```
controller/          → Camada HTTP (ResponseEntity, cookies, validação)
  ├── UrlController
  ├── UserController
  ├── AdminUserController
  ├── GroupController
  └── RuleController

service/             → Lógica de negócio (retorna DTOs, lança exceções)
  ├── UrlService
  ├── UserService
  ├── AdminUserService
  ├── AuthService
  ├── GroupService
  ├── RuleService
  ├── TokenService
  ├── RefreshTokenService
  ├── GeoIpService
  ├── UrlAccessService
  └── CustomUserDetailsService

entity/              → Entidades JPA
  ├── User
  ├── Url
  ├── UrlAccess
  ├── Group
  ├── Rule
  ├── GroupRule
  └── RefreshToken

dto/                 → Data Transfer Objects
  ├── auth/
  ├── user/
  ├── url/
  ├── group/
  ├── rule/
  ├── geoip/
  └── response/

exception/           → Exceções de negócio (capturadas pelo GlobalExceptionHandler)
security/            → JWT filter, SecurityConfig, UserAuthenticated
config/              → Flyway, GlobalExceptionHandler
seed/                → InitialDataSeeder (groups, rules, usuários de teste)
```

## 🗃️ Modelo de Dados

```
┌──────────┐     ┌───────────┐     ┌──────────┐
│  groups   │────▶│ group_rule │◀────│  rules   │
└──────────┘     └───────────┘     └──────────┘
     │
     ▼
┌──────────┐     ┌───────────────┐
│  users   │────▶│ refresh_tokens│
└──────────┘     └───────────────┘
     │
     ▼
┌──────────┐     ┌───────────────┐
│   urls   │────▶│ url_accesses  │
└──────────┘     └───────────────┘
```

## 📄 Licença

Este projeto é um side-project pessoal de estudos.
