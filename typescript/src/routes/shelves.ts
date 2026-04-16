import { Router } from 'express';
import type { Request, Response } from 'express';
import { findById } from '../data/catalog.js';
import { shelvesFor, cartCountFor } from '../data/stores.js';
import { cartKeyOrNull } from '../session/middleware.js';
import { renderShelves } from '../views/shelves.js';
import type { ShelfName } from '../model/types.js';
import { SHELF_DISPLAY } from '../model/types.js';

const router = Router();

const SHELF_NAMES: ShelfName[] = ['WANT', 'READING', 'FINISHED'];

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

function parseShelf(value: string | undefined): ShelfName | null {
  if (!value) return null;
  const upper = value.toUpperCase() as ShelfName;
  if (SHELF_NAMES.includes(upper)) return upper;
  return null;
}

// GET /shelves -- require login, show shelves
router.get('/shelves', (req, res) => {
  const username = requireUser(req, res);
  if (!username) return;

  const userShelves = shelvesFor(username);
  const data: Record<ShelfName, number[]> = {
    WANT: userShelves.booksOn('WANT'),
    READING: userShelves.booksOn('READING'),
    FINISHED: userShelves.booksOn('FINISHED'),
  };
  const opts = {
    username: req.session.username,
    cartCount: cartCountFor(cartKeyOrNull(req)),
  };
  res.send(renderShelves(data, findById, opts));
});

// POST /shelves/add -- add book to a shelf
router.post('/shelves/add', (req, res) => {
  const username = requireUser(req, res);
  if (!username) return;

  const bookId = parseInt(req.body.bookId, 10);
  const shelf = parseShelf(req.body.shelf) ?? 'WANT';

  if (Number.isNaN(bookId) || !findById(bookId)) {
    res.status(400).send('Unknown book');
    return;
  }

  shelvesFor(username).addIfAbsent(bookId, shelf);
  res.redirect(303, `/books/${bookId}?added=${SHELF_DISPLAY[shelf]}`);
});

// POST /shelves/move -- move book between shelves
router.post('/shelves/move', (req, res) => {
  const username = requireUser(req, res);
  if (!username) return;

  const bookId = parseInt(req.body.bookId, 10);
  const from = parseShelf(req.body.fromShelf);
  const to = parseShelf(req.body.toShelf);

  if (Number.isNaN(bookId) || !from || !to) {
    res.status(400).send('Invalid payload');
    return;
  }

  const beforeBookId = req.body.beforeBookId
    ? parseInt(req.body.beforeBookId, 10)
    : null;
  shelvesFor(username).move(
    bookId,
    from,
    to,
    Number.isFinite(beforeBookId) ? beforeBookId : null,
  );
  res.redirect(303, '/shelves');
});

// POST /shelves/reorder -- reorder within a shelf
router.post('/shelves/reorder', (req, res) => {
  const username = requireUser(req, res);
  if (!username) return;

  const bookId = parseInt(req.body.bookId, 10);
  const shelf = parseShelf(req.body.shelf);

  if (Number.isNaN(bookId) || !shelf) {
    res.status(400).send('Invalid payload');
    return;
  }

  const beforeBookId = req.body.beforeBookId
    ? parseInt(req.body.beforeBookId, 10)
    : null;
  shelvesFor(username).reorder(
    shelf,
    bookId,
    Number.isFinite(beforeBookId) ? beforeBookId : null,
  );
  res.redirect(303, '/shelves');
});

export default router;
