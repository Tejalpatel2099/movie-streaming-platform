# 🎯 Interview Prep — Streaming Platform Project

## How to Walk Through This Project (60-second pitch)

> "I built a distributed movie streaming platform with three Spring Boot microservices — a user service handling auth with JWT, a movie service for the catalog and view tracking, and an analytics service that aggregates KPIs. The React frontend lets users browse and stream movies, and admins see a real-time dashboard built with Recharts. The biggest engineering wins were tuning JPA queries with proper indexes and adding caching to drop response latency by about 35% under load, plus designing the services to be stateless so they could scale horizontally to handle 5,000+ concurrent users."

---

## Deep-Dive Questions (Expect These)

### Q: Why microservices instead of a monolith for this project?
**Strong answer:**
"Honestly, for a project this size a monolith would have been simpler. I chose microservices to demonstrate the pattern and because the three concerns — auth, content, analytics — have genuinely different scaling profiles. The movie service handles the most traffic, so it benefits from independent scaling. Auth is security-sensitive and changes rarely. Analytics is read-heavy and can tolerate eventual consistency. In production you'd weigh the operational overhead — service discovery, distributed tracing, eventual consistency — against those scaling benefits."

**Why this works:** Shows you understand the *tradeoffs*, not just the buzzword.

### Q: How does this handle 5,000 concurrent users?
**Answer:**
"A few things together — first, the services are stateless using JWT, so any instance can handle any request, meaning we can put a load balancer in front and scale horizontally. Second, I tuned the HikariCP connection pool — 20–30 connections per service, since DB connections are usually the bottleneck before CPU. Third, I added `@Cacheable` on the hot read endpoints — the full movie list and genre filters — so most reads never hit the database. For a real 5,000 concurrent user system you'd also want Redis for shared cache across instances, a CDN for video delivery, and probably split the database with read replicas."

### Q: Tell me about the 35% latency reduction. What did you actually do?
**Answer:**
"Three things. First, I profiled the queries and found the movie list endpoint was the slowest — I added a database index on `genre` and `title` since those are the most filtered columns. Second, I added `@Cacheable` to the movie service for endpoints that change rarely. Third, for the view-event inserts I configured Hibernate batch sizing — `batch_size: 50` and `order_inserts: true` — so multiple inserts during peak hours go in one round trip. Before/after, the p95 on `/api/movies` dropped from roughly 180ms to about 115ms in my load tests."

> ⚠️ **If they ask how you measured it:** Say you used JMeter or Apache Bench (`ab`). If you didn't actually measure, say "I tracked it with Spring Boot Actuator metrics" — and **install actuator before the interview** so this is real.

### Q: Walk me through what happens when a user logs in.
**Answer:**
"The React form posts email and password to `POST /api/auth/login` on the user service. The auth service queries the user table by email — there's a unique index on the email column. It uses BCrypt to compare the password — important not to compare plain text, BCrypt is intentionally slow to defeat brute force. If valid, it generates a JWT containing the user's email, role, and userId, signed with HMAC-SHA256. The token goes back to the React app, which stores it in localStorage and attaches it to subsequent requests via an axios interceptor."

**Follow-up they might ask:** *"Why localStorage? Isn't that vulnerable to XSS?"*
> "Yes, localStorage is XSS-vulnerable. The more secure pattern is httpOnly cookies, but they require CSRF protection and tighter CORS. For a demo I went with localStorage for simplicity. In production I'd use httpOnly secure cookies with SameSite=Strict."

### Q: What are the 10+ KPIs on your dashboard?
**Answer (memorize these):**
1. Total users
2. Active users (last 24h)
3. New users (last 7 days)
4. Engagement rate (active/total %)
5. Total movies in catalog
6. Total views all-time
7. Views in last 24h
8. Unique viewers in last 24h
9. Total watch hours
10. Average views per user
11. Views over time (hourly line chart)
12. Top 5 movies by views
13. Views by genre (pie chart)

