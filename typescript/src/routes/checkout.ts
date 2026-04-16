import { Router } from 'express';
import { findById } from '../data/catalog.js';
import {
  cartFor,
  cartCountFor,
  addOrder,
  nextOrderId,
  findOrder,
} from '../data/stores.js';
import { cartKey, cartKeyOrNull } from '../session/middleware.js';
import { renderCheckout, renderConfirmation } from '../views/checkout.js';
import type { Order } from '../model/types.js';

const router = Router();

// ---------------------------------------------------------------------------
// Validation regexes (matching Kotlin exactly)
// ---------------------------------------------------------------------------

const emailRegex = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;
const cardDigitsRegex = /^[0-9]{13,19}$/;
const expiryRegex = /^(0[1-9]|1[0-2])\/\d{2}$/;
const cvcRegex = /^[0-9]{3,4}$/;

interface CheckoutFields {
  name: string;
  email: string;
  address: string;
  cardNumber: string;
  cardExpiry: string;
  cardCvc: string;
}

function validateCheckout(form: CheckoutFields): Record<string, string> {
  const errors: Record<string, string> = {};

  if (!form.name.trim()) {
    errors.name = 'Please enter your full name.';
  }

  if (!form.email.trim()) {
    errors.email = 'Email is required.';
  } else if (!emailRegex.test(form.email)) {
    errors.email = "That doesn't look like a valid email.";
  }

  if (!form.address.trim()) {
    errors.address = 'Shipping address is required.';
  }

  const cardDigits = form.cardNumber.replace(/ /g, '').replace(/-/g, '');
  if (!cardDigits) {
    errors.cardNumber = 'Card number is required.';
  } else if (!cardDigitsRegex.test(cardDigits)) {
    errors.cardNumber = 'Card number must be 13\u201319 digits.';
  }

  if (!form.cardExpiry.trim()) {
    errors.cardExpiry = 'Expiry is required.';
  } else if (!expiryRegex.test(form.cardExpiry)) {
    errors.cardExpiry = 'Use MM/YY format.';
  }

  if (!form.cardCvc.trim()) {
    errors.cardCvc = 'CVC is required.';
  } else if (!cvcRegex.test(form.cardCvc)) {
    errors.cardCvc = 'CVC must be 3 or 4 digits.';
  }

  return errors;
}

// GET /checkout — render checkout form (redirect to /cart if empty)
router.get('/checkout', (req, res) => {
  const items = cartFor(cartKey(req)).snapshot();
  if (items.length === 0) {
    res.redirect(303, '/cart');
    return;
  }
  const opts = {
    username: req.session.username,
    cartCount: cartCountFor(cartKeyOrNull(req)),
  };
  res.send(renderCheckout(items, findById, undefined, undefined, opts));
});

// POST /checkout — validate and place order
router.post('/checkout', (req, res) => {
  const form: CheckoutFields = {
    name: (req.body.name ?? '').trim(),
    email: (req.body.email ?? '').trim(),
    address: (req.body.address ?? '').trim(),
    cardNumber: (req.body.cardNumber ?? '').trim(),
    cardExpiry: (req.body.cardExpiry ?? '').trim(),
    cardCvc: (req.body.cardCvc ?? '').trim(),
  };

  const items = cartFor(cartKey(req)).snapshot();
  if (items.length === 0) {
    res.redirect(303, '/cart');
    return;
  }

  const errors = validateCheckout(form);

  if (Object.keys(errors).length > 0) {
    const opts = {
      username: req.session.username,
      cartCount: cartCountFor(cartKeyOrNull(req)),
    };
    res.status(400).send(renderCheckout(items, findById, form, errors, opts));
    return;
  }

  const username = req.session.username ?? 'guest';
  const totalCents = items.reduce((sum, item) => {
    const book = findById(item.bookId);
    return sum + (book ? book.priceCents * item.qty : 0);
  }, 0);

  const order: Order = {
    id: nextOrderId(),
    username,
    items,
    totalCents,
    shippingName: form.name,
    shippingAddress: form.address,
    placedAt: new Date(),
  };
  addOrder(order);
  cartFor(cartKey(req)).clear();
  res.redirect(303, `/checkout/confirmation/${order.id}`);
});

// GET /checkout/confirmation/:orderId — show confirmation
router.get('/checkout/confirmation/:orderId', (req, res) => {
  const order = findOrder(req.params.orderId);
  if (!order) {
    res.status(404).send('Order not found');
    return;
  }
  const opts = {
    username: req.session.username,
    cartCount: cartCountFor(cartKeyOrNull(req)),
  };
  res.send(renderConfirmation(order, findById, opts));
});

export default router;
