import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { AuthService } from './auth.service';
import { authGuard } from './auth.guard';

describe('authGuard', () => {
  let isAuthenticated: ReturnType<typeof vi.fn>;
  let parseUrl: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    isAuthenticated = vi.fn();
    parseUrl = vi.fn().mockReturnValue('url-tree' as unknown as UrlTree);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: { isAuthenticated } },
        { provide: Router, useValue: { parseUrl } },
      ],
    });
  });

  it('should allow access when authenticated', () => {
    isAuthenticated.mockReturnValue(true);
    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as any, {} as any),
    );
    expect(result).toBe(true);
  });

  it('should redirect to /login when not authenticated', () => {
    isAuthenticated.mockReturnValue(false);
    TestBed.runInInjectionContext(() => authGuard({} as any, {} as any));
    expect(parseUrl).toHaveBeenCalledWith('/login');
  });
});
