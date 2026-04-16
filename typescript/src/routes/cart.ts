import { Router } from 'express';
import { findById } from '../data/catalog.js';
import { cartFor, cartCountFor } from '../data/stores.js';
import { cartKey, cartKeyOrNull } from '../session/middleware.js';
import { renderCart } from '../views/cart.js';

const router = Router();

// GET /cart — render cart page
router.get('/cart', (req, res) => {
  const items = cartFor(cartKey(req)).snapshot();
  const opts = {
    username: req.session.username,
    cartCount: cartCountFor(cartKeyOrNull(req)),
  };
  res.send(renderCart(items, findById, opts));
});

// POST /cart/add — add book to cart
router.post('/cart/add', (req, res) => {
  const bookId = parseInt(req.body.bookId, 10);
  if (Number.isNaN(bookId) || !findById(bookId)) {
    res.status(400).send('Unknown book');
    return;
  }
  const rawQty = parseInt(req.body.qty, 10);
  const qty = Number.isFinite(rawQty) ? Math.max(1, Math.min(rawQty, 99)) : 1;
  cartFor(cartKey(req)).add(bookId, qty);
  res.redirect(303, '/cart');
});

// POST /cart/:id/qty — update quantity (0 removes)
router.post('/cart/:id/qty', (req, res) => {
  const bookId = parseInt(req.params.id, 10);
  const rawQty = parseInt(req.body.qty, 10);
  if (Number.isNaN(bookId) || Number.isNaN(rawQty)) {
    res.status(400).send('');
    return;
  }
  const qty = Math.max(0, Math.min(rawQty, 99));
  cartFor(cartKey(req)).setQty(bookId, qty);
  res.redirect(303, '/cart');
});

// POST /cart/:id/remove — remove item from cart
router.post('/cart/:id/remove', (req, res) => {
  const bookId = parseInt(req.params.id, 10);
  if (Number.isNaN(bookId)) {
    res.status(400).send('');
    return;
  }
  cartFor(cartKey(req)).remove(bookId);
  res.redirect(303, '/cart');
});

export default router;
