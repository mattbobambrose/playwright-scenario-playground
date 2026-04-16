import { Router } from 'express';
import type { Request, Response } from 'express';
import { findById } from '../data/catalog.js';
import { ordersFor, cartCountFor } from '../data/stores.js';
import { cartKeyOrNull } from '../session/middleware.js';
import { renderOrderHistory } from '../views/orders.js';

const router = Router();

/**
 * requireUser -- if no session.username, redirects to /login?next={originalUrl} and returns null.
 * Otherwise returns the username string.
 */
function requireUser(req: Request, res: Response): string | null {
  if (!req.session.username) {
    res.redirect(303, `/login?next=${encodeURIComponent(req.originalUrl)}`);
    return null;
  }
  return req.session.username;
}

// GET /orders -- require login, show order history
router.get('/orders', (req, res) => {
  const username = requireUser(req, res);
  if (!username) return;

  const orders = ordersFor(username);
  const opts = {
    username: req.session.username,
    cartCount: cartCountFor(cartKeyOrNull(req)),
  };
  res.send(renderOrderHistory(orders, findById, opts));
});

export default router;
