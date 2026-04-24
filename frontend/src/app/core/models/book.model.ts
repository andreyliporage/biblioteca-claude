export type BookStatus = 'AVAILABLE' | 'RENTED' | 'MAINTENANCE';

export interface Book {
  id: number;
  code: string;
  name: string;
  author: string;
  isbn: string;
  status: BookStatus;
}

export interface CreateBookRequest {
  name: string;
  author: string;
  isbn: string;
}

export interface UpdateBookRequest {
  name?: string;
  author?: string;
  isbn?: string;
  status?: BookStatus;
}
