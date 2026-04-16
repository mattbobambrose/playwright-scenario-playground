/**
 * Order history page view. Mirrors OrderHistoryPage.kt.
 */
import type { Book, CartItem, Order } from '../model/types.js';
import { formatCents } from '../model/types.js';
import { esc, layout } from './layout.js';
import type { LayoutOpts } from './layout.js';

export function renderOrderHistory(
  orders: Order[],
  findBook: (id: number) => Book | undefined,
  opts: LayoutOpts,
): string {
  let body: string;
  if (orders.length === 0) {
    body = `<section class="rounded border border-dashed border-slate-300 p-10 text-center" data-testid="orders-empty">
<p class="text-slate-500">You haven't placed any orders yet.</p>
<a href="/catalog" class="mt-3 inline-block text-indigo-700 hover:underline">Start browsing \u2192</a>
</section>`;
  } else {
    const sorted = orders.slice().sort((a, b) => b.placedAt.getTime() - a.placedAt.getTime());
    const cards = sorted
      .map((order) => {
        const itemLines = order.items
          .map((item) => {
            const book = findBook(item.bookId);
            if (!book) return '';
            const subtotal = formatCents(book.priceCents * item.qty);
            return `<li class="flex justify-between">
<span>${esc(book.title)} \u00D7 ${item.qty}</span>
<span class="text-slate-700">${esc(subtotal)}</span>
</li>`;
          })
          .filter(Boolean)
          .join('\n');

        return `<section class="bg-white rounded-xl border border-slate-200 p-5" data-testid="order-${esc(order.id)}">
<div class="flex items-center justify-between">
<h2 class="font-semibold text-slate-900">${esc(order.id)}</h2>
<span class="text-sm text-slate-500">${esc(formatOrderDate(order.placedAt))}</span>
</div>
<ul class="mt-3 space-y-1 text-sm">
${itemLines}
</ul>
<div class="mt-3 pt-3 border-t border-slate-100 flex justify-between font-semibold">
<span>Total</span>
<span>${esc(formatCents(order.totalCents))}</span>
</div>
</section>`;
      })
      .join('\n');

    body = `<div class="space-y-4" data-testid="orders-list">
${cards}
</div>`;
  }

  const content = `<h1 class="text-3xl font-bold mb-6">Your orders</h1>
${body}`;

  return layout('Orders', content, opts);
}

/**
 * Formats a Date in the same style as the Kotlin DateTimeFormatter:
 * "MMM d, yyyy 'at' h:mm a" — e.g. "Apr 15, 2026 at 2:30 PM"
 */
function formatOrderDate(date: Date): string {
  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  const month = months[date.getMonth()];
  const day = date.getDate();
  const year = date.getFullYear();
  let hours = date.getHours();
  const minutes = date.getMinutes();
  const ampm = hours >= 12 ? 'PM' : 'AM';
  hours = hours % 12;
  if (hours === 0) hours = 12;
  const mm = minutes.toString().padStart(2, '0');
  return `${month} ${day}, ${year} at ${hours}:${mm} ${ampm}`;
}
