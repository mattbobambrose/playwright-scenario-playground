/**
 * Cart page view. Mirrors CartPage.kt.
 */
import type { Book, CartItem } from '../model/types.js';
import { formatCents } from '../model/types.js';
import { esc, layout } from './layout.js';
import type { LayoutOpts } from './layout.js';

export function renderCart(
  items: CartItem[],
  findBook: (id: number) => Book | undefined,
  opts: LayoutOpts,
): string {
  const withBook: { book: Book; item: CartItem }[] = [];
  for (const item of items) {
    const book = findBook(item.bookId);
    if (book) withBook.push({ book, item });
  }

  const totalCents = withBook.reduce((sum, { book, item }) => sum + book.priceCents * item.qty, 0);

  let body: string;
  if (withBook.length === 0) {
    body = `<section class="rounded border border-dashed border-slate-300 p-10 text-center" data-testid="cart-empty">
<p class="text-slate-500">Your cart is empty.</p>
<a href="/catalog" class="mt-3 inline-block text-indigo-700 hover:underline">Browse the catalog \u2192</a>
</section>`;
  } else {
    const rows = withBook
      .map(({ book, item }) => {
        const subtotal = formatCents(book.priceCents * item.qty);
        return `<tr class="border-t border-slate-100" data-testid="cart-row-${book.id}">
<td class="px-4 py-3"><a href="/books/${book.id}" class="text-slate-900 hover:text-indigo-700 font-medium">${esc(book.coverEmoji)} ${esc(book.title)}</a></td>
<td class="px-4 py-3 text-slate-700">${esc(formatCents(book.priceCents))}</td>
<td class="px-4 py-3"><form action="/cart/${book.id}/qty" method="post" class="flex gap-2 items-center"><input type="number" name="qty" value="${item.qty}" min="1" max="99" data-testid="cart-qty-${book.id}" class="w-20 rounded border border-slate-300 px-2 py-1"><button type="submit" class="text-xs text-indigo-700 hover:underline" data-testid="cart-update-${book.id}">Update</button></form></td>
<td class="px-4 py-3 text-slate-900 font-medium" data-testid="cart-subtotal-${book.id}">${esc(subtotal)}</td>
<td class="px-4 py-3"><form action="/cart/${book.id}/remove" method="post"><input type="hidden" name="_method" value="delete"><button type="submit" class="text-sm text-red-600 hover:underline" data-testid="cart-remove-${book.id}">Remove</button></form></td>
</tr>`;
      })
      .join('\n');

    body = `<section class="rounded-xl bg-white border border-slate-200 overflow-hidden">
<table class="w-full text-sm" data-testid="cart-table">
<thead class="bg-slate-50 text-left text-slate-600">
<tr>
<th class="px-4 py-3">Book</th>
<th class="px-4 py-3">Price</th>
<th class="px-4 py-3">Qty</th>
<th class="px-4 py-3">Subtotal</th>
<th class="px-4 py-3"></th>
</tr>
</thead>
<tbody>
${rows}
</tbody>
</table>
</section>
<div class="mt-6 flex items-center justify-between">
<span class="text-lg font-semibold">Total: <span data-testid="cart-total">${esc(formatCents(totalCents))}</span></span>
<a href="/checkout" class="rounded bg-indigo-600 px-5 py-3 text-white font-semibold hover:bg-indigo-700" data-testid="checkout-button">Check out</a>
</div>`;
  }

  const content = `<h1 class="text-3xl font-bold mb-6">Your Cart</h1>
${body}`;

  return layout('Cart', content, opts);
}
