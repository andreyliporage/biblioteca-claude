import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';

const TOKEN_KEY = 'biblioteca_token';
const API_BASE = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly _token = signal<string | null>(localStorage.getItem(TOKEN_KEY));

  readonly token = this._token.asReadonly();
  readonly isAuthenticated = computed(() => !!this._token());

  login(username: string, password: string) {
    return this.http
      .post<{ token: string }>(`${API_BASE}/auth/login`, { username, password })
      .pipe(
        tap(res => {
          localStorage.setItem(TOKEN_KEY, res.token);
          this._token.set(res.token);
        }),
      );
  }

  logout() {
    localStorage.removeItem(TOKEN_KEY);
    this._token.set(null);
    this.router.navigate(['/login']);
  }
}
