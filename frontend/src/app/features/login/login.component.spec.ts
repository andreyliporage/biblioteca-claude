import { TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../../core/auth/auth.service';

describe('LoginComponent', () => {
  let authMock: { login: ReturnType<typeof vi.fn>; isAuthenticated: ReturnType<typeof vi.fn> };
  let routerMock: { navigate: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    authMock = {
      login: vi.fn(),
      isAuthenticated: vi.fn().mockReturnValue(false),
    };
    routerMock = { navigate: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideNoopAnimations(),
        { provide: AuthService, useValue: authMock },
        { provide: Router, useValue: routerMock },
      ],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should redirect to /books if already authenticated', () => {
    authMock.isAuthenticated.mockReturnValue(true);
    TestBed.createComponent(LoginComponent);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/books']);
  });

  it('should not call login when form is invalid', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    fixture.detectChanges();
    (fixture.componentInstance as any).submit();
    expect(authMock.login).not.toHaveBeenCalled();
  });

  it('should call login with credentials and navigate to /books on success', () => {
    authMock.login.mockReturnValue(of({ token: 'jwt' }));
    const fixture = TestBed.createComponent(LoginComponent);
    fixture.detectChanges();
    const component = fixture.componentInstance as any;
    component.form.setValue({ username: 'admin', password: 'secret' });
    component.submit();
    expect(authMock.login).toHaveBeenCalledWith('admin', 'secret');
    expect(routerMock.navigate).toHaveBeenCalledWith(['/books']);
  });

  it('should show error message and stop loading on login failure', () => {
    authMock.login.mockReturnValue(throwError(() => new Error('401')));
    const fixture = TestBed.createComponent(LoginComponent);
    fixture.detectChanges();
    const component = fixture.componentInstance as any;
    component.form.setValue({ username: 'admin', password: 'wrong' });
    component.submit();
    expect(component.error()).toBe('Usuário ou senha incorretos.');
    expect(component.loading()).toBe(false);
  });
});
