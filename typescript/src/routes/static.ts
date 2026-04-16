import { Router } from 'express';
import { books } from '../data/catalog.js';
import { cartCountFor } from '../data/stores.js';
import { cartKeyOrNull } from '../session/middleware.js';
import { renderHome } from '../views/home.js';
import { renderAbout } from '../views/about.js';

const router = Router();

// GET / — home page with top 4 books by rating
router.get('/', (req, res) => {
  const featured = [...books]
    .sort((a, b) => b.rating - a.rating)
    .slice(0, 4);
  const opts = {
    username: req.session.username,
    cartCount: cartCountFor(cartKeyOrNull(req)),
  };
  res.send(renderHome(featured, opts));
});

// GET /about — about page
router.get('/about', (req, res) => {
  const opts = {
    username: req.session.username,
    cartCount: cartCountFor(cartKeyOrNull(req)),
  };
  res.send(renderAbout(opts));
});

export default router;
