# playwright-scenario-playground

## Architecture
- Ktor + Kotlin HTML DSL; all feature areas split per file under `routing/` and `html/`
- In-memory state only (`data/Stores.kt`); tests use `runTestApp { }` which calls `Stores.resetAll()` first
- Session storage via Ktor `Sessions` plugin; `UserSession` (logged-in) + `GuestSession` (anonymous cart)
- JDK 21 toolchain pinned in `build.gradle.kts`; dependencies managed via `gradle/libs.versions.toml`

## Non-obvious gotchas
- Ktor `Sessions` data classes MUST be `@Serializable` (kotlinx.serialization) — failures surface as `Serializer for class 'X' is not found` at request time, not compile time
- Use `respondSeeOther(url)` (303) for POST→GET redirects, not `respondRedirect` (302). 303 interacts cleanly with Ktor client test redirect following and the PRG pattern
- In Kotlin HTML DSL, `input { id = "x" }` and `select { id = "x" }` don't compile — use `attributes["id"] = "x"` instead
- `respondPage(title, status, content)` accepts a custom status — pass `HttpStatusCode.BadRequest` when re-rendering a form with validation errors, otherwise `respondHtml` defaults to 200 and overrides any `call.response.status(...)` set before it

## Testing
- Kotest StringSpec with `init { }` per the global rule
- Use `runTestApp { client -> ... }` from `src/test/kotlin/TestSupport.kt`
- Test client has `HttpCookies` but no auto-redirect plugin — follow redirects explicitly via `client.followRedirectAsGet(response)` (POST 303 to a GET endpoint would otherwise re-POST and 405)
- `./gradlew test` for the suite; `./gradlew build` runs tests too

## Manual verification
- `./gradlew run` → http://localhost:8080; demo users in README
- When done, kill with `lsof -ti:8080 | xargs -r kill`
