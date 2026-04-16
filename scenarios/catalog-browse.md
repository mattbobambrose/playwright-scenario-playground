# Catalog search, filter, sort, and pagination

**URL:** /catalog

## Test 1: Catalog grid renders the first page of books

- **Expected:** The `h1` "Catalog" is visible.
- **Expected:** The `[data-testid="catalog-grid"]` is visible and contains 6 `[data-testid^="book-card-"]` cards.
- **Expected:** With the default Title (A–Z) sort, the first card is `[data-testid="book-card-5"]` ("A Quiet Revolution").

## Test 2: Genre filter narrows the result set

- **Action:** In `[data-testid="filter-genre"]`, select the value `scifi`.
- **Action:** Click `[data-testid="filter-apply"]`.
- **Expected:** The URL contains `genre=scifi`.
- **Expected:** `[data-testid="catalog-grid"]` contains exactly 4 book cards: `book-card-2`, `book-card-8`, `book-card-13`, `book-card-18`.
- **Expected:** `[data-testid="pagination"]` is not present (single page).

## Test 3: Search box narrows by title or author

- **Action:** Fill `[data-testid="filter-q"]` with `Mars`.
- **Action:** Click `[data-testid="filter-apply"]`.
- **Expected:** The URL contains `q=Mars`.
- **Expected:** Exactly one card is visible: `[data-testid="book-card-2"]` ("Echoes of Mars").

## Test 4: Empty state appears when no books match

- **Action:** Fill `[data-testid="filter-q"]` with `zzzzzzz`.
- **Action:** Click `[data-testid="filter-apply"]`.
- **Expected:** `[data-testid="catalog-grid"]` is not attached.
- **Expected:** `[data-testid="catalog-empty"]` is visible with text "No books match your search."

## Test 5: Clear link resets filters

- **Action:** Fill `[data-testid="filter-q"]` with `Mars`, click Apply.
- **Action:** Click `[data-testid="filter-clear"]`.
- **Expected:** The URL ends with `/catalog` (no query string).
- **Expected:** `[data-testid="catalog-grid"]` contains 6 cards again.

## Test 6: Pagination advances to page 2

- **Expected:** `[data-testid="pagination-next"]` is visible on page 1.
- **Action:** Click `[data-testid="pagination-next"]`.
- **Expected:** The URL contains `page=2`.
- **Expected:** The pagination label reads "Page 2 of 4".
- **Expected:** `[data-testid="book-card-5"]` (the page-1 first card) is no longer attached.

## Test 7: Book card link navigates to the detail page

With the default Title (A–Z) sort, `book-card-5` ("A Quiet Revolution") is the first card on page 1 — use it so the test runs without paginating.

- **Action:** Click the "A Quiet Revolution" link inside `[data-testid="book-card-5"]`.
- **Expected:** The URL ends with `/books/5`.
- **Expected:** `[data-testid="book-title"]` has text "A Quiet Revolution".
- **Expected:** `[data-testid="book-price"]` has text "$16.99".
