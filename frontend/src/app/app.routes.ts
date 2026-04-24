import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/login/login.component').then(m => m.LoginComponent),
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./shared/layout/shell/shell.component').then(m => m.ShellComponent),
    children: [
      {
        path: 'books',
        loadComponent: () =>
          import('./features/books/book-list/book-list.component').then(
            m => m.BookListComponent,
          ),
      },
      {
        path: 'rentals',
        loadComponent: () =>
          import('./features/rentals/rental-list/rental-list.component').then(
            m => m.RentalListComponent,
          ),
      },
      { path: '', redirectTo: 'books', pathMatch: 'full' },
    ],
  },
  { path: '**', redirectTo: '' },
];
