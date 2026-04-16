# Cart add, update quantity, and remove

**URL:** /catalog

## Test 1: Add a book from its detail page lands on the cart

- **Action:** Navigate to `/books/1` (book 1 is not on catalog page 1 under the default sort).
- **Action:** Click `[data-testid="add-to-cart"]`.
- **Expected:** The URL ends with `/cart`.
- **Expected:** `[data-testid="cart-row-1"]` is visible.
- **Expected:** `[data-testid="cart-subtotal-1"]` has text "$14.99".
- **Expected:** `[data-testid="cart-total"]` has text "$14.99".
- **Expected:** The header badge `[data-testid="cart-count"]` has text "1".

## Test 2: Updating quantity recomputes subtotal and total

- **Action:** Navigate to `/books/1`, click `[data-testid="add-to-cart"]`.
- **Action:** On `/cart`, fill `[data-testid="cart-qty-1"]` with `3`.
- **Action:** Click `[data-testid="cart-update-1"]`.
- **Expected:** `[data-testid="cart-qty-1"]` has value `3`.
- **Expected:** `[data-testid="cart-subtotal-1"]` has text "$44.97".
- **Expected:** `[data-testid="cart-total"]` has text "$44.97".
- **Expected:** `[data-testid="cart-count"]` has text "3".

## Test 3: Adding a second book shows both rows and a combined total

- **Action:** Navigate to `/books/1`, click `[data-testid="add-to-cart"]`.
- **Action:** Navigate to `/books/2`, click `[data-testid="add-to-cart"]`.
- **Expected:** Both `[data-testid="cart-row-1"]` and `[data-testid="cart-row-2"]` are visible.
- **Expected:** `[data-testid="cart-total"]` has text "$33.98".
- **Expected:** `[data-testid="cart-count"]` has text "2".

## Test 4: Removing the last row reveals the empty state

- **Action:** Navigate to `/books/1`, click `[data-testid="add-to-cart"]`.
- **Action:** On `/cart`, click `[data-testid="cart-remove-1"]`.
- **Expected:** `[data-testid="cart-row-1"]` is not attached.
- **Expected:** `[data-testid="cart-empty"]` is visible containing the text "Your cart is empty."
- **Expected:** `[data-testid="cart-count"]` has text "0".
