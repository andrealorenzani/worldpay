![Upgraded with Claude](https://img.shields.io/badge/Upgraded%20with-Claude-D97757)

# Worldpay Offer microservice

A REST service to create, query and cancel **offers**: something a vendor sells at a given price for a limited time.
It was written in 2018 as the solution to a technical test.

* `POST /offer` creates an offer. It expires after `duration` seconds (default one day).
* `GET /offer` finds the offers that did not expire, by title, description, vendor and price range.
* `DELETE /offer/{offerId}/{memorableWord}` cancels an offer before it expires. The memorable word is chosen by the
  vendor when the offer is created.

## Upgraded after 8 years

The project was written in 2018 (Spring Boot 2.0.3, Java 8) and was upgraded in 2026 with Claude. It had been frozen
on dependencies that no longer exist or work: SpringFox (abandoned, does not run on current Spring), Spring Cloud
Contract 2.0 and Config Client 2.0, Lombok and the `javax` packages. The API (paths,
parameters, JSON field names, error codes) is the same except for the points marked **fix** below.

| Area | Before (2018) | Now |
|------|---------------|-----|
| Java | 8 | 21 |
| Spring Boot | 2.0.3 | 4.1.2 (Spring Framework 7, Hibernate 7, Jakarta EE 11) |
| API documentation | SpringFox 2.7 (Swagger 2) + hand-written `swagger.yml` | springdoc-openapi 3.2 (OpenAPI 3.1), committed in `docs/openapi.json` |
| Boilerplate | Lombok | records for requests/responses |
| Contract tests | Spring Cloud Contract (Groovy), WireMock, RestAssured, assertj-swagger, shazamcrest | MockMvc + a test that compares the generated OpenAPI with `docs/openapi.json` |
| Tests | JUnit 4, 12 tests plus 12 generated contract tests | JUnit 5 and AssertJ, 39 tests |
| Cloud config | Spring Cloud Config client / `bootstrap.yml`, a `local` profile was mandatory | removed: `mvn spring-boot:run` is enough |

What changed in the code:

* **Layers**: `controller` (HTTP), `service` (rules), `dao` (queries), `model` (the stored entity plus request and
  response records). The entity `StoredOffer` is never serialised: clients get an `OfferView`, so a field added to the
  table cannot leak by accident (the memorable word was hidden with `@JsonIgnore`).
* **Time is a `Clock`**, and `Date`/`DateTime` became `Instant`. Tests don't depend on the current time any more.
* **Memorable words are hashed** with BCrypt (just `spring-security-crypto`, not the whole of Spring Security). They
  used to be stored, and logged, in the clear. They are not logged any more.
* **fix** `expiration` is ISO-8601 (`2026-07-21T17:32:28Z`), as the swagger contract always said. The code serialised
  `dd-MM-yyyy hh:mm:ss`: 12-hour clock without AM/PM and without a time zone.
* **fix** Validation: texts must not be blank (not only non-null), the price must not be negative and the duration
  must be positive. `price`, `minPrix` and `maxPrix` accept decimals.
* **fix** Malformed JSON, an unknown currency or a non-numeric price used to answer `500`; they answer `400` with
  code `03`. Unknown paths answer `404` (code `404`) and wrong methods `405`, they used to be `500` as well.
* **fix** Unexpected errors answer `{"code":"99","message":"Unexpected error"}` instead of the text of the exception.
* **fix** Results are sorted (by expiration, then id), and the currency is stored as text instead of its ordinal, so
  reordering the enum can't change the existing offers.
* The query is a single JPQL statement with a `:now` parameter; the unused `deleteIfMemorable` query, the `DateFormat`
  validator, the SOAP/`WebServiceTemplate`, `@EnableAsync`, `@EnableCaching` and the commented ELK appender were removed.
* Actuator exposes only `health` and `info`. `env` and `beans` (they used to be exposed too) show the internals of
  the application and don't belong on a public port.

## Requirements

JDK 21 or newer.

## Run

```bash
./mvnw spring-boot:run
```

Everything is in an in-memory H2 database, so the offers are lost when the application stops. The service listens on
port `9000`.

* Swagger UI: <http://localhost:9000/swagger-ui.html>
* OpenAPI document: <http://localhost:9000/v3/api-docs> (the published copy is [docs/openapi.json](docs/openapi.json))
* Health: <http://localhost:9000/actuator/health>, build information: <http://localhost:9000/actuator/info>

```bash
curl -X POST localhost:9000/offer -H 'Content-Type: application/json' -d '{
  "title": "New Tesla Model 3", "description": "At a special price only in our shop",
  "vendor": "Andrea Lorenzani Spa.", "price": 100000, "currency": "EUR", "duration": 86400,
  "memorableWord": "pa$$w0rd"}'
curl 'localhost:9000/offer?title=tesla&minPrix=50000'
curl -X DELETE 'localhost:9000/offer/<id>/pa$$w0rd'
```

Errors are `{"code": ..., "message": ...}`: `02` the request cannot be done (wrong id or memorable word, offer already
expired), `03` invalid input, `01` internal error, `99` unexpected error.

### Search parameters

| Parameter | Meaning |
|-----------|---------|
| `title`, `description` | the text is contained (ignoring the case) |
| `vendor` | the same vendor (ignoring the case) |
| `minPrix`, `maxPrix` | price range, both included |

Expired offers are never returned.

## Build and test

```bash
./mvnw verify
```

The tests use fixed time (`TestClockConfig`) and cover validation, the service and queries on a real database,
the HTTP layer and error mapping, and the OpenAPI contract. After changing the API on purpose, update the contract with
`OPENAPI_UPDATE=true ./mvnw test -Dtest=OpenApiContractTest`.

## Release notes

### v2.0.0 (2026)
The upgrade described above.

### v1.0.1
* Offers can be deleted only when they are not expired (the test said: *Offers may also be explicitly cancelled before they expire*).
* Cleaned the pom and the code; an expired offer cannot be retrieved.

### v1.0.0
* H2 in-memory database, script to populate the test database, Swagger UI, the swagger contract, contract tests for
  create, retrieve and delete.

## License

[Apache License 2.0](LICENSE), Copyright 2018-2026 Andrea Lorenzani.
