export type Genre = 'fiction' | 'nonfiction' | 'scifi' | 'mystery' | 'biography';

export const GENRE_DISPLAY: Record<Genre, string> = {
  fiction: 'Fiction',
  nonfiction: 'Nonfiction',
  scifi: 'Sci-Fi',
  mystery: 'Mystery',
  biography: 'Biography',
};

export type ShelfName = 'WANT' | 'READING' | 'FINISHED';

export const SHELF_DISPLAY: Record<ShelfName, string> = {
  WANT: 'Want to Read',
  READING: 'Reading',
  FINISHED: 'Finished',
};

export interface Book {
  id: number;
  title: string;
  author: string;
  genre: Genre;
  priceCents: number;
  rating: number;
  coverEmoji: string;
  blurb: string;
}

export interface CartItem {
  bookId: number;
  qty: number;
}

export interface Order {
  id: string;
  username: string;
  items: CartItem[];
  totalCents: number;
  shippingName: string;
  shippingAddress: string;
  placedAt: Date;
}

export interface User {
  username: string;
  displayName: string;
  password: string;
}

export function formatCents(cents: number): string {
  return '$' + (cents / 100).toFixed(2);
}
