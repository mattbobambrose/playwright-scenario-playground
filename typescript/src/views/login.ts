/**
 * Login page view. Mirrors LoginPage.kt.
 */
import { esc, layout, flashError, textField } from './layout.js';
import type { LayoutOpts } from './layout.js';

export function renderLogin(
  next: string,
  username: string = '',
  error: string | undefined = undefined,
  opts: LayoutOpts,
): string {
  const content = `<section class="max-w-md mx-auto bg-white rounded-xl border border-slate-200 p-8" data-testid="login-section">
<h1 class="text-2xl font-bold mb-4">Log in</h1>
${flashError(error)}
<form action="/login" method="post" class="space-y-4" data-testid="login-form">
<input type="hidden" name="next" value="${esc(next)}">
${textField('username', 'Username', username)}
${textField('password', 'Password', '', undefined, 'password')}
<button type="submit" class="w-full rounded bg-indigo-600 text-white font-semibold px-4 py-2 hover:bg-indigo-700" data-testid="login-submit">Log in</button>
</form>
<div class="mt-6 text-sm text-slate-500">
<p class="font-semibold text-slate-700">Demo accounts:</p>
<ul class="list-disc list-inside mt-1">
<li>demo / demo</li>
<li>alice / wonderland</li>
<li>bob / password1</li>
</ul>
</div>
</section>`;

  return layout('Log in', content, opts);
}
