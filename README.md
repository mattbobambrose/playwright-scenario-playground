# playwright-scenario-playground

A Ktor-based "Bookshelf" demo site used as a target for the `/record-scenario`, `/review-scenario`, and `scenario-to-tests` skills in this marketplace.

The site mixes static marketing pages, dynamic HTML-DSL views, a session-backed cart, hardcoded login, form validation with field-level errors, and a drag-and-drop "My Shelves" page — enough surface area to exercise a wide range of Playwright scenarios against a single small app.

## Requirements

- JDK 21+ (the Kotlin JVM toolchain is pinned to 21 in `build.gradle.kts`)
- macOS, Linux, or Windows with a Bourne-compatible shell for the Gradle wrapper

## Running

```bash
./gradlew run
```

Server listens on `http://localhost:8080` (configurable via `src/main/resources/application.yaml`). State (carts, shelves, orders) lives only in memory and resets on restart.

A `Makefile` is also provided with shortcuts:

```bash
make build   # ./gradlew clean build
make run     # ./gradlew run
make tests   # ./gradlew --rerun-tasks check
```

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

> Crawl the site and pick the top 3 scenarios we're not covering yet. For each one, hand-write the scenario markdown directly into `src/test/scenarios/` using the `authoring-scenarios` skill — do NOT use `/record-scenario`. Then run `/review-scenario` across all three to verify them against the live site, then `/scenario-to-tests` to generate the Kotest files and run them. Don't ask me any clarifying questions.

**Targeted single scenario.** Name the flow up front, then just drive the browser:

> `/record-scenario checkout-invalid-email` starting at `/catalog`.

**Audit existing scenarios against reality.** No recording, just re-verify the markdown against the live site:

> `/review-scenario`

**Generate tests from reviewed scenarios.** Reads `src/test/scenarios/*.md` and writes Kotest files under the configured test directory:

> `/scenario-to-tests`

**Just brainstorm.** Get a prioritized list without kicking off any automation:

> Crawl the site and give me a prioritized list of scenarios worth recording, grouped by whether they fill a coverage gap or demonstrate a specific scenario-format feature.

## Project structure

```
src/main/kotlin/com/mattbobambrose/
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
src/test/kotlin/       Kotest StringSpec tests (ApplicationTest, CatalogTest, AuthTest, CartTest, CheckoutTest, ShelfTest)
gradle/libs.versions.toml   version catalog for all dependencies and plugins
```

## Testing

```bash
./gradlew test
```

Tests are Kotest `StringSpec` using `testApplication { ... }`. See `src/test/kotlin/TestSupport.kt` for the shared `testClient()` and `loginAs()` helpers.

## Drag-and-drop

The shelves page uses standard HTML5 drag-and-drop (`draggable`, `dragstart`, `dragover`, `drop`). On drop, a tiny inline script POSTs JSON to `/shelves/move` or `/shelves/reorder`, and the server persists the new order.

Playwright's `locator.dragTo()` works directly against this markup — no custom helpers needed.
