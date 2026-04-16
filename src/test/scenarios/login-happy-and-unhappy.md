# Login Happy And Unhappy

**URL:** /login

A visitor attempts to log in, first with bad credentials, then with a valid demo account. The header and navigation should reflect the authenticated state after success.

## Test 1: Invalid credentials re-render the form with an error

- **Username:** alice
- **Password:** wrongpass
- **Action:** Enter the username into the "Username" field.
- **Action:** Enter the password into the "Password" field.
- **Action:** Click the "Log in" button.
- **Expected:** The page stays on `/login`.
- **Expected:** The page shows the text "Invalid username or password.".
- **Expected:** The header still shows a "Log in" link (the user is not authenticated).

## Test 2: Valid credentials land on the home page with an authenticated header

- **Username:** alice
- **Password:** wonderland
- **Action:** Enter the username into the "Username" field.
- **Action:** Enter the password into the "Password" field.
- **Action:** Click the "Log in" button.
- **Expected:** The page navigates to `/`.
- **Expected:** The header shows the text "Hi, alice" and a "Log out" button.
- **Expected:** The navigation includes a "My Shelves" link pointing to `/shelves` and an "Orders" link pointing to `/orders`.
- **Expected:** The header no longer shows a "Log in" link.
