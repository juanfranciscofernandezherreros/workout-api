# workout-api

API-first Spring Boot microservice for storing running workouts as a system of record.

## Supplied requirements

The requested service stores detailed running sessions, including long runs, easy/recovery runs, tempo/threshold work, intervals, fartlek, progression runs, hills, races and other running workouts. A workout can contain ordered structural segments, recorded laps, GPS track points, and an optional running-shoe reference. It persists supplied measurements and does not invent coaching recommendations, pace calculations, race predictions, training load, recovery or fitness scores.

The public API contract is `src/main/resources/static/openapi.yaml`. Maven validates it and OpenAPI Generator produces Spring server interfaces that handwritten controllers implement. SQL Server is the persistence engine and Flyway owns schema creation/evolution.

## Inherited rules

Implementation follows `springboot-agent-rules`: Java 21, Spring Boot 4, Maven Wrapper, MVC, JPA, Bean Validation, Lombok, feature packages split by layers, controller/service/repository boundaries, explicit static mappers, global application exceptions, Spotless with Palantir Java Format, JUnit/Mockito/AssertJ, Cucumber via JUnit Platform, JaCoCo and SQL Server runtime persistence acceptance. Hibernate is configured with `ddl-auto=validate`.

## Implementation decisions

`Workout` is the aggregate root. `WorkoutSegment` records intended workout structure; `WorkoutLap` records supplied split/lap observations; `WorkoutTrackPoint` records optional supplied GPS samples. Child rows are physically cascade-deleted only with their owning workout. Running shoes are independent resources; deletion is restricted while referenced by workouts. Dynamic workout filtering uses Spring Data `JpaSpecificationExecutor`.

PATCH uses generated nullable/presence-aware fields: absent properties remain unchanged; explicit nullable scalar values clear optional fields; `segments`, `laps`, or `trackPoints` absent means unchanged, an empty array clears the collection, and a supplied array replaces it. `id` and `createdAt` are never patchable.

## Build and verification

Use the Maven Wrapper and the canonical command sequence from `docs/testing.md` in the rules repository. CI runs the compile/package gate and `spotless:check verify`. Reports are written under `target/site/jacoco/` and `target/cucumber/`.

## Run locally

Set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`, then run the application with the Maven Wrapper. Example local JDBC URL:

`jdbc:sqlserver://localhost:1433;databaseName=workoutdb;encrypt=true;trustServerCertificate=true`

## Docker

`docker compose up -d --build` starts SQL Server, an idempotent database initializer and the application. SQL Server data lives in named volume `workout-api-sqlserver-data`. Never use `docker compose down -v` for the persistence acceptance.

Run `scripts/runtime-acceptance.ps1` to create a shoe, long run, interval session and fartlek through the API, query SQL Server directly, stop without deleting the volume, restart, retrieve the same data and query SQL Server again.

## Endpoints

- `POST /api/v1/workouts`
- `GET /api/v1/workouts/{id}`
- `GET /api/v1/workouts` with Spring pagination and only `workoutType`, `workoutDate`, `workoutDateFrom`, `workoutDateTo`, `completed`, `shoeId`, `source` filters
- `PATCH /api/v1/workouts/{id}`
- `DELETE /api/v1/workouts/{id}`
- `POST /api/v1/shoes`
- `GET /api/v1/shoes/{id}`
- `GET /api/v1/shoes`
- `PATCH /api/v1/shoes/{id}`
- `DELETE /api/v1/shoes/{id}`

## curl examples

Long run:

```bash
curl -i -X POST http://localhost:8080/api/v1/workouts -H 'Content-Type: application/json' -d '{"workoutDate":"2026-09-20","name":"Sunday long run","workoutType":"LONG_RUN","distanceMeters":26000,"durationSeconds":8100,"averagePaceSecondsPerKm":312,"averageHeartRate":146,"completed":true,"source":"MANUAL"}'
```

Intervals:

```bash
curl -i -X POST http://localhost:8080/api/v1/workouts -H 'Content-Type: application/json' -d '{"workoutDate":"2026-09-22","name":"6 x 1000 m","workoutType":"INTERVALS","completed":true,"source":"MANUAL","segments":[{"position":1,"segmentType":"WARM_UP","distanceMeters":3000},{"position":2,"segmentType":"INTERVAL","repetitions":6,"distanceMeters":1000,"restDurationSeconds":120},{"position":3,"segmentType":"COOL_DOWN","distanceMeters":2000}]}'
```

Fartlek:

```bash
curl -i -X POST http://localhost:8080/api/v1/workouts -H 'Content-Type: application/json' -d '{"workoutDate":"2026-09-24","name":"10 x 1 min / 1 min","workoutType":"FARTLEK","completed":true,"source":"MANUAL","segments":[{"position":1,"segmentType":"FARTLEK_FAST","repetitions":10,"durationSeconds":60,"restDurationSeconds":60}]}'
```

Threshold, GET, search, PATCH and DELETE follow the same public OpenAPI contract. For example: `GET /api/v1/workouts?workoutType=LONG_RUN&page=0&size=20`, `PATCH /api/v1/workouts/1` with `{"notes":"felt good"}`, and `DELETE /api/v1/workouts/1`.

Create shoes with `POST /api/v1/shoes` and a body such as `{"brand":"Example","model":"Daily Trainer","initialDistanceMeters":0,"active":true}`.

## Tests and reports

Service unit tests cover application behavior. MVC tests are intended to exercise generated-interface HTTP semantics. Cucumber is wired explicitly through the JUnit Platform engine and writes `target/cucumber/cucumber.html` and `target/cucumber/cucumber.json`; Maven verification must discover at least one scenario. JaCoCo enforces at least 80% line coverage of application logic and excludes generated OpenAPI classes from the denominator.

Verification status must be reported from actual command execution only; inability to run Maven or Docker is not treated as a pass.
