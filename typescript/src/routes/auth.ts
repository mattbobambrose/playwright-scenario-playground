import { Router } from 'express';
import { authenticate } from '../data/users.js';
import { cartCountFor } from '../data/stores.js';
import { cartKeyOrNull } from '../session/middleware.js';
import { renderLogin } from '../views/login.js';

const router = Router();

// GET /login — render login form
router.get('/login', (req, res) => {
  const next = (req.query.next as string) ?? '/';
  const opts = {
    username: req.session.username,
    cartCount: cartCountFor(cartKeyOrNull(req)),
  };
  res.send(renderLogin(next, undefined, undefined, opts));
});

// POST /login — authenticate user
router.post('/login', (req, res) => {
  const username = (req.body.username ?? '').trim();
  const password = req.body.password ?? '';
  let next: string = (req.body.next ?? '').trim() || '/';
  if (!next.startsWith('/')) next = '/';

  const user = authenticate(username, password);
  if (!user) {
    const opts = {
      username: req.session.username,
      cartCount: cartCountFor(cartKeyOrNull(req)),
    };
    res.send(renderLogin(next, username, 'Invalid username or password.', opts));
    return;
  }
  req.session.username = user.username;
  res.redirect(303, next);
});

// POST /logout — clear session, redirect to /
router.post('/logout', (req, res) => {
  delete req.session.username;
  res.redirect(303, '/');
});

export default router;
