export type BookStatus = 'AVAILABLE' | 'RENTED' | 'MAINTENANCE';

export interface Book {
  id: number;
  code: string;
  name: string;
  author: string;
  isbn: string;
  status: BookStatus;
}
