# Shelves: add, drag between shelves, and reorder within a shelf

**URL:** /login

Uses the demo account `demo / demo`. The shelves page requires authentication — unauthenticated visits redirect to `/login`.

## Test 1: Logging in as demo lands on the home page with the shelves nav link

- **Action:** Fill `[data-testid="input-username"]` with `demo`.
- **Action:** Fill `[data-testid="input-password"]` with `demo`.
- **Action:** Click `[data-testid="login-submit"]`.
- **Expected:** The URL ends with `/`.
- **Expected:** `[data-testid="nav-user"]` has text "Hi, demo".
- **Expected:** `[data-testid="nav-shelves"]` is visible.

## Test 2: Adding a book to "Reading" from the book detail page shows a flash

- **Action:** Log in as `demo / demo`.
- **Action:** Navigate to `/books/1`.
- **Action:** In `[data-testid="shelf-select"]`, select the value `reading`.
- **Action:** Click `[data-testid="add-to-shelf"]`.
- **Expected:** The URL contains `/books/1?added=Reading`.
- **Expected:** `[data-testid="flash-success"]` has text "Reading added."

## Test 3: The shelves page shows each shelf column with a count badge

- **Action:** Log in as `demo / demo`.
- **Action:** Navigate to `/books/1`. In `[data-testid="shelf-select"]`, select `want`. Click `[data-testid="add-to-shelf"]`.
- **Action:** Navigate to `/books/2`. In `[data-testid="shelf-select"]`, select `reading`. Click `[data-testid="add-to-shelf"]`.
- **Action:** Navigate to `/shelves`.
- **Expected:** `[data-testid="shelf-column-want"]`, `[data-testid="shelf-column-reading"]`, and `[data-testid="shelf-column-finished"]` are all visible.
- **Expected:** `[data-testid="shelf-count-want"]` has text "1" and `[data-testid="shelf-count-reading"]` has text "1".
- **Expected:** `[data-testid="shelf-book-1"]` is inside `[data-testid="shelf-want"]` and `[data-testid="shelf-book-2"]` is inside `[data-testid="shelf-reading"]`.
- **Expected:** `[data-testid="shelf-empty-finished"]` is visible with text "Drop a book here".

## Test 4: Dragging a book between shelves moves it and updates the counts

Playwright's built-in `dragTo` is unreliable for HTML5 native drag-and-drop in headless Chromium, so tests dispatch `dragstart`/`dragover`/`drop` events via `page.evaluate` — that's what a real user's drag would produce and it's what the page's inline JS listens for.

- **Action:** Log in as `demo / demo`.
- **Action:** Navigate to `/books/1`. Add to shelf `want`.
- **Action:** Navigate to `/shelves`.
- **Action:** Dispatch an HTML5 drag of `[data-testid="shelf-book-1"]` onto `[data-testid="shelf-reading"]`.
- **Expected:** `[data-testid="shelf-book-1"]` is a descendant of `[data-testid="shelf-reading"]` (not `shelf-want`).
- **Expected:** `[data-testid="shelf-count-want"]` has text "0" and `[data-testid="shelf-count-reading"]` has text "1".
- **Expected:** `[data-testid="shelf-empty-want"]` is visible.
- **Action:** Reload the page.
- **Expected:** After reload, `[data-testid="shelf-book-1"]` is still a descendant of `[data-testid="shelf-reading"]` (server-side persistence).

## Test 5: Reordering within a shelf persists across reloads

- **Action:** Log in as `demo / demo`.
- **Action:** Navigate to `/books/1`, add to shelf `reading`. Then `/books/2`, add to `reading`. Then `/books/3`, add to `reading`.
- **Action:** Navigate to `/shelves`.
- **Action:** Dispatch an HTML5 drag of `[data-testid="shelf-book-3"]` onto `[data-testid="shelf-book-1"]` (drop targets the parent `ul.shelf`; `beforeBookId` resolves to book 1 so book 3 lands at the top).
- **Action:** Reload the page.
- **Expected:** Inside `[data-testid="shelf-reading"]`, the first `li.book-card` is `[data-testid="shelf-book-3"]` and the third is `[data-testid="shelf-book-2"]`.
