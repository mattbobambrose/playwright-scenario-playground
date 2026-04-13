# playwright-scenarios

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
