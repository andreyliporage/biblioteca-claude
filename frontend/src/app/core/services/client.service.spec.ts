import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ClientService } from './client.service';
import { Client } from '../models/client.model';

const API_BASE = 'http://localhost:8080/api';

const mockClient: Client = {
  id: 1,
  name: 'João Silva',
  email: 'joao@email.com',
  phone: '11999999999',
};

describe('ClientService', () => {
  let service: ClientService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ClientService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should search clients by name', () => {
    let result: Client[] | undefined;
    service.searchClients('João').subscribe(clients => (result = clients));

    const req = httpMock.expectOne(`${API_BASE}/clients?name=Jo%C3%A3o`);
    expect(req.request.method).toBe('GET');
    req.flush([mockClient]);

    expect(result).toEqual([mockClient]);
  });

  it('should create a client', () => {
    let result: Client | undefined;
    service
      .createClient({ name: 'João Silva', email: 'joao@email.com', phone: null })
      .subscribe(c => (result = c));

    const req = httpMock.expectOne(`${API_BASE}/clients`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ name: 'João Silva', email: 'joao@email.com', phone: null });
    req.flush(mockClient);

    expect(result).toEqual(mockClient);
  });
});
