# Sci-Fi Book Checkout

**URL:** http://0.0.0.0:8080/

A visitor filters the catalog to sci-fi, sorts by ascending price, adds a book to their cart, and proceeds to checkout.

## Test 1: Filter sci-fi books and reach checkout

- **Expected:** The landing page shows the heading "Your next favorite book is on the shelf.".
- **Expected:** The landing page shows a "Browse the catalog" link and a "Log in" link.
- **Action:** Click the "Browse the catalog" link.
- **Expected:** The catalog page shows the heading "Catalog".
- **Action:** Select "Sci-Fi" from the Genre dropdown.
- **Action:** Select "Price (low → high)" from the Sort dropdown.
- **Action:** Click the "Apply" button.
- **Expected:** The filtered results list "Orbital" first.
- **Action:** Click the "Orbital" link in the results.
- **Expected:** The book detail page shows the heading "Orbital".
- **Action:** Click the "Add to Cart" button.
- **Expected:** The cart page shows the heading "Your Cart" and a row for "Orbital" priced at "$15.99".
- **Action:** Click the "Check out" link.
- **Expected:** The checkout page shows the heading "Checkout".
- **Expected:** The order summary shows "Orbital × 1".
