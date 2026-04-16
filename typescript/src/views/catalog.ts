/**
 * Catalog page view. Mirrors CatalogPage.kt.
 */
import type { Book, Genre } from '../model/types.js';
import { GENRE_DISPLAY, formatCents } from '../model/types.js';
import { esc, layout } from './layout.js';
import type { LayoutOpts } from './layout.js';

export interface CatalogQuery {
  q: string;
  genre: Genre | null;
  sort: string;
  page: number;
}

const SORT_OPTIONS: { param: string; display: string }[] = [
  { param: 'title', display: 'Title (A\u2013Z)' },
  { param: 'priceAsc', display: 'Price (low \u2192 high)' },
  { param: 'priceDesc', display: 'Price (high \u2192 low)' },
  { param: 'rating', display: 'Rating (high \u2192 low)' },
];

const DEFAULT_SORT = 'title';

function toQueryString(query: CatalogQuery, overrides: Record<string, string | null> = {}): string {
  const params: Record<string, string | null> = {
    q: query.q || null,
    genre: query.genre ?? null,
    sort: query.sort !== DEFAULT_SORT ? query.sort : null,
    page: query.page > 1 ? String(query.page) : null,
    ...overrides,
  };
  const parts: string[] = [];
  for (const [k, v] of Object.entries(params)) {
    if (v !== null && v !== undefined && v !== '') {
      parts.push(`${encodeURIComponent(k)}=${encodeURIComponent(v)}`);
    }
  }
  return parts.length === 0 ? '' : '?' + parts.join('&');
}

export function renderCatalog(
  allBooks: Book[],
  query: CatalogQuery,
  opts: LayoutOpts,
): string {
  const pageSize = 6;

  const filtered = allBooks.filter((book) => {
    if (query.q) {
      const q = query.q.toLowerCase();
      if (
        !book.title.toLowerCase().includes(q) &&
        !book.author.toLowerCase().includes(q)
      ) {
        return false;
      }
    }
    if (query.genre !== null && book.genre !== query.genre) return false;
    return true;
  });

  let sorted: Book[];
  switch (query.sort) {
    case 'priceAsc':
      sorted = filtered.slice().sort((a, b) => a.priceCents - b.priceCents);
      break;
    case 'priceDesc':
      sorted = filtered.slice().sort((a, b) => b.priceCents - a.priceCents);
      break;
    case 'rating':
      sorted = filtered.slice().sort((a, b) => b.rating - a.rating);
      break;
    case 'title':
    default:
      sorted = filtered.slice().sort((a, b) => a.title.toLowerCase().localeCompare(b.title.toLowerCase()));
      break;
  }

  const totalPages = Math.max(1, Math.ceil(sorted.length / pageSize));
  const page = Math.min(Math.max(1, query.page), totalPages);
  const pageItems = sorted.slice((page - 1) * pageSize, page * pageSize);

  let body: string;
  if (pageItems.length === 0) {
    body = `<div class="mt-8 rounded border border-dashed border-slate-300 p-8 text-center text-slate-500" data-testid="catalog-empty">No books match your search.</div>`;
  } else {
    const cards = pageItems.map((book) => catalogCard(book)).join('\n');
    body = `<div class="mt-6 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4" data-testid="catalog-grid">
${cards}
</div>
${renderPagination(query, page, totalPages)}`;
  }

  const content = `<h1 class="text-3xl font-bold mb-6">Catalog</h1>
${renderFilters(query)}
${body}`;

  return layout('Catalog', content, opts);
}

