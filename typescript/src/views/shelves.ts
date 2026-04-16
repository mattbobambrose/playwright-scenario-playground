/**
 * Shelves page view. Mirrors ShelvesPage.kt.
 */
import type { Book, ShelfName } from '../model/types.js';
import { SHELF_DISPLAY } from '../model/types.js';
import { esc, layout } from './layout.js';
import type { LayoutOpts } from './layout.js';

export function renderShelves(
  shelvesData: Record<ShelfName, number[]>,
  findBook: (id: number) => Book | undefined,
  opts: LayoutOpts,
): string {
  const shelfEntries: ShelfName[] = ['WANT', 'READING', 'FINISHED'];

  const columns = shelfEntries.map((shelf) => renderShelfColumn(shelf, shelvesData[shelf] ?? [], findBook)).join('\n');

  const content = `<h1 class="text-3xl font-bold mb-2">My Shelves</h1>
<p class="text-slate-600 mb-6">Drag books between columns to move them, or within a column to reorder. <a href="/catalog" class="text-indigo-700 hover:underline">Add more from the catalog \u2192</a></p>
<div class="grid grid-cols-1 md:grid-cols-3 gap-4" data-testid="shelves">
${columns}
</div>
<div class="mt-4 text-sm text-red-600 hidden" id="shelf-error" data-testid="shelf-error"></div>
<script>${DRAG_DROP_SCRIPT}</script>`;

  return layout('My Shelves', content, opts);
}

function renderShelfColumn(
  shelf: ShelfName,
  bookIds: number[],
  findBook: (id: number) => Book | undefined,
): string {
  const shelfLower = shelf.toLowerCase();

  let listItems: string;
  if (bookIds.length === 0) {
    listItems = `<li class="shelf-empty text-center text-sm text-slate-400 italic py-8 border border-dashed border-slate-200 rounded" data-testid="shelf-empty-${shelfLower}">Drop a book here</li>`;
  } else {
    listItems = bookIds
      .map((bookId) => {
        const book = findBook(bookId);
        if (!book) return '';
        return `<li class="book-card cursor-grab select-none rounded-lg border border-slate-200 bg-slate-50 p-3 flex items-center gap-3 hover:border-indigo-300" draggable="true" data-book-id="${bookId}" data-testid="shelf-book-${bookId}">
<span class="text-2xl">${esc(book.coverEmoji)}</span>
<div class="flex-1 min-w-0">
<div class="font-medium text-slate-900 truncate">${esc(book.title)}</div>
<div class="text-xs text-slate-500 truncate">${esc(book.author)}</div>
</div>
</li>`;
      })
      .filter(Boolean)
      .join('\n');
  }

  return `<section class="bg-white rounded-xl border border-slate-200 flex flex-col" data-testid="shelf-column-${shelfLower}">
<div class="px-4 py-3 border-b border-slate-100 flex items-center justify-between">
<h2 class="font-semibold text-slate-900">${esc(SHELF_DISPLAY[shelf])}</h2>
<span class="text-xs rounded-full bg-slate-100 px-2 py-0.5 text-slate-600" data-testid="shelf-count-${shelfLower}">${bookIds.length}</span>
</div>
<ul class="shelf p-3 space-y-2 min-h-[240px] flex-1" data-shelf="${shelf}" data-testid="shelf-${shelfLower}">
${listItems}
</ul>
</section>`;
}

const DRAG_DROP_SCRIPT = `(function() {
  const shelves = document.querySelectorAll('ul.shelf');
  let dragged = null;
  let sourceShelf = null;

  function findInsertBefore(shelfEl, clientY) {
    const cards = Array.from(shelfEl.querySelectorAll('li.book-card:not(.dragging)'));
    for (const card of cards) {
      const rect = card.getBoundingClientRect();
      if (clientY < rect.top + rect.height / 2) return card;
    }
    return null;
  }

  function removeEmptyPlaceholder(shelfEl) {
    const empty = shelfEl.querySelector('.shelf-empty');
    if (empty) empty.remove();
  }

  shelves.forEach(shelf => {
    shelf.addEventListener('dragover', (e) => {
      e.preventDefault();
      e.dataTransfer.dropEffect = 'move';
    });

    shelf.addEventListener('drop', (e) => {
      e.preventDefault();
      if (!dragged || !sourceShelf) return;
      const bookId = parseInt(dragged.dataset.bookId, 10);
      const fromShelf = sourceShelf.dataset.shelf;
      const toShelf = shelf.dataset.shelf;
      const insertBefore = findInsertBefore(shelf, e.clientY);
      const beforeBookId = insertBefore ? parseInt(insertBefore.dataset.bookId, 10) : null;

      // Optimistic UI so the drop feels instant — the form submit below
      // then navigates to the server-rendered authoritative state.
      removeEmptyPlaceholder(shelf);
      if (insertBefore) shelf.insertBefore(dragged, insertBefore);
      else shelf.appendChild(dragged);

      const sameShelf = fromShelf === toShelf;
      const form = document.createElement('form');
      form.method = 'POST';
      form.action = sameShelf ? '/shelves/reorder' : '/shelves/move';
      form.style.display = 'none';
      const addHidden = (name, value) => {
        if (value === null || value === undefined) return;
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = name;
        input.value = String(value);
        form.appendChild(input);
      };
      addHidden('bookId', bookId);
      if (sameShelf) {
        addHidden('shelf', toShelf);
      } else {
        addHidden('fromShelf', fromShelf);
        addHidden('toShelf', toShelf);
      }
      if (beforeBookId !== null) addHidden('beforeBookId', beforeBookId);
      document.body.appendChild(form);
      form.submit();
    });
  });

  document.addEventListener('dragstart', (e) => {
    const card = e.target.closest('li.book-card');
    if (!card) return;
    dragged = card;
    sourceShelf = card.closest('ul.shelf');
    card.classList.add('dragging', 'opacity-50');
    e.dataTransfer.effectAllowed = 'move';
    e.dataTransfer.setData('text/plain', card.dataset.bookId);
  });

  document.addEventListener('dragend', () => {
    if (dragged) dragged.classList.remove('dragging', 'opacity-50');
    dragged = null;
    sourceShelf = null;
  });
})();`;
