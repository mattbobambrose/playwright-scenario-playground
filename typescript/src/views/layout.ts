/**
 * Shared layout shell, flash helpers, and form field helpers.
 * Mirrors Layout.kt from the Kotlin Ktor app.
 */

/** Escape HTML-sensitive characters for safe insertion into templates. */
export function esc(str: string): string {
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

export interface LayoutOpts {
  username?: string;
  cartCount: number;
}

/**
 * Wraps page content in the full HTML shell: head, header, main, footer.
 * Matches `respondPage()` from Layout.kt exactly.
 */
export function layout(title: string, content: string, opts: LayoutOpts): string {
  const { username, cartCount } = opts;
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>${esc(title)} · Bookshelf Demo</title>
<script src="https://cdn.tailwindcss.com"></script>
<link rel="icon" href="data:,">
</head>
<body class="min-h-screen bg-slate-50 text-slate-900 flex flex-col">
${renderHeader(username, cartCount)}
<main class="flex-1 max-w-6xl w-full mx-auto px-4 py-8">
${content}
</main>
${renderFooter()}
</body>
</html>`;
}

function renderHeader(username: string | undefined, cartCount: number): string {
  const loggedIn = username !== undefined;

  const navLinks = [
    navLink('/catalog', 'Catalog', 'nav-catalog'),
    ...(loggedIn
      ? [
          navLink('/shelves', 'My Shelves', 'nav-shelves'),
          navLink('/orders', 'Orders', 'nav-orders'),
        ]
      : []),
    navLink('/about', 'About', 'nav-about'),
  ].join('\n');

  const authSection = loggedIn
    ? `<div class="flex items-center gap-3">
<span class="text-sm text-slate-600" data-testid="nav-user">Hi, ${esc(username!)}</span>
<form action="/logout" method="post">
<button type="submit" class="text-sm font-semibold text-slate-600 hover:text-red-600" data-testid="nav-logout">Log out</button>
</form>
</div>`
    : `<a href="/login" class="text-sm font-semibold text-indigo-700 hover:underline" data-testid="nav-login">Log in</a>`;

  return `<header class="bg-white border-b border-slate-200 shadow-sm">
<div class="max-w-6xl mx-auto px-4 py-4 flex items-center gap-6">
<a href="/" class="text-xl font-bold text-indigo-700" data-testid="brand">\u{1F4DA} Bookshelf</a>
<nav class="flex items-center gap-4 text-sm font-medium text-slate-700 flex-1">
${navLinks}
</nav>
<a href="/cart" class="relative inline-flex items-center gap-1 text-sm font-semibold text-slate-700 hover:text-indigo-700" data-testid="nav-cart">Cart<span class="ml-1 inline-flex items-center justify-center min-w-[1.5rem] h-6 px-2 rounded-full bg-indigo-600 text-white text-xs" data-testid="cart-count">${cartCount}</span></a>
${authSection}
</div>
</header>`;
}

function navLink(href: string, label: string, testId: string): string {
  return `<a href="${esc(href)}" class="hover:text-indigo-700" data-testid="${esc(testId)}">${esc(label)}</a>`;
}

function renderFooter(): string {
  return `<footer class="bg-white border-t border-slate-200 mt-12">
<div class="max-w-6xl mx-auto px-4 py-6 text-sm text-slate-500 flex justify-between">
<span>\u00A9 Bookshelf Demo</span>
<a href="/about" class="hover:text-indigo-700">About this demo</a>
</div>
</footer>`;
}

/** Renders a red flash-error banner if message is present. Matches `flashError()` in Layout.kt. */
export function flashError(message?: string): string {
  if (!message) return '';
  return `<div class="mb-4 rounded border border-red-300 bg-red-50 px-4 py-3 text-red-800" data-testid="flash-error">${esc(message)}</div>`;
}

/** Renders an inline field-level error. Matches `fieldError()` in Layout.kt. */
export function fieldError(error?: string): string {
  if (!error) return '';
  return `<div class="mt-1 text-sm text-red-600" data-field-error="true">${esc(error)}</div>`;
}

/**
 * Renders a labelled text input field with optional error.
 * Matches `textField()` in Layout.kt.
 */
export function textField(
  name: string,
  label: string,
  value: string = '',
  error?: string,
  type: string = 'text',
): string {
  return `<div>
<label for="${esc(name)}" class="block text-sm font-medium text-slate-700">${esc(label)}</label>
<input type="${esc(type)}" name="${esc(name)}" id="${esc(name)}" data-testid="input-${esc(name)}" value="${esc(value)}" class="mt-1 w-full rounded border border-slate-300 px-3 py-2">
${fieldError(error)}
</div>`;
}
