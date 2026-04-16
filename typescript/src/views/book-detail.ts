/**
 * Book detail page view. Mirrors BookDetailPage.kt.
 */
import type { Book, ShelfName } from '../model/types.js';
import { GENRE_DISPLAY, SHELF_DISPLAY, formatCents } from '../model/types.js';
import { esc, layout } from './layout.js';
import type { LayoutOpts } from './layout.js';

export function renderBookDetail(
  book: Book,
  showShelfPicker: boolean,
  flashMessage: string | undefined,
  opts: LayoutOpts,
): string {
  const shelfEntries: ShelfName[] = ['WANT', 'READING', 'FINISHED'];

  const flashHtml = flashMessage
    ? `<div class="mt-4 rounded border border-green-200 bg-green-50 px-4 py-3 text-green-800" data-testid="flash-success">${esc(flashMessage)}</div>`
    : '';

  const shelfSection = showShelfPicker
    ? `<form action="/shelves/add" method="post" class="flex gap-2 items-center">
<input type="hidden" name="bookId" value="${book.id}">
<select name="shelf" data-testid="shelf-select" class="rounded border border-slate-300 px-3 py-2">
${shelfEntries.map((s) => `<option value="${s.toLowerCase()}">${esc(SHELF_DISPLAY[s])}</option>`).join('\n')}
</select>
<button type="submit" class="rounded border border-indigo-300 text-indigo-700 font-semibold px-4 py-2 hover:bg-indigo-50" data-testid="add-to-shelf">Add to Shelf</button>
</form>`
    : `<a href="/login?next=/books/${book.id}" class="text-sm text-indigo-700 hover:underline self-center" data-testid="login-to-shelf">Log in to add to your shelves \u2192</a>`;

  const content = `<a href="/catalog" class="text-sm text-indigo-700 hover:underline">\u2190 Back to catalog</a>
${flashHtml}
<section class="mt-4 grid grid-cols-1 md:grid-cols-[200px_1fr] gap-8 bg-white rounded-xl border border-slate-200 p-6" data-testid="book-detail-${book.id}">
<div class="text-9xl text-center">${esc(book.coverEmoji)}</div>
<div>
<h1 class="text-3xl font-bold text-slate-900" data-testid="book-title">${esc(book.title)}</h1>
<p class="text-slate-600 mt-1">by ${esc(book.author)}</p>
<div class="mt-2 flex gap-3 text-sm">
<span class="rounded-full bg-slate-100 px-3 py-1 text-slate-700">${esc(GENRE_DISPLAY[book.genre])}</span>
<span class="text-amber-600">\u2605 ${book.rating.toFixed(1)}</span>
<span class="text-indigo-700 font-semibold text-lg" data-testid="book-price">${esc(formatCents(book.priceCents))}</span>
</div>
<p class="mt-4 text-slate-700 leading-relaxed">${esc(book.blurb)}</p>
<div class="mt-6 flex flex-wrap gap-3">
<form action="/cart/add" method="post">
<input type="hidden" name="bookId" value="${book.id}">
<button type="submit" class="rounded bg-indigo-600 text-white font-semibold px-5 py-2 hover:bg-indigo-700" data-testid="add-to-cart">Add to Cart</button>
</form>
${shelfSection}
</div>
</div>
</section>`;

  return layout(book.title, content, opts);
}
