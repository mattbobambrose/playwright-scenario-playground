# Shelf Move Persists On Refresh

**URL:** /login

A user drags a book between shelves and then refreshes the page. The move should persist — the book should appear in the new shelf after the refresh, not revert to the old one.

The drag dispatches a native form submission (`POST /shelves/move` → 303 → `GET /shelves`), so the drop itself is the commit; a subsequent refresh is just re-rendering the already-persisted state.

## Test 1: Drag a book from "Want to Read" to "Reading" and refresh immediately

- **Username:** alice
- **Password:** wonderland
- **Action:** Enter the username into the "Username" field.
- **Action:** Enter the password into the "Password" field.
- **Action:** Click the "Log in" button.
- **Expected:** The page navigates to `/`.
- **Action:** Put book 1 on the "Want to Read" shelf (via the book detail page dropdown).
- **Action:** Navigate to `/shelves`.
- **Expected:** The "Want to Read" column shows a count badge of "1" and contains book 1.
- **Expected:** The "Reading" column shows a count badge of "0".
- **Action:** Drag book 1 from the "Want to Read" column into the "Reading" column.
- **Expected:** The page navigates to `/shelves` (the drop's form submission commits the move before rendering).
- **Action:** Reload the page.
- **Expected:** After the reload, the "Reading" column shows a count badge of "1" and contains book 1.
- **Expected:** After the reload, the "Want to Read" column shows a count badge of "0" and the placeholder "Drop a book here".
