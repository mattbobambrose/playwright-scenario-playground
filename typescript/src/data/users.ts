import type { User } from '../model/types.js';

export const users = new Map<string, User>([
  ['demo', { username: 'demo', displayName: 'Demo Reader', password: 'demo' }],
  ['alice', { username: 'alice', displayName: 'Alice Wonderland', password: 'wonderland' }],
  ['bob', { username: 'bob', displayName: 'Bob Marlowe', password: 'password1' }],
]);

export function authenticate(username: string, password: string): User | null {
  const user = users.get(username);
  if (user && user.password === password) return user;
  return null;
}
