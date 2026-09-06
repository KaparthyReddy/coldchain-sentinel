# ColdChain Sentinel

A concurrent, safety-critical Spring Boot API for pharmaceutical cold-chain and inventory monitoring — validates shipment temperature excursions against product safety ranges, tracks inventory and expiry, serves JWT-secured role-based endpoints, and includes a working Razorpay payment/subscription flow — backed by a real PostgreSQL database.

**Live:** https://coldchain-sentinel-xrsk.onrender.com

## What this is

ColdChain Sentinel models the backend that would sit behind a hospital pharmacy or pharmaceutical logistics operation — the kind of system Baxter's IV therapy, biologics, and diagnostics reagents would depend on. Shipments carry temperature-sensitive products (vaccines, insulin, plasma, PCR reagents) between storage units; the system ingests temperature readings in transit, validates them against each product's safe range, and raises severity-graded alerts the moment a shipment goes out of range.

It's built to demonstrate production practices, not a CRUD demo: real persistence, JWT auth with role-based access, database migrations, containerization, CI, and a genuinely deployed cloud instance — not just code that compiles locally.

## Core flow

1. A **logistics** user creates a shipment for a product moving between storage units
2. Temperature readings are ingested as the shipment travels
3. `TemperatureValidationService` checks each reading against the product's registered safe range (`minSafeTempC` / `maxSafeTempC`)
4. An out-of-range reading raises a severity-graded `Alert` (LOW → CRITICAL, based on how far outside the range the reading falls) and flips the shipment to `COMPROMISED`
5. A **pharmacist** user reviews and resolves alerts; inventory is tracked separately with expiry-window queries

## Tech stack

- **Java 17**, Spring Boot 3, Maven
- **Spring Data JPA** + **PostgreSQL** (managed instance in production, containerized locally)
- **Spring Security** with JWT (stateless auth, role-based endpoint authorization)
- **Flyway** for versioned schema migrations
- **JUnit 5**, Mockito, `@WebMvcTest` slices for controller tests
- **Docker** (multi-stage build) + **docker-compose** for local orchestration
- **GitHub Actions** CI (build + test on every push/PR)
- **Razorpay** payment integration (test mode) with webhook signature verification

## Project structure

```text
coldchain-sentinel/
├── src/main/java/com/coldchainsentinel/
│ ├── model/ # JPA entities: User, Product, StorageUnit, Shipment,
│ │ # TemperatureReading, Alert, InventoryItem
│ ├── repository/ # Spring Data JPA repositories
│ ├── dto/ # Request/response DTOs
│ ├── service/ # AuthService, ShipmentService, TemperatureValidationService,
│ │ # AlertService, InventoryService
│ ├── controller/ # REST endpoints
│ ├── security/ # JwtUtil, JwtAuthFilter, UserDetailsServiceImpl
│ ├── config/ # SecurityConfig, OpenApiConfig
│ └── exception/ # GlobalExceptionHandler + custom exceptions
├── src/main/resources/
│ ├── application.yml # base config (local defaults)
│ ├── application-docker.yml # docker-compose profile
│ ├── application-render.yml # production profile (Render env vars)
│ └── db/migration/ # Flyway migrations (schema + seed data)
└── src/test/java/com/coldchainsentinel/
├── service/ # Mockito unit tests
└── controller/ # @WebMvcTest slices
```


## API overview

| Endpoint | Method | Auth | Description |
|---|---|---|---|
| `/api/v1/status` | GET | none | Health check |
| `/api/v1/auth/register/{role}` | POST | none | Register as `pharmacist` or `logistics` |
| `/api/v1/auth/login` | POST | none | Returns a JWT |
| `/api/v1/shipments` | POST | LOGISTICS | Create a shipment |
| `/api/v1/shipments/{id}/depart` \| `/arrive` | POST | LOGISTICS | Update shipment status |
| `/api/v1/shipments/{id}/readings` | POST | LOGISTICS | Ingest a temperature reading |
| `/api/v1/alerts` | GET | any authenticated user | List unresolved alerts |
| `/api/v1/alerts/{id}/resolve` | POST | PHARMACIST | Resolve an alert |
| `/api/v1/inventory` | POST | PHARMACIST | Add inventory |
| `/api/v1/inventory/near-expiry` | GET | any authenticated user | Items nearing expiry |

