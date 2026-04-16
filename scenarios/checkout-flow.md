# Checkout form validation and happy-path order placement

**URL:** /catalog

## Test 1: Empty submit surfaces all six field-level errors

- **Action:** Navigate to `/books/1`, click `[data-testid="add-to-cart"]`.
- **Action:** On `/cart`, click `[data-testid="checkout-button"]`.
- **Expected:** The URL ends with `/checkout`.
- **Action:** Click `[data-testid="place-order"]` without filling any field.
- **Expected:** The URL still ends with `/checkout` (form rejected, no redirect).
- **Expected:** The page body contains "Please enter your full name.", "Email is required.", "Shipping address is required.", "Card number is required.", "Expiry is required.", and "CVC is required."
- **Expected:** Exactly six `[data-field-error="true"]` elements are attached.

## Test 2: Format-level errors call out bad email, card, expiry, and CVC values

- **Action:** Navigate to `/books/1`, click `[data-testid="add-to-cart"]`.
- **Action:** Navigate to `/checkout`.
- **Action:** Fill `[data-testid="input-name"]` with `Ada Lovelace`.
- **Action:** Fill `[data-testid="input-email"]` with `foo@bar` (passes the browser's built-in `type="email"` check but fails the server regex that requires a dot in the domain).
- **Action:** Fill `[data-testid="input-address"]` with `10 Downing St`.
- **Action:** Fill `[data-testid="input-cardNumber"]` with `1234`.
- **Action:** Fill `[data-testid="input-cardExpiry"]` with `13/99`.
- **Action:** Fill `[data-testid="input-cardCvc"]` with `ab`.
- **Action:** Click `[data-testid="place-order"]`.
- **Expected:** The URL still ends with `/checkout` (form rejected).
- **Expected:** The page body contains "That doesn't look like a valid email.", "Card number must be 13–19 digits.", "Use MM/YY format.", and "CVC must be 3 or 4 digits."

## Test 3: Valid checkout lands on the confirmation page

- **Action:** Navigate to `/books/1`, click `[data-testid="add-to-cart"]`.
- **Action:** Navigate to `/checkout`.
- **Action:** Fill `[data-testid="input-name"]` with `Ada Lovelace`.
- **Action:** Fill `[data-testid="input-email"]` with `ada@example.com`.
- **Action:** Fill `[data-testid="input-address"]` with `10 Downing St`.
- **Action:** Fill `[data-testid="input-cardNumber"]` with `4111111111111111`.
- **Action:** Fill `[data-testid="input-cardExpiry"]` with `04/30`.
- **Action:** Fill `[data-testid="input-cardCvc"]` with `123`.
- **Action:** Click `[data-testid="place-order"]`.
- **Expected:** The URL contains `/checkout/confirmation/`.
- **Expected:** `[data-testid="confirmation"]` is visible.
- **Expected:** `[data-testid="confirmation-id"]` has non-empty text.
- **Expected:** The page body contains "Thank you for your order!".
- **Expected:** After navigating to `/cart`, `[data-testid="cart-empty"]` is visible (cart cleared).