function renderFilters(query: CatalogQuery): string {
  const genres: (Genre | null)[] = [null, 'fiction', 'nonfiction', 'scifi', 'mystery', 'biography'];
  const genreOptions = genres
    .map((g) => {
      const value = g ?? '';
      const label = g === null ? 'All genres' : GENRE_DISPLAY[g];
      const selected = (query.genre ?? null) === g ? ' selected="selected"' : '';
      return `<option value="${esc(value)}"${selected}>${esc(label)}</option>`;
    })
    .join('\n');

  const sortOptions = SORT_OPTIONS.map((s) => {
    const selected = query.sort === s.param ? ' selected="selected"' : '';
    return `<option value="${esc(s.param)}"${selected}>${esc(s.display)}</option>`;
  }).join('\n');

  return `<form action="/catalog" method="get" class="grid grid-cols-1 md:grid-cols-4 gap-3 items-end" data-testid="catalog-filters">
<div>
<label for="q" class="block text-sm font-medium text-slate-700">Search</label>
<input type="text" name="q" id="q" data-testid="filter-q" placeholder="Title or author" value="${esc(query.q)}" class="mt-1 w-full rounded border border-slate-300 px-3 py-2">
</div>
<div>
<label for="genre" class="block text-sm font-medium text-slate-700">Genre</label>
<select id="genre" name="genre" data-testid="filter-genre" class="mt-1 w-full rounded border border-slate-300 px-3 py-2">
${genreOptions}
</select>
</div>
<div>
<label for="sort" class="block text-sm font-medium text-slate-700">Sort</label>
<select id="sort" name="sort" data-testid="filter-sort" class="mt-1 w-full rounded border border-slate-300 px-3 py-2">
${sortOptions}
</select>
</div>
<div class="flex gap-2">
<button type="submit" class="rounded bg-indigo-600 text-white font-semibold px-4 py-2 hover:bg-indigo-700" data-testid="filter-apply">Apply</button>
<a href="/catalog" class="rounded border border-slate-300 px-4 py-2 text-slate-700 hover:border-slate-500" data-testid="filter-clear">Clear</a>
</div>
</form>`;
}

function catalogCard(book: Book): string {
  return `<div class="rounded-xl bg-white border border-slate-200 p-4 flex flex-col" data-testid="book-card-${book.id}">
<a href="/books/${book.id}" class="block">
<div class="text-5xl mb-3">${esc(book.coverEmoji)}</div>
<h2 class="font-semibold text-slate-900 hover:text-indigo-700">${esc(book.title)}</h2>
</a>
<p class="text-sm text-slate-500 mt-1">${esc(book.author)}</p>
<p class="text-xs uppercase tracking-wide text-slate-400 mt-1">${esc(GENRE_DISPLAY[book.genre])}</p>
<div class="mt-4 flex items-center justify-between text-sm">
<span class="text-indigo-700 font-semibold" data-testid="book-price-${book.id}">${esc(formatCents(book.priceCents))}</span>
<span class="text-amber-600">\u2605 ${book.rating.toFixed(1)}</span>
</div>
</div>`;
}

function renderPagination(query: CatalogQuery, page: number, totalPages: number): string {
  if (totalPages <= 1) return '';
  return `<div class="mt-8 flex items-center justify-between" data-testid="pagination">
<span class="text-sm text-slate-500">Page ${page} of ${totalPages}</span>
<div class="flex gap-2">
${paginationLink(query, page - 1, 'Previous', 'pagination-prev', page > 1)}
${paginationLink(query, page + 1, 'Next', 'pagination-next', page < totalPages)}
</div>
</div>`;
}

function paginationLink(
  query: CatalogQuery,
  targetPage: number,
  label: string,
  testId: string,
  enabled: boolean,
): string {
  if (enabled) {
    const href = `/catalog${toQueryString(query, { page: String(targetPage) })}`;
    return `<a href="${esc(href)}" class="rounded border border-slate-300 px-3 py-1.5 text-sm text-slate-700 hover:border-slate-500" data-testid="${esc(testId)}">${esc(label)}</a>`;
  }
  return `<span class="rounded border border-slate-200 px-3 py-1.5 text-sm text-slate-400 cursor-not-allowed" data-testid="${esc(testId)}" aria-disabled="true">${esc(label)}</span>`;
}
