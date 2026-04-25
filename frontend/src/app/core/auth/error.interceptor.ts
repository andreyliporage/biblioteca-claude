import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const snackBar = inject(MatSnackBar);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        auth.logout();
      } else if (error.status === 400) {
        const message = error.error?.error ?? 'Requisição inválida.';
        snackBar.open(message, 'OK', { duration: 5000 });
      } else if (error.status === 0) {
        snackBar.open('Sem conexão com o servidor.', 'OK', { duration: 5000 });
      } else if (error.status >= 500) {
        snackBar.open('Erro interno do servidor. Tente novamente.', 'OK', { duration: 5000 });
      }
      return throwError(() => error);
    }),
  );
};
