# event-integrity-replay-protection

Production-grade reference implementation of **Event Integrity + Replay Protection** for Kafka events.

## What this project demonstrates

- **HMAC-SHA256 event signatures** with **key rotation** (multiple key IDs supported)
- **Consumer-side validation**: signature verification + replay prevention
- **Event freshness validation**: reject stale or too-far-in-the-future events
- **Replay protection**:
  - Redis atomic put-if-absent with TTL (fast, distributed)
  - Postgres unique constraint as a durable safety net (recommended)
- **Dead-letter topic** (DLT) for forged/invalid/replayed/stale events
- **Audit tables** for received & rejected events
- **Correlation ID propagation** for operational debugging
- **Startup validation** for HMAC key configuration
- **Metrics** (Micrometer): processed / rejected / replay counters
- **Postman collection** included

## Production-grade improvements added

- Error-handling consumer deserialization aligned with Spring Kafka guidance
- Event age and future-skew validation
- Admin API key protection for internal admin endpoints
- Internal raw-envelope publish endpoint for deterministic negative testing
- Better startup key validation for rotation safety
- Duplicate DB insert handling translated into replay rejection behavior

## Run locally

### 1) Start infrastructure

```bash
docker compose up -d
```

### 2) Start the app

```bash
./gradlew bootRun
```

App runs on `http://localhost:8080`.

## Postman

Import:
- `postman/EventIntegrityReplayProtection.postman_collection.json`
- `postman/EventIntegrityReplayProtection.postman_environment.json`

Set environment values:
- `baseUrl=http://localhost:8080`
- `adminApiKey=change-me-admin-key`

Suggested flow:
1. Publish valid event
2. List received events
3. Publish raw replayed envelope with same eventId
4. Publish raw forged envelope with modified signature
5. List rejected events

## Endpoints

Public:
- `POST /api/events/publish`

Admin (header `X-Admin-Api-Key` required):
- `POST /api/admin/events/publish-raw`
- `GET /api/admin/events/received`
- `GET /api/admin/events/rejected`

## Key rotation

Configured in `application.yml`:
- `security.hmac.activeKeyId`
- `security.hmac.keys`

Typical rotation:
1. Add a new key to `keys`
2. Switch `activeKeyId` to the new key
3. Keep the old key for a grace period so consumers can still verify older events
4. Remove the old key after all producers are rotated

For real production, store secrets in KMS/Secrets Manager/Vault rather than local config.