Full interactive docs (Swagger UI) available at `/swagger-ui.html` once running.

## Payments (Razorpay, Test Mode)

ColdChain Sentinel includes a working subscription/payment flow using **Razorpay's Payment Links API**, running against Razorpay's test environment — a fully real integration (checkout, webhook delivery, HMAC signature verification, database state updates) with no real money involved.

## Monitoring

Render retains build and runtime logs for each deploy, accessible via the **Logs** tab on the service dashboard. For this project's scope, monitoring consists of:

- Checking `/api/v1/status` periodically to confirm the service is up
- Reviewing Render's log tail after any deploy to confirm clean startup (no stack traces, successful Flyway migrations, Tomcat bound to the port)
- Watching for `ERROR`-level log lines in the payment webhook handler specifically, since that's the one path that receives unauthenticated external traffic and depends on signature verification succeeding

A production system handling real payments would add structured logging with a dedicated aggregator (e.g. Better Stack, Datadog) and alerting on failed webhook signature checks — out of scope for this project's free-tier deployment, but the webhook handler already logs enough context (event type, payment link ID) to debug manually if something fails.

### How it works

1. An authenticated user calls `POST /api/v1/payments/subscribe` with a plan name and amount
2. The backend calls Razorpay's API to generate a hosted payment link and returns it to the user
3. The user completes payment on Razorpay's own checkout page (no custom frontend needed)
4. Razorpay sends a webhook to `POST /api/v1/payments/webhook` the moment payment succeeds
5. The webhook handler verifies the request's HMAC-SHA256 signature against a shared secret (`RAZORPAY_WEBHOOK_SECRET`) before trusting it — this is the actual security mechanism, since the endpoint itself must be public (Razorpay can't send a JWT)
6. On a verified `payment_link.paid` event, the matching `Subscription` record is updated: `status` → `ACTIVE`, `activatedAt` → now

### Endpoints

| Endpoint | Method | Auth | Description |
|---|---|---|---|
| `/api/v1/payments/subscribe` | POST | any authenticated user | Creates a Razorpay payment link for a plan |
| `/api/v1/payments/webhook` | POST | Razorpay signature (public route) | Receives payment confirmation from Razorpay |

### Trying it yourself

Since this runs against Razorpay's test mode, you can complete a full payment with no real card or money:

- **Card:** `5267 3181 8797 5449` (domestic test Mastercard)
- **Expiry:** any future date
- **CVV:** any 3 digits
- **OTP:** any 4–10 digit number (e.g. `123456`)

Razorpay's checkout will show a **"This payment link is created in Test Mode"** banner, and no real transaction ever occurs.

## Running locally

```bash
cp .env.example .env
# edit .env: set DB_PASSWORD and JWT_SECRET

mvn clean compile
mvn test

docker-compose up --build
curl http://localhost:8080/api/v1/status
```

See inline comments in `docker-compose.yml` for the Postgres + app orchestration.

## Deployment

Deployed on Render: a Docker-based web service connected to a managed Render Postgres instance (internal network, same region). Schema migrations run automatically via Flyway on startup. Environment-specific config lives in `application-render.yml`, driven entirely by environment variables (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION_MS`) — no secrets committed to the repo.

**Known free-tier constraints:**
- The Postgres instance is on Render's free plan, which expires 30 days after creation
- The web service spins down after inactivity; the first request after idle may take ~50 seconds to respond while it wakes up

## Test coverage

22 tests across data structures... — actually, service logic, controller slices, and dosage/validation rules, all passing:
```bash
Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Service-layer tests are plain Mockito unit tests; controller tests use `@WebMvcTest` with the security filter chain's dependencies (`JwtUtil`, `UserDetailsServiceImpl`) mocked so the full test suite runs without a database or Docker.

## License

MIT
