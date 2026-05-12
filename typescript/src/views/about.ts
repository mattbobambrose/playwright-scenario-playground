/**
 * About page view. Mirrors AboutPage.kt.
 */
import { layout } from './layout.js';
import type { LayoutOpts } from './layout.js';

export function renderAbout(opts: LayoutOpts): string {
  const content = `<article class="prose max-w-3xl">
<h1 class="text-3xl font-bold mb-4">About this demo</h1>
<p class="mb-4 text-slate-700">Bookstore is a deliberately small Ktor application used to demonstrate the playwright scenario recording and test-generation skills in this repo. It mixes static marketing pages, dynamic HTML DSL views, forms with validation, a session cart, hardcoded login, and a drag-and-drop &quot;My Shelves&quot; page.</p>
<h2 class="text-xl font-semibold mt-6 mb-2">Demo accounts</h2>
<ul class="list-disc list-inside text-slate-700">
<li>demo / demo</li>
<li>alice / wonderland</li>
<li>bob / password1</li>
</ul>
<h2 class="text-xl font-semibold mt-6 mb-2">Everything is in memory</h2>
<p class="text-slate-700">Carts, shelves, and orders are reset whenever the server restarts. Treat this site like a scratch pad for exercising flows \u2014 not a real store.</p>
</article>`;

  return layout('About', content, opts);
}
