# 🎬 StreamHub — Scalable Microservices Streaming Platform

Distributed movie streaming platform built with **Java Spring Boot** (3 microservices) and **React.js**, demonstrating microservices architecture, JPA/Hibernate optimization, JWT auth, and real-time analytics with Recharts.

<p align="left">
  <img alt="Java" src="https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?logo=springboot&logoColor=white">
  <img alt="React" src="https://img.shields.io/badge/React-18.2-61DAFB?logo=react&logoColor=black">
  <img alt="Vite" src="https://img.shields.io/badge/Vite-5-646CFF?logo=vite&logoColor=white">
  <img alt="JWT" src="https://img.shields.io/badge/Auth-JWT-000000?logo=jsonwebtokens&logoColor=white">
  <img alt="Recharts" src="https://img.shields.io/badge/Charts-Recharts-FF6384">
</p>

---

## 🏗 Architecture

Three independently deployable Spring Boot services — each with its own port and its own database. The frontend talks directly to the User and Movie services; the Analytics service composes data from both over HTTP.


**Why it's built this way**

- **Database-per-service** — no shared schema, so services stay loosely coupled and evolve independently. The Analytics service deliberately owns *no* database; it's a pure read-time composition layer.
- **Stateless JWT** — the signed token carries identity, role, and user id, so any service can verify a request without a shared session store. That's what makes horizontal scaling possible.
- **Aggregation over duplication** — instead of copying user/movie data into analytics, KPIs are computed on demand from the source services and cached briefly.

---

## 🧰 Tech Stack

| Area | Technologies |
|---|---|
| **Backend** | Java 17 · Spring Boot 3.2.5 · Spring Web (MVC) · Spring Data JPA / Hibernate · Spring Security · Spring Validation · Spring Cache |
| **Auth** | JJWT (JSON Web Tokens) · BCrypt password hashing |
| **Inter-service** | Spring WebFlux `WebClient` (non-blocking HTTP) |
| **Database** | H2 (in-memory, one per service) |
| **Frontend** | React 18.2 · Vite 5 · React Router 6 · Axios · Recharts 2 |
| **Tooling** | Maven · Lombok |

---

## 🗃 Data Model

Two services own data. The link from `VIEW_EVENT` to a user is a **logical reference, not a SQL foreign key** — because the user lives in a *different* service's database. This is the classic microservices trade-off, and the reason the Analytics service exists.


> **Indexing strategy:** every column on a hot path is indexed — `email`/`username` (login), `genre`/`title` (catalog filters), and `movie_id`/`user_id`/`viewed_at` (analytics aggregations).

---

## 🔁 Request Flows

Stateless login on the left; the analytics fan-out (parallel calls to both services, with derived KPIs and caching) on the right.


---

## ⚙️ Prerequisites

- **JDK 17+** (`java -version` to verify)
- **Maven 3.8+** (`mvn -version`)
- **Node 18+** and **npm**

If you don't have Maven: download from https://maven.apache.org/download.cgi, unzip, add `bin` to PATH.

---

## 🚀 Quick Start (4 terminals)

### Terminal 1 — User Service
```bash
cd user-service
mvn spring-boot:run
```

### Terminal 2 — Movie Service
```bash
cd movie-service
mvn spring-boot:run
```

### Terminal 3 — Analytics Service
```bash
cd analytics-service
mvn spring-boot:run
```

### Terminal 4 — Frontend
```bash
cd frontend
npm install
npm run dev
```

Open **http://localhost:3000**

> 💡 Each backend service exposes an H2 console at `/h2-console` (e.g. `http://localhost:8081/h2-console`). The catalog auto-seeds 12 movies across 5 genres on startup.

---

## 👥 Demo Accounts

| Role  | Email             | Password |
|-------|-------------------|----------|
| Admin | admin@stream.com  | admin123 |
| User  | user@stream.com   | user123  |

---

## 📦 What Each Service Does

### User Service (8081)
- Registration, login (BCrypt), JWT issuance
- `POST /api/auth/register`, `POST /api/auth/login`, `GET /api/auth/validate`
- Stats endpoint for analytics: `GET /api/auth/stats`

### Movie Service (8082)
- Catalog (browse, search, filter by genre, trending)
- Records view events (movieId, userId, watch duration)
- **Caching**: `@Cacheable` on hot endpoints; `@CacheEvict` on writes so view counts stay fresh
- Stats endpoint: `GET /api/movies/stats`

### Analytics Service (8083)
- Aggregates user + movie stats via `WebClient` (5s timeout, graceful fallback)
- Returns dashboard KPIs (cached)
- `GET /api/analytics/dashboard`

---

## 📌 Key Talking Points (Backed by Real Code)

| Talking Point | Where in Code |
|---|---|
| Microservice architecture | 3 separate Spring Boot apps, separate ports & databases |
| Stateless JWT auth | `JwtUtil`, `SecurityConfig` (`SessionCreationPolicy.STATELESS`), BCrypt hashing |
| Spring Data JPA + Hibernate | `UserRepository`, `MovieRepository`, `ViewEventRepository` with custom `@Query` + native queries |
| Read-path performance | DB indexes on hot columns, `@Cacheable`/`@CacheEvict`, HikariCP pool tuning (20–30), batch inserts (`order_inserts`, `batch_size: 50`) |
| Resilient service-to-service calls | `WebClient` fan-out with per-call timeout + fallback to empty results |
| React.js + Recharts dashboard | `frontend/src/pages/Dashboard.jsx` — multiple chart types, auto-refresh |
| 12+ operational KPIs | total users, active 24h, new users 7d, engagement rate, total movies, total views, views 24h, unique viewers 24h, watch hours, avg views/user, views by hour, top movies |

> ℹ️ **A note for interviews:** if your resume cites specific numbers like *"5,000+ concurrent users"* or *"35% latency reduction,"* be ready to say how you measured them. The code contains the *techniques* that support those claims (pooling, caching, indexing, batching), but the exact figures need a load test (e.g. JMeter/k6) to cite honestly. Run one and you can keep the numbers with confidence — or talk about the techniques instead.

---
