package com.mattbobambrose.html

import com.mattbobambrose.data.Catalog
import com.mattbobambrose.model.Shelf
import io.ktor.server.application.ApplicationCall
import kotlinx.html.HtmlBlockTag
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.section
import kotlinx.html.span
import kotlinx.html.ul
import kotlinx.html.unsafe

suspend fun ApplicationCall.renderShelves(shelvesData: Map<Shelf, List<Int>>) {
  respondPage("My Shelves") {
    h1(classes = "text-3xl font-bold mb-2") { +"My Shelves" }
    p(classes = "text-slate-600 mb-6") {
      +"Drag books between columns to move them, or within a column to reorder. "
      a(href = "/catalog", classes = "text-indigo-700 hover:underline") { +"Add more from the catalog →" }
    }
    div(classes = "grid grid-cols-1 md:grid-cols-3 gap-4") {
      attributes["data-testid"] = "shelves"
      Shelf.entries.forEach { shelf -> renderShelfColumn(shelf, shelvesData[shelf].orEmpty()) }
    }
    div(classes = "mt-4 text-sm text-red-600 hidden") {
      attributes["id"] = "shelf-error"
      attributes["data-testid"] = "shelf-error"
    }
    script { unsafe { +DRAG_DROP_SCRIPT } }
  }
}

private fun HtmlBlockTag.renderShelfColumn(shelf: Shelf, bookIds: List<Int>) {
  section(classes = "bg-white rounded-xl border border-slate-200 flex flex-col") {
    attributes["data-testid"] = "shelf-column-${shelf.name.lowercase()}"
    div(classes = "px-4 py-3 border-b border-slate-100 flex items-center justify-between") {
      h2(classes = "font-semibold text-slate-900") { +shelf.display }
      span(classes = "text-xs rounded-full bg-slate-100 px-2 py-0.5 text-slate-600") {
        attributes["data-testid"] = "shelf-count-${shelf.name.lowercase()}"
        +bookIds.size.toString()
      }
    }
    ul(classes = "shelf p-3 space-y-2 min-h-[240px] flex-1") {
      attributes["data-shelf"] = shelf.name
      attributes["data-testid"] = "shelf-${shelf.name.lowercase()}"
      if (bookIds.isEmpty()) {
        li(classes = "shelf-empty text-center text-sm text-slate-400 italic py-8 border border-dashed border-slate-200 rounded") {
          attributes["data-testid"] = "shelf-empty-${shelf.name.lowercase()}"
          +"Drop a book here"
        }
      }
      bookIds.forEach { bookId ->
        val book = Catalog.findById(bookId) ?: return@forEach
        li(classes = "book-card cursor-grab select-none rounded-lg border border-slate-200 bg-slate-50 p-3 flex items-center gap-3 hover:border-indigo-300") {
          attributes["draggable"] = "true"
          attributes["data-book-id"] = bookId.toString()
          attributes["data-testid"] = "shelf-book-$bookId"
          span(classes = "text-2xl") { +book.coverEmoji }
          div(classes = "flex-1 min-w-0") {
            div(classes = "font-medium text-slate-900 truncate") { +book.title }
            div(classes = "text-xs text-slate-500 truncate") { +book.author }
          }
        }
      }
    }
  }
}

private val DRAG_DROP_SCRIPT: String = """
(function() {
  const shelves = document.querySelectorAll('ul.shelf');
  let dragged = null;
  let sourceShelf = null;
  const errorBox = document.getElementById('shelf-error');

  function showError(msg) {
    if (!errorBox) return;
    errorBox.textContent = msg;
    errorBox.classList.remove('hidden');
    setTimeout(() => errorBox.classList.add('hidden'), 4000);
  }

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

  function restoreEmptyPlaceholder(shelfEl) {
    if (shelfEl.querySelectorAll('li.book-card').length === 0 && !shelfEl.querySelector('.shelf-empty')) {
      const li = document.createElement('li');
      li.className = 'shelf-empty text-center text-sm text-slate-400 italic py-8 border border-dashed border-slate-200 rounded';
      li.setAttribute('data-testid', 'shelf-empty-' + shelfEl.dataset.shelf.toLowerCase());
      li.textContent = 'Drop a book here';
      shelfEl.appendChild(li);
    }
  }

  function updateCount(shelfEl) {
    const shelfName = shelfEl.dataset.shelf.toLowerCase();
    const badge = document.querySelector('[data-testid="shelf-count-' + shelfName + '"]');
    if (badge) badge.textContent = shelfEl.querySelectorAll('li.book-card').length;
  }

  shelves.forEach(shelf => {
    shelf.addEventListener('dragover', (e) => {
      e.preventDefault();
      e.dataTransfer.dropEffect = 'move';
    });

    shelf.addEventListener('drop', async (e) => {
      e.preventDefault();
      if (!dragged || !sourceShelf) return;
      const bookId = parseInt(dragged.dataset.bookId, 10);
      const fromShelf = sourceShelf.dataset.shelf;
      const toShelf = shelf.dataset.shelf;
      const insertBefore = findInsertBefore(shelf, e.clientY);
      const beforeBookId = insertBefore ? parseInt(insertBefore.dataset.bookId, 10) : null;

      removeEmptyPlaceholder(shelf);
      if (insertBefore) shelf.insertBefore(dragged, insertBefore);
      else shelf.appendChild(dragged);

      const endpoint = fromShelf === toShelf ? '/shelves/reorder' : '/shelves/move';
      const body = fromShelf === toShelf
        ? { shelf: toShelf, bookId, beforeBookId }
        : { bookId, fromShelf, toShelf, beforeBookId };

      try {
        const res = await fetch(endpoint, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(body),
        });
        if (!res.ok) throw new Error('Server returned ' + res.status);
        restoreEmptyPlaceholder(sourceShelf);
        updateCount(sourceShelf);
        updateCount(shelf);
      } catch (err) {
        showError('Could not save that move. Please refresh.');
      }
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
})();
""".trimIndent()
