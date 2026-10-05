# Pequi Shorter

URL shortener with access analytics, JWT authentication, and RBAC permission system.

## 🛠️ Tech Stack

| Technology | Version |
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

## 📦 Features

- ✅ URL shortening with custom or auto-generated codes
- ✅ Redirection via `/r/{code}`
- ✅ Access analytics (IP, country, state, city, browser, OS, device)
- ✅ JWT authentication with access + refresh token (HttpOnly cookies)
- ✅ Refresh token rotation with revocation
- ✅ RBAC (Role-Based Access Control) system with Groups and Rules
- ✅ Separate Admin and User panels
- ✅ Full CRUD for Users, Groups, Rules, and URLs
- ✅ Access geolocation via MaxMind GeoLite2
- ✅ Hashed passwords with Argon2
- ✅ Versioned migrations with Flyway

## 🚀 How to Run

### Prerequisites

- Java 21+
- PostgreSQL running on port `5432`
- Database `pequishortdb` created

### Database Setup

```sql
CREATE DATABASE pequishortdb;
CREATE USER pequishort WITH PASSWORD 'admin';
GRANT ALL PRIVILEGES ON DATABASE pequishortdb TO pequishort;
```

### Running the application

```bash
./gradlew bootRun
```

The application starts at `http://localhost:8080`.

### Docker

> ⚠️ **WARNING:** Before starting the application, you must fill in the environment variables in the `.env-copy` file and rename it to `.env` (just remove `-copy` from the name). The application will not work without it!

```bash
docker build -t pequi-shorter .
docker run -p 8080:8080 pequi-shorter
```

> ⚠️ The container needs access to a PostgreSQL database. Configure the credentials in your new `.env` file or use a `docker-compose`.

## 🔐 Authentication

The API uses **JWT** with two tokens:

| Token | Type | Expiration | Storage |
|---|---|---|---|
| Access Token | Signed JWT | 15 min | Cookie `access_token` (HttpOnly, Secure) + body |
| Refresh Token | SHA-256 Hash | 15 days | Cookie `refresh_token` (HttpOnly, Secure) + body |

### Flow

```
1. POST /api/user/auth/login     → Receives access_token + refresh_token
2. Authenticated requests      → Header: Authorization: Bearer <access_token>
3. POST /api/user/auth/refresh   → Exchanges refresh_token for a new pair of tokens
4. POST /api/user/auth/logout    → Revokes refresh_token and clears cookies
```

### Test Users (Automatic Seed)

| Email | Password | Group |
|---|---|---|
| `admin@test.com` | `admin123` | ADMIN |
| `user@test.com` | `user123` | USER |

## 📡 API Endpoints

### Authentication — User (`/api/user/auth/`)

| Method | Route | Description | Auth |
|---|---|---|---|
| `POST` | `/api/user/auth/save` | New user registration | ❌ |
| `POST` | `/api/user/auth/login` | Login (returns tokens) | ❌ |
| `POST` | `/api/user/auth/logout` | Logout (revokes refresh token) | ❌ |
| `POST` | `/api/user/auth/refresh` | Access token refresh | ❌ |

### Authentication — Admin (`/api/admin/user/auth/`)

| Method | Route | Description | Auth |
|---|---|---|---|
| `POST` | `/api/admin/user/auth/login` | Admin login | ❌ |
| `POST` | `/api/admin/user/auth/logout` | Admin logout | ❌ |
| `POST` | `/api/admin/user/auth/refresh` | Admin refresh | ❌ |

### Users (`/api/user/`)

| Method | Route | Description | Permission |
|---|---|---|---|
| `GET` | `/api/user/{id}` | Get user by ID | `USER_GET` |
| `PUT` | `/api/user/update/{id}` | Update user | `USER_UPDATE` |
| `DELETE` | `/api/user/delete/{id}` | Delete user | `USER_DELETE` |

> 💡 Regular users can only access/edit/delete **their own data**.

### Admin Users (`/api/admin/user/`)

| Method | Route | Description | Permission |
|---|---|---|---|
| `POST` | `/api/admin/user/save` | Create user (any group) | `ADMIN_USER_CREATE` |
| `GET` | `/api/admin/user/{id}` | Get any user | `ADMIN_USER_GET` |
| `PUT` | `/api/admin/user/update/{id}` | Update any user | `ADMIN_USER_UPDATE` |
| `DELETE` | `/api/admin/user/delete/{id}` | Delete any user | `ADMIN_USER_DELETE` |

### URLs (`/api/url/`)

| Method | Route | Description | Permission |
|---|---|---|---|
| `POST` | `/api/url/save` | Create shortened URL | `URL_CREATE` |
| `GET` | `/api/url/get` | List URLs for logged user | `URL_GET` |
| `DELETE` | `/api/url/delete/{id}` | Delete URL | `URL_DELETE` |
| `GET` | `/r/{name}` | **Redirect** (public) | ❌ |

### Groups (`/api/group/`)

| Method | Route | Description | Permission |
|---|---|---|---|
| `POST` | `/api/group/save` | Create group | `GROUP_CREATE` |
| `GET` | `/api/group/{id}` | Get group by ID | `GROUP_GET` |
| `GET` | `/api/group/` | List all groups | `GROUP_GET_ALL` |
| `PUT` | `/api/group/update` | Update group | `GROUP_UPDATE` |
| `DELETE` | `/api/group/delete/{id}` | Delete group | `GROUP_DELETE` |

### Rules (`/api/rule/`)

| Method | Route | Description | Permission |
|---|---|---|---|
| `POST` | `/api/rule/save` | Create rule | `RULE_CREATE` |
| `GET` | `/api/rule/getAll` | List all rules | `RULE_GET_ALL` |
| `GET` | `/api/rule/{id}` | Get rule by ID | `RULE_GET` |
| `PUT` | `/api/rule/update` | Update rule | `RULE_UPDATE` |
| `DELETE` | `/api/rule/delete/{id}` | Delete rule | `RULE_DELETE` |

## 🏗️ Architecture

```
controller/          → HTTP Layer (ResponseEntity, cookies, validation)
  ├── UrlController
  ├── UserController
  ├── AdminUserController
  ├── GroupController
  └── RuleController

service/             → Business logic (returns DTOs, throws exceptions)
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

entity/              → JPA Entities
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

exception/           → Business exceptions (caught by GlobalExceptionHandler)
security/            → JWT filter, SecurityConfig, UserAuthenticated
config/              → Flyway, GlobalExceptionHandler
seed/                → InitialDataSeeder (groups, rules, test users)
```

## 🗃️ Data Model

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

## 📄 License

This project is a personal side-project for study purposes.
