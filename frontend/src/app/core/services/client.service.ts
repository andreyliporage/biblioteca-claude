import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Client, CreateClientRequest } from '../models/client.model';

const API_BASE = '/api';

@Injectable({ providedIn: 'root' })
export class ClientService {
  private readonly http = inject(HttpClient);

  searchClients(name: string) {
    return this.http.get<Client[]>(`${API_BASE}/clients`, { params: { name } });
  }

  createClient(req: CreateClientRequest) {
    return this.http.post<Client>(`${API_BASE}/clients`, req);
  }
}
