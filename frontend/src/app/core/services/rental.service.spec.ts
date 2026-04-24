import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { RentalService } from './rental.service';
import { Rental } from '../models/rental.model';

const API_BASE = '/api';

const mockRental: Rental = {
  id: 1,
  bookId: 1,
  bookName: 'Clean Code',
  bookCode: 'LIV-00001',
  clientId: 1,
  clientName: 'João Silva',
  startDate: '2026-04-23',
  endDate: '2026-04-30',
  returnedAt: null,
  active: true,
};

describe('RentalService', () => {
  let service: RentalService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(RentalService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should fetch active rentals', () => {
    let result: Rental[] | undefined;
    service.getActiveRentals().subscribe(r => (result = r));

    const req = httpMock.expectOne(`${API_BASE}/rentals`);
    expect(req.request.method).toBe('GET');
    req.flush([mockRental]);

    expect(result).toEqual([mockRental]);
  });

  it('should fetch rental history', () => {
    let result: Rental[] | undefined;
    service.getHistory().subscribe(r => (result = r));

    const req = httpMock.expectOne(`${API_BASE}/rentals/history`);
    expect(req.request.method).toBe('GET');
    req.flush([{ ...mockRental, active: false, returnedAt: '2026-04-28' }]);

    expect(result?.[0].active).toBe(false);
  });

  it('should create a rental', () => {
    let result: Rental | undefined;
    service
      .createRental({ bookId: 1, clientId: 1, startDate: '2026-04-23', endDate: '2026-04-30' })
      .subscribe(r => (result = r));

    const req = httpMock.expectOne(`${API_BASE}/rentals`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      bookId: 1,
      clientId: 1,
      startDate: '2026-04-23',
      endDate: '2026-04-30',
    });
    req.flush(mockRental);

    expect(result).toEqual(mockRental);
  });

  it('should return a book', () => {
    let result: Rental | undefined;
    service.returnBook(1).subscribe(r => (result = r));

    const req = httpMock.expectOne(`${API_BASE}/rentals/1/return`);
    expect(req.request.method).toBe('PUT');
    req.flush({ ...mockRental, active: false, returnedAt: '2026-04-28' });

    expect(result?.active).toBe(false);
    expect(result?.returnedAt).toBe('2026-04-28');
  });
});
