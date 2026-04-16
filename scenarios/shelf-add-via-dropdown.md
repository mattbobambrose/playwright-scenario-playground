# Shelf Add Via Dropdown

**URL:** /login

An authenticated user adds a book to a specific shelf by picking the shelf from the dropdown on the book detail page and clicking "Add to Shelf". The chosen shelf column on `/shelves` should gain the book and its count badge should increment.

## Test 1: Adding "The Lunar Protocol" to the Finished shelf

- **Username:** alice
- **Password:** wonderland
- **Action:** Enter the username into the "Username" field.
- **Action:** Enter the password into the "Password" field.
- **Action:** Click the "Log in" button.
- **Expected:** The page navigates to `/`.
- **Action:** Navigate to `/books/8`.
- **Expected:** The book detail page shows the heading "The Lunar Protocol".
- **Expected:** The page shows a shelf dropdown with options "Want to Read", "Reading", and "Finished".
- **Action:** Select "Finished" from the shelf dropdown.
- **Action:** Click the "Add to Shelf" button.
- **Expected:** The URL becomes `/books/8?added=Finished`.
- **Expected:** The page shows the text "Finished added.".
- **Action:** Navigate to `/shelves`.
- **Expected:** The "Finished" shelf column shows a count badge of "1".
- **Expected:** The "Finished" shelf column lists "The Lunar Protocol".
- **Expected:** The "Want to Read" column shows a count badge of "0" and the placeholder "Drop a book here".
- **Expected:** The "Reading" column shows a count badge of "0" and the placeholder "Drop a book here".