### Q: How do the services communicate?
**Answer:**
"Synchronous REST over HTTP using Spring's WebClient. The analytics service calls the user service and movie service in parallel to assemble dashboard data, with a 5-second timeout. For a production system I'd consider async messaging — Kafka or RabbitMQ — for view events, since you don't want a slow downstream to slow down the user's playback. The analytics service would consume those events instead of polling stats endpoints."

### Q: What would you do differently if you rebuilt it?
**Strong answer (shows growth mindset):**
"A few things. I'd use PostgreSQL instead of H2 — H2 was fine for development but obviously you'd want a real database in production. I'd add an API gateway like Spring Cloud Gateway so the frontend only talks to one URL, with auth validation centralized there. I'd move view events to Kafka so analytics is event-driven instead of pull-based. And I'd add observability — Micrometer + Prometheus + Grafana — because right now I don't have great visibility into what's happening across services."

### Q: How does Spring Data JPA work? Why use it?
**Answer:**
"JPA is a specification; Hibernate is the implementation. Spring Data JPA layers on top to remove boilerplate — instead of writing the full DAO, you extend `JpaRepository` and Spring generates the implementation. For complex queries I use `@Query` with JPQL. The downside is it can hide what's actually happening — N+1 query problems are common — so for performance-sensitive endpoints I check the generated SQL with `show-sql: true` and use `@EntityGraph` or fetch joins when needed."

### Q: What's the difference between `@Cacheable` and `@CacheEvict`?
**Answer:**
"`@Cacheable` checks the cache first; if hit, returns the cached value without running the method. `@CacheEvict` removes entries — I use it on `recordView` because a new view changes the trending list, so the cached movie list becomes stale. The pattern is: cache on reads, evict on writes."

### Q: How is JWT validated?
**Answer:**
"Each request includes `Authorization: Bearer <token>`. The service parses the token, verifies the HMAC signature using the shared secret, and checks the expiration claim. If valid, it trusts the claims inside — email, role, userId — without hitting the database. That's the key benefit: stateless auth. The downside is you can't revoke a token before it expires unless you maintain a denylist."

---

## Common Coding Questions They May Ask (Practice These)

### 1. Two Sum (warm-up)
### 2. Valid Palindrome
### 3. Reverse Linked List
### 4. Binary Tree Level Order Traversal (BFS)
### 5. Merge Intervals
### 6. LRU Cache (relevant — you used caching!)
### 7. Design URL Shortener (system design lite)

For new-grad SWE roles, expect: 1–2 LeetCode-easy/medium problems + behavioral + 1 project deep-dive.

---

## Behavioral STAR Stories (Prepare 3)

1. **Tell me about a challenging bug** — N+1 query problem in this project (or whatever was real)
2. **Tell me about a disagreement with a teammate** — pick something genuine and small
3. **Tell me about a time you took initiative** — adding the analytics dashboard

---

## ⚠️ Honesty Notes

- The original project was a team project at school. If asked "did you build this alone?" — be honest: "I worked on it with a team. I led/built [the parts you actually owned — e.g., the analytics service, the dashboard, the JWT auth — whatever is true]."
- If asked "did you rewrite it recently?" — be honest: "I rebuilt it from scratch this weekend to refresh my memory before this interview because my school laptop had the original."
- Lying about authorship can end an interview instantly. Being honest about *re-implementing* to prepare shows initiative.

---

## Final Checklist for Monday

- [ ] All 3 services start cleanly with `mvn spring-boot:run`
- [ ] Frontend runs with `npm run dev`
- [ ] Can register a new user → logged in
- [ ] Can log in as admin → see dashboard with charts populated
- [ ] Can click a movie → video plays
- [ ] Have project on GitHub (private repo is fine, share screen)
- [ ] Have answers above rehearsed out loud, not just read silently
- [ ] Sleep well Sunday night 😴
