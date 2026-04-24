import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from './auth.service';
import { errorInterceptor } from './error.interceptor';

describe('errorInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let authLogout: ReturnType<typeof vi.fn>;
  let snackBarOpen: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    authLogout = vi.fn();
    snackBarOpen = vi.fn();

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: { logout: authLogout } },
        { provide: MatSnackBar, useValue: { open: snackBarOpen } },
        provideHttpClient(withInterceptors([errorInterceptor])),
        provideHttpClientTesting(),
      ],
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should call logout on 401', () => {
    http.get('/test').subscribe({ error: () => {} });
    httpMock.expectOne('/test').flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(authLogout).toHaveBeenCalled();
    expect(snackBarOpen).not.toHaveBeenCalled();
  });

  it('should show snackbar on network error (status 0)', () => {
    http.get('/test').subscribe({ error: () => {} });
    httpMock.expectOne('/test').error(new ProgressEvent('error'));
    expect(snackBarOpen).toHaveBeenCalledWith('Sem conexão com o servidor.', 'OK', { duration: 5000 });
  });

  it('should show snackbar on 500', () => {
    http.get('/test').subscribe({ error: () => {} });
    httpMock.expectOne('/test').flush({}, { status: 500, statusText: 'Server Error' });
    expect(snackBarOpen).toHaveBeenCalledWith(
      'Erro interno do servidor. Tente novamente.',
      'OK',
      { duration: 5000 },
    );
  });

  it('should not show snackbar or logout on 400', () => {
    http.get('/test').subscribe({ error: () => {} });
    httpMock.expectOne('/test').flush({}, { status: 400, statusText: 'Bad Request' });
    expect(snackBarOpen).not.toHaveBeenCalled();
    expect(authLogout).not.toHaveBeenCalled();
  });
});
