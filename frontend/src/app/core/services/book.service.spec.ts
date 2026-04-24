import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { BookService } from './book.service';
import { Book } from '../models/book.model';

const API_BASE = '/api';

const mockBook: Book = {
  id: 1,
  code: 'LIV-00001',
  name: 'Clean Code',
  author: 'Robert C. Martin',
  isbn: '9780132350884',
  status: 'AVAILABLE',
};

describe('BookService', () => {
  let service: BookService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(BookService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should fetch all books', () => {
    let result: Book[] | undefined;
    service.getBooks().subscribe(books => (result = books));

    const req = httpMock.expectOne(`${API_BASE}/books`);
    expect(req.request.method).toBe('GET');
    req.flush([mockBook]);

    expect(result).toEqual([mockBook]);
  });

  it('should create a book', () => {
    let result: Book | undefined;
    service
      .createBook({ name: 'Clean Code', author: 'Robert C. Martin', isbn: '9780132350884' })
      .subscribe(b => (result = b));

    const req = httpMock.expectOne(`${API_BASE}/books`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      name: 'Clean Code',
      author: 'Robert C. Martin',
      isbn: '9780132350884',
    });
    req.flush(mockBook);

    expect(result).toEqual(mockBook);
  });

  it('should update a book', () => {
    let result: Book | undefined;
    service
      .updateBook(1, { name: 'Clean Code Updated', status: 'MAINTENANCE' })
      .subscribe(b => (result = b));

    const req = httpMock.expectOne(`${API_BASE}/books/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ name: 'Clean Code Updated', status: 'MAINTENANCE' });
    req.flush({ ...mockBook, name: 'Clean Code Updated', status: 'MAINTENANCE' });

    expect(result?.name).toBe('Clean Code Updated');
    expect(result?.status).toBe('MAINTENANCE');
  });
});
