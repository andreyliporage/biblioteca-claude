import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Book, CreateBookRequest, UpdateBookRequest } from '../models/book.model';

const API_BASE = '/api';

@Injectable({ providedIn: 'root' })
export class BookService {
  private readonly http = inject(HttpClient);

  getBooks() {
    return this.http.get<Book[]>(`${API_BASE}/books`);
  }

  createBook(req: CreateBookRequest) {
    return this.http.post<Book>(`${API_BASE}/books`, req);
  }

  updateBook(id: number, req: UpdateBookRequest) {
    return this.http.put<Book>(`${API_BASE}/books/${id}`, req);
  }
}
