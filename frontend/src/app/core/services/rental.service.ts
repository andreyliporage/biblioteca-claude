import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { CreateRentalRequest, Rental } from '../models/rental.model';

const API_BASE = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class RentalService {
  private readonly http = inject(HttpClient);

  getActiveRentals() {
    return this.http.get<Rental[]>(`${API_BASE}/rentals`);
  }

  getHistory() {
    return this.http.get<Rental[]>(`${API_BASE}/rentals/history`);
  }

  createRental(req: CreateRentalRequest) {
    return this.http.post<Rental>(`${API_BASE}/rentals`, req);
  }

  returnBook(id: number) {
    return this.http.put<Rental>(`${API_BASE}/rentals/${id}/return`, {});
  }
}
