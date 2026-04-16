# Order Appears In Orders

**URL:** /login

An authenticated user completes a checkout and then visits the Orders page. The newly-placed order should appear there with the book title, total, and confirmation number.

## Test 1: Placing an order lists it on the Orders page

- **Username:** alice
- **Password:** wonderland
- **Action:** Enter the username into the "Username" field.
- **Action:** Enter the password into the "Password" field.
- **Action:** Click the "Log in" button.
- **Expected:** The page navigates to `/`.
- **Action:** Navigate to `/books/5`.
- **Expected:** The book detail page shows the heading "A Quiet Revolution".
- **Action:** Click the "Add to Cart" button.
- **Expected:** The page navigates to `/cart` and shows the heading "Your Cart" with a row for "A Quiet Revolution".
- **Action:** Navigate to `/checkout`.
- **Expected:** The checkout page shows the heading "Checkout".
- **Name:** Alice Liddell
- **Email:** alice@example.com
- **Address:** 7 Rabbit Hole Ln
- **Card number:** 4111111111111111
- **Card expiry:** 04/30
- **CVC:** 123
- **Action:** Enter the name into the "Full name" field.
- **Action:** Enter the email into the "Email" field.
- **Action:** Enter the address into the "Shipping address" field.
- **Action:** Enter the card number into the "Card number" field.
- **Action:** Enter the expiry into the "Expiry (MM/YY)" field.
- **Action:** Enter the CVC into the "CVC" field.
- **Action:** Click the "Place order" button.
- **Expected:** The page navigates to a URL matching `/checkout/confirmation/ORD-*`.
- **Expected:** The confirmation page shows the heading "Thank you for your order!" and the text "Your confirmation number is ORD-".
- **Action:** Navigate to `/orders`.
- **Expected:** The orders page shows the heading "Your orders".
- **Expected:** The orders page no longer shows the text "You haven't placed any orders yet.".
- **Expected:** The orders page shows an order heading beginning with "ORD-".
- **Expected:** The orders page shows the text "A Quiet Revolution × 1" and a total of "$16.99".
