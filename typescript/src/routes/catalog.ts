import { Router } from 'express';
import { books, findById } from '../data/catalog.js';
import { cartCountFor } from '../data/stores.js';
import { cartKeyOrNull } from '../session/middleware.js';
import { renderCatalog } from '../views/catalog.js';
import { renderBookDetail } from '../views/book-detail.js';
import type { Genre } from '../model/types.js';
import { GENRE_DISPLAY } from '../model/types.js';

const router = Router();

function parseGenre(value: string | undefined): Genre | null {
  if (!value) return null;
  const lower = value.toLowerCase();
  if (lower in GENRE_DISPLAY) return lower as Genre;
  return null;
}

function parseSort(value: string | undefined): string {
  if (value === 'priceAsc' || value === 'priceDesc' || value === 'rating') return value;
  return 'title';
}

// GET /catalog — filterable, sortable, paginated catalog
// The view function handles filtering, sorting, and pagination internally.
router.get('/catalog', (req, res) => {
  const q = (req.query.q as string) ?? '';
  const genre = parseGenre(req.query.genre as string | undefined);
  const sort = parseSort(req.query.sort as string | undefined);
  const rawPage = parseInt(req.query.page as string, 10);
  const page = Number.isFinite(rawPage) ? rawPage : 1;

  const opts = {
    username: req.session.username,
    cartCount: cartCountFor(cartKeyOrNull(req)),
  };
  res.send(renderCatalog(books, { q, genre, sort, page }, opts));
});

// GET /books/:id — book detail page
router.get('/books/:id', (req, res) => {
  const id = parseInt(req.params.id, 10);
  if (Number.isNaN(id)) {
    res.status(404).send('Book not found');
    return;
  }
  const book = findById(id);
  if (!book) {
    res.status(404).send('Book not found');
    return;
  }
  const loggedIn = req.session.username !== undefined;
  const flash = req.query.added
    ? `${req.query.added as string} added.`
    : undefined;
  const opts = {
    username: req.session.username,
    cartCount: cartCountFor(cartKeyOrNull(req)),
  };
  res.send(renderBookDetail(book, loggedIn, flash, opts));
});

export default router;
