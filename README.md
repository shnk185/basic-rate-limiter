# Basic Rate Limiter Service

A centralized, standalone rate limiting service. A gateway (or any client)
forwards request metadata to this service before proxying, and this service
allows the request or rejects it with `429`. Limits are configured per
method + path pattern and stored in MySQL, enforced with token buckets in
Redis.

Intended usage: gateway intercepts incoming request → calls this service
with the original IP, URI, and method → allows or blocks based on the
matching limit configuration.

## Stack

- Java 17, Spring Boot 4
- MySQL 8
- Redis 7

## Setup instructions
```
git clone <this-repo>
cd basic-rate-limiter
docker compose up -d
./mvnw spring-boot:run
```
The app starts on **http://localhost:8080/**

## API summary

Rate limit: called by the gateway on every request:

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/rateLimit` | Public (behavior differs if user cookie present) | Check limits for one request. Reads forwarding headers (below). Returns `200` empty body if allowed, `429` if any applicable bucket is empty |

Expected forwarding headers on `GET /rateLimit`:

| Header | Description |
|---|---|
| `x-real-ip` | Original client IP, used for per-IP limits |
| `x-request-uri` | Original request URI (query string and trailing slash are stripped before matching) |
| `x-request-method` | Original request method (`GET`, `POST`, ...) |
| `Cookie: access_token=<JWT>` | Optional. If present and valid, request is treated as authenticated and checked against per-user limits; otherwise against IP / anonymous limits |

Config management — requires admin key header `x-api-key`:

| Method | Path | Description |
|---|---|---|
| POST | `/rateLimit/configs` | Create a limit config |
| PUT | `/rateLimit/configs/{id}` | Update a limit config |
| DELETE | `/rateLimit/configs/{id}` | Soft-delete a limit config |
| GET | `/rateLimit/configs?path=&method=&limitType=&page=&size=` | Paginated list, filterable by path, method, limit type |

Config body shape:

```
{
  "limitType": "AUTH | IP | NON_AUTH | GLOBAL_AUTH",
  "requestMethod": "GET | POST | PUT | DELETE | ...",
  "requestPath": "/api/{id} style pattern, wildcards supported",
  "refillRate": 5,
  "bucketCapacity": 100
}
```

- `refillRate`: tokens added per second.
- `bucketCapacity`: max burst size.

Limit scopes applied per enforcement call:

| Caller | Buckets checked |
|---|---|
| Authenticated (valid JWT) | Per-user bucket + shared global-authenticated bucket for that method + path |
| Anonymous (no / expired / logged-out JWT) | Per-IP bucket + shared global-anonymous bucket for that method + path |

Both applicable buckets must have a token or the request is rejected.

## Design decisions

* Token bucket in Redis, executed atomically with Lua: Each bucket is a
  Redis hash holding token count and last refill timestamp. A single script
  refills based on elapsed seconds, consumes one token if available, and
  refreshes the key TTL

* MySQL is the source of truth, memory is the hot path: All configs live
  in MySQL and are loaded into an in-memory snapshot at startup. Every
  create / update / delete rewrites the snapshot, so enforcement never hits
  the database.

* Pattern-based path matching: Incoming URIs are sanitized (query string
  dropped, trailing slash trimmed) and matched against configured patterns
  using Spring path patterns, so one config can cover e.g.
  `/api/**`. If no pattern matches, the request is rejected as an
  invalid URI rather than silently allowed.

* Shared auth state via JWT + Redis logout marker: User identity comes
  from the `access_token` JWT cookie. Signature and expiry are verified
  locally, and a Redis logout key is checked to honor explicit logouts. 
  Missing or invalid credentials simply fall through to the anonymous 
  limit path instead of failing the call.

* Per identity and global buckets are checked together: A per user (or
  per IP) bucket isolates abusive clients, while the parallel global bucket
  caps total load on an endpoint. Both must pass, so neither a single heavy
  user nor a distributed spike can overwhelm a downstream service.
