# playwright-scenario-playground

A "Bookstore" demo site — a small, self-contained web app for exercising the [`playwright-scenarios`](https://github.com/mattbobambrose/playwright-scenarios) Claude Code plugin against a live site you control. Login, catalog, search, cart, checkout, shelves with drag-and-drop, and order history — enough surface to demo every authoring path the plugin supports.

## Quick start

```bash
docker run --rm -p 8080:8080 mattbobambrose/playwright-scenario-playground
```

Then open <http://localhost:8080>. State (carts, shelves, orders) lives only in memory and resets on restart, so you can demo destructive flows without cleanup.

The site is implemented in **both Kotlin/Ktor and TypeScript/Express**, serving the same pages with the same `data-testid` attributes, routes, validation rules, and demo data — so the same scenario markdown files validate both. The published Docker image ships the Kotlin build. To run from source (either implementation), see [Running from source](#running-from-source) below.

## Running from source

### Requirements

- **Kotlin:** JDK 21+ (the Kotlin JVM toolchain is pinned to 21 in `build.gradle.kts`)
- **TypeScript:** Node.js 20+
- macOS, Linux, or Windows with a Bourne-compatible shell

### Kotlin (port 8080)

```bash
./gradlew run
```

### TypeScript (port 3000)

```bash
cd typescript && npm install && npm start
```

Both servers can run simultaneously. State (carts, shelves, orders) lives only in memory and resets on restart.

A `Makefile` is also provided with shortcuts:

```bash
make build          # ./gradlew build -x test
make run            # ./gradlew run
make tests          # ./gradlew test
make versioncheck   # ./gradlew dependencyUpdates
make docker-local   # build the container image into the local Docker daemon
make docker-run     # run the local image on port 8080
make docker-push    # build multi-arch and push to the registry
```

### Docker

```bash
make docker-local && make docker-run
```

Then open <http://localhost:8080>. The image is based on `eclipse-temurin:21-jre-alpine`, runs as a non-root user, and uses `-XX:MaxRAMPercentage=75.0` so the JVM scales with whatever memory the container is given.

## Demo accounts

| Username | Password    |
| -------- | ----------- |
| `demo`   | `demo`      |
| `alice`  | `wonderland`|
| `bob`    | `password1` |

## Pages

| Path | Notes |
|------|-------|
| `/` | Static home (hero + featured books) |
| `/about` | Static about page |
| `/catalog` | Search, genre filter, sort, paginated (6/page) |
| `/books/{id}` | Detail page — Add to Cart / Add to Shelf |
| `/cart` | Line items, qty updates, remove |
| `/checkout` | Multi-field form with server-side validation |
| `/login` | Hardcoded credentials, `?next=` support |
| `/shelves` | Three shelves with native HTML5 drag-and-drop between and within columns |
| `/orders` | Past orders for the logged-in user |

## Suggested prompts

With the `playwright-scenarios` plugin installed and the server running, try these prompts in Claude Code. All of them target the live site at `http://localhost:8080`.

**End-to-end, no follow-up questions.** One prompt, hands on the browser once, everything else runs on autopilot:

> Crawl the site and pick the most valuable scenario we're not covering yet. Kick off `/record-scenario` — I'll drive the browser when it opens. When I close the window, run the full `/review-scenario` → `/scenario-to-tests` pipeline and the generated test at the end. Don't ask me any clarifying questions; make the calls yourself.

**Batch creation, fully autonomous.** No browser driving — Claude writes the scenarios by hand, verifies them against the live site, and generates the tests:

> Crawl the site and pick the top 3 scenarios we're not covering yet. For each one, hand-write the scenario markdown directly into `scenarios/` using the `authoring-scenarios` skill — do NOT use `/record-scenario`. Then run `/review-scenario` across all three to verify them against the live site, then `/scenario-to-tests` to generate the Kotest files and run them. Don't ask me any clarifying questions.

**Targeted single scenario.** Name the flow up front, then just drive the browser:

> `/record-scenario checkout-invalid-email` starting at `/catalog`.

**Audit existing scenarios against reality.** No recording, just re-verify the markdown against the live site:

> `/review-scenario`

**Generate tests from reviewed scenarios.** Reads `scenarios/*.md` and writes Kotest files under the configured test directory:

> `/scenario-to-tests`

**Just brainstorm.** Get a prioritized list without kicking off any automation:

> Crawl the site and give me a prioritized list of scenarios worth recording, grouped by whether they fill a coverage gap or demonstrate a specific scenario-format feature.

## Project structure

```
scenarios/                         shared scenario markdown (both implementations)
src/main/kotlin/                   (package com.mattbobambrose; flat layout)
  Application.kt       module wiring
  Monitoring.kt        call-logging install
  routing/             one file per feature area (static, catalog, cart, checkout, auth, shelf, order)
  html/                Kotlin HTML DSL pages + shared Layout
  model/               Book, Cart, Order, Shelf, User types
  data/                hardcoded Catalog + Users, in-memory Stores
  session/             cookie-based UserSession / GuestSession
src/main/resources/
  application.yaml     Ktor deployment config (port, modules)
  logback.xml          logging config
  banner/banner.txt    ASCII banner logged on startup
src/test/kotlin/       Kotest StringSpec tests
typescript/
  src/
    app.ts             Express app factory
    server.ts          entry point (port 3000)
    routes/            one file per feature area (mirrors Kotlin routing/)
    views/             tagged template literals (mirrors Kotlin html/)
    model/types.ts     Book, Cart, Order, Shelf, User types
    data/              same 20 books, 3 users, in-memory stores
    session/           express-session middleware
gradle/libs.versions.toml   version catalog (Kotlin deps)
gradle.properties           group, version, Gradle perf flags
Dockerfile                  multi-arch container image (linux/amd64, linux/arm64)
.dockerignore               build-context exclusions
Makefile                    convenience targets (build/run/test/docker-*)
```

## Testing

```bash
./gradlew test                     # Kotlin route tests
```

Tests are Kotest `StringSpec` using `testApplication { ... }`. See `src/test/kotlin/TestSupport.kt` for the shared `testClient()` and `loginAs()` helpers.

## Switching the plugin between implementations

The scenario markdown files in `scenarios/` work for both. To switch which implementation `/scenario-to-tests` generates tests for, edit `.claude/playwright-scenarios.local.md`:

```yaml
# Kotlin
test_dir: src/test/kotlin/com/mattbobambrose/examples
test_language: kotlin
test_framework: kotest-stringspec

# TypeScript (not yet supported by /scenario-to-tests)
test_dir: typescript/tests/scenarios
test_language: typescript
test_framework: playwright-test
```

## Drag-and-drop

The shelves page uses standard HTML5 drag-and-drop (`draggable`, `dragstart`, `dragover`, `drop`). On drop, a native form submission POSTs to `/shelves/move` or `/shelves/reorder` (303 redirect back to `/shelves`), ensuring the persisted state is always what the user sees — no async fetch race on refresh.

Playwright's `locator.dragTo()` works directly against this markup — no custom helpers needed.
