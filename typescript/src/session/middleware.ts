import session from 'express-session';
import { randomUUID } from 'node:crypto';
import type { Request } from 'express';

// ---------------------------------------------------------------------------
// Augment express-session types
// ---------------------------------------------------------------------------

declare module 'express-session' {
  interface SessionData {
    username?: string;
    guestId?: string;
  }
}

// ---------------------------------------------------------------------------
// Session middleware
// ---------------------------------------------------------------------------

export const sessionMiddleware = session({
  secret: 'bookshelf-demo-secret',
  resave: false,
  saveUninitialized: true,
  cookie: {
    httpOnly: true,
    maxAge: 7 * 24 * 60 * 60 * 1000, // 7 days
  },
});

// ---------------------------------------------------------------------------
// Cart-key helpers
// ---------------------------------------------------------------------------

/**
 * Returns the cart key for the current request.
 * Logged-in users get "user:{username}"; anonymous visitors get "guest:{guestId}".
 * A new guest UUID is created and stored on the session if one doesn't exist yet.
 */
export function cartKey(req: Request): string {
  if (req.session.username) {
    return `user:${req.session.username}`;
  }
  if (!req.session.guestId) {
    req.session.guestId = randomUUID();
  }
  return `guest:${req.session.guestId}`;
}

/**
 * Returns the cart key if the session already has one, or null otherwise.
 * Does NOT create a new guest UUID.
 */
export function cartKeyOrNull(req: Request): string | null {
  if (req.session.username) {
    return `user:${req.session.username}`;
  }
  if (req.session.guestId) {
    return `guest:${req.session.guestId}`;
  }
  return null;
}
