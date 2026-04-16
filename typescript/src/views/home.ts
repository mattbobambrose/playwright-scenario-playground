/**
 * Home page view. Mirrors HomePage.kt.
 */
import type { Book } from '../model/types.js';
import { esc, layout } from './layout.js';
import type { LayoutOpts } from './layout.js';
import { formatCents } from '../model/types.js';

export function renderHome(
  featuredBooks: Book[],
  opts: LayoutOpts,
): string {
  const featured = featuredBooks
    .slice()
    .sort((a, b) => b.rating - a.rating)
    .slice(0, 4);

  const featuredCards = featured.map((book) => featuredCard(book)).join('\n');

  const content = `<section class="bg-gradient-to-br from-indigo-100 via-white to-amber-100 rounded-2xl p-10 mb-10" data-testid="hero">
<h1 class="text-4xl md:text-5xl font-extrabold tracking-tight text-slate-900">Your next favorite book is on the shelf.</h1>
<p class="mt-4 text-lg text-slate-700 max-w-2xl">Browse twenty hand-picked titles across fiction, nonfiction, mystery, sci-fi, and biography. Build a cart, check out, and track what you&#39;re reading across three personal shelves.</p>
<div class="mt-6 flex gap-3">
<a href="/catalog" class="rounded-lg bg-indigo-600 px-5 py-3 text-white font-semibold shadow hover:bg-indigo-700" data-testid="cta-browse">Browse the catalog</a>
<a href="/login" class="rounded-lg bg-white px-5 py-3 text-indigo-700 font-semibold border border-indigo-200 hover:border-indigo-400" data-testid="cta-login">Log in</a>
</div>
</section>
<section>
<h2 class="text-2xl font-bold text-slate-900 mb-4">Top-rated this week</h2>
<div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4" data-testid="featured-grid">
${featuredCards}
</div>
</section>`;

  return layout('Home', content, opts);
}

function featuredCard(book: Book): string {
  return `<a href="/books/${book.id}" class="block rounded-xl bg-white border border-slate-200 p-4 hover:shadow-md hover:border-indigo-300 transition" data-testid="featured-${book.id}">
<div class="text-5xl mb-3">${esc(book.coverEmoji)}</div>
<div class="font-semibold text-slate-900 line-clamp-2">${esc(book.title)}</div>
<div class="text-sm text-slate-500">${esc(book.author)}</div>
<div class="mt-2 flex items-center justify-between text-sm">
<span class="text-indigo-700 font-semibold">${esc(formatCents(book.priceCents))}</span>
<span class="text-amber-600">\u2605 ${book.rating.toFixed(1)}</span>
</div>
</a>`;
}
