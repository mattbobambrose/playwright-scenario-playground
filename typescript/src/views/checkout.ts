/**
 * Checkout page view. Mirrors CheckoutPage.kt.
 */
import type { Book, CartItem, Order } from '../model/types.js';
import { formatCents } from '../model/types.js';
import { esc, layout, textField, fieldError } from './layout.js';
import type { LayoutOpts } from './layout.js';

export interface CheckoutForm {
  name: string;
  email: string;
  address: string;
  cardNumber: string;
  cardExpiry: string;
  cardCvc: string;
}

export function renderCheckout(
  items: CartItem[],
  findBook: (id: number) => Book | undefined,
  form: CheckoutForm = { name: '', email: '', address: '', cardNumber: '', cardExpiry: '', cardCvc: '' },
  errors: Record<string, string> = {},
  opts: LayoutOpts,
): string {
  const lines: { book: Book; item: CartItem }[] = [];
  for (const item of items) {
    const book = findBook(item.bookId);
    if (book) lines.push({ book, item });
  }
  const totalCents = lines.reduce((sum, { book, item }) => sum + book.priceCents * item.qty, 0);

  const orderLines = lines
    .map(({ book, item }) => {
      const subtotal = formatCents(book.priceCents * item.qty);
      return `<div class="flex justify-between py-1">
<span>${esc(book.title)} \u00D7 ${item.qty}</span>
<span class="text-slate-700">${esc(subtotal)}</span>
</div>`;
    })
    .join('\n');

  const content = `<h1 class="text-3xl font-bold mb-6">Checkout</h1>
<div class="grid grid-cols-1 lg:grid-cols-[1fr_320px] gap-8">
<form action="/checkout" method="post" class="bg-white rounded-xl border border-slate-200 p-6 space-y-4" data-testid="checkout-form">
${textField('name', 'Full name', form.name, errors['name'])}
${textField('email', 'Email', form.email, errors['email'], 'email')}
<div>
<label for="address" class="block text-sm font-medium text-slate-700">Shipping address</label>
<textarea id="address" name="address" rows="3" data-testid="input-address" class="mt-1 w-full rounded border border-slate-300 px-3 py-2">${esc(form.address)}</textarea>
${fieldError(errors['address'])}
</div>
<h2 class="text-lg font-semibold pt-4">Payment</h2>
${textField('cardNumber', 'Card number', form.cardNumber, errors['cardNumber'])}
<div class="grid grid-cols-2 gap-3">
${textField('cardExpiry', 'Expiry (MM/YY)', form.cardExpiry, errors['cardExpiry'])}
${textField('cardCvc', 'CVC', form.cardCvc, errors['cardCvc'])}
</div>
<button type="submit" class="mt-4 rounded bg-indigo-600 text-white font-semibold px-5 py-3 hover:bg-indigo-700" data-testid="place-order">Place order</button>
</form>
<section class="bg-white rounded-xl border border-slate-200 p-6" data-testid="checkout-summary">
<h2 class="text-lg font-semibold mb-3">Order summary</h2>
${orderLines}
<div class="border-t border-slate-200 mt-3 pt-3 flex justify-between font-semibold">
<span>Total</span>
<span data-testid="checkout-total">${esc(formatCents(totalCents))}</span>
</div>
</section>
</div>`;

  return layout('Checkout', content, opts);
}

export function renderConfirmation(
  order: Order,
  findBook: (id: number) => Book | undefined,
  opts: LayoutOpts,
): string {
  const lines: { book: Book; item: CartItem }[] = [];
  for (const item of order.items) {
    const book = findBook(item.bookId);
    if (book) lines.push({ book, item });
  }

  const orderLines = lines
    .map(({ book, item }) => {
      const subtotal = formatCents(book.priceCents * item.qty);
      return `<div class="flex justify-between py-1">
<span>${esc(book.title)} \u00D7 ${item.qty}</span>
<span class="text-slate-700">${esc(subtotal)}</span>
</div>`;
    })
    .join('\n');

  const content = `<section class="bg-white rounded-xl border border-green-200 p-8 text-center" data-testid="confirmation">
<div class="text-5xl mb-4">\u2705</div>
<h1 class="text-2xl font-bold text-green-800">Thank you for your order!</h1>
<p class="mt-2 text-slate-700">Your confirmation number is <span class="font-mono font-semibold" data-testid="confirmation-id">${esc(order.id)}</span>.</p>
<div class="mt-6 mx-auto max-w-sm text-left text-sm">
${orderLines}
<div class="border-t border-slate-200 mt-2 pt-2 flex justify-between font-semibold">
<span>Total</span>
<span>${esc(formatCents(order.totalCents))}</span>
</div>
</div>
</section>`;

  return layout('Order placed', content, opts);
}
