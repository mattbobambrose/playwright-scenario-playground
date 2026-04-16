import type { CartItem, Order, ShelfName } from '../model/types.js';

// ---------------------------------------------------------------------------
// Cart
// ---------------------------------------------------------------------------

export class Cart {
  private items: CartItem[] = [];

  snapshot(): CartItem[] {
    return [...this.items];
  }

  add(bookId: number, qty: number = 1): void {
    const idx = this.items.findIndex((i) => i.bookId === bookId);
    if (idx === -1) {
      this.items.push({ bookId, qty });
    } else {
      this.items[idx] = { bookId, qty: this.items[idx].qty + qty };
    }
  }

  setQty(bookId: number, qty: number): void {
    const idx = this.items.findIndex((i) => i.bookId === bookId);
    if (idx !== -1) {
      if (qty <= 0) {
        this.items.splice(idx, 1);
      } else {
        this.items[idx] = { bookId, qty };
      }
    }
  }

  remove(bookId: number): void {
    this.items = this.items.filter((i) => i.bookId !== bookId);
  }

  clear(): void {
    this.items = [];
  }

  get itemCount(): number {
    return this.items.reduce((sum, i) => sum + i.qty, 0);
  }
}

// ---------------------------------------------------------------------------
// Shelves
// ---------------------------------------------------------------------------

const SHELF_NAMES: ShelfName[] = ['WANT', 'READING', 'FINISHED'];

export class Shelves {
  private lists: Map<ShelfName, number[]> = new Map(
    SHELF_NAMES.map((s) => [s, []]),
  );

  booksOn(shelf: ShelfName): number[] {
    return [...this.lists.get(shelf)!];
  }

  addIfAbsent(bookId: number, shelf: ShelfName): void {
    for (const list of this.lists.values()) {
      if (list.includes(bookId)) return;
    }
    this.lists.get(shelf)!.push(bookId);
  }

  move(bookId: number, from: ShelfName, to: ShelfName, beforeBookId: number | null): void {
    const src = this.lists.get(from)!;
    const srcIdx = src.indexOf(bookId);
    if (srcIdx === -1) return;
    src.splice(srcIdx, 1);

    const dest = this.lists.get(to)!;
    const insertAt =
      beforeBookId === null
        ? dest.length
        : (() => {
            const i = dest.indexOf(beforeBookId);
            return i === -1 ? dest.length : i;
          })();
    dest.splice(insertAt, 0, bookId);
  }

  reorder(shelf: ShelfName, bookId: number, beforeBookId: number | null): void {
    const list = this.lists.get(shelf)!;
    const idx = list.indexOf(bookId);
    if (idx === -1) return;
    list.splice(idx, 1);

    const insertAt =
      beforeBookId === null
        ? list.length
        : (() => {
            const i = list.indexOf(beforeBookId);
            return i === -1 ? list.length : i;
          })();
    list.splice(insertAt, 0, bookId);
  }
}

// ---------------------------------------------------------------------------
// Stores (singleton)
// ---------------------------------------------------------------------------

const carts = new Map<string, Cart>();
const shelves = new Map<string, Shelves>();
const ordersByUser = new Map<string, Order[]>();
const orderById = new Map<string, Order>();
let orderSeq = 1000;

export function cartFor(key: string): Cart {
  let cart = carts.get(key);
  if (!cart) {
    cart = new Cart();
    carts.set(key, cart);
  }
  return cart;
}

export function cartCountFor(key: string | null | undefined): number {
  if (!key) return 0;
  const cart = carts.get(key);
  return cart ? cart.itemCount : 0;
}

export function shelvesFor(username: string): Shelves {
  let s = shelves.get(username);
  if (!s) {
    s = new Shelves();
    shelves.set(username, s);
  }
  return s;
}

export function ordersFor(username: string): Order[] {
  return ordersByUser.get(username) ?? [];
}

export function addOrder(order: Order): void {
  let list = ordersByUser.get(order.username);
  if (!list) {
    list = [];
    ordersByUser.set(order.username, list);
  }
  list.push(order);
  orderById.set(order.id, order);
}

export function findOrder(id: string): Order | undefined {
  return orderById.get(id);
}

export function nextOrderId(): string {
  return `ORD-${++orderSeq}`;
}

export function resetAll(): void {
  carts.clear();
  shelves.clear();
  ordersByUser.clear();
  orderById.clear();
  orderSeq = 1000;
}
