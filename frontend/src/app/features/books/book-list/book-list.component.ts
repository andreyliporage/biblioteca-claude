import { AfterViewInit, Component, OnInit, ViewChild, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatDrawer, MatSidenavModule } from '@angular/material/sidenav';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Book, BookStatus } from '../../../core/models/book.model';
import { BookService } from '../../../core/services/book.service';
import { BookFormComponent } from '../book-form/book-form.component';

interface BookFilter {
  name: string;
  author: string;
  isbn: string;
  code: string;
  status: BookStatus | '';
}

@Component({
  selector: 'app-book-list',
  imports: [
    ReactiveFormsModule,
    MatTableModule,
    MatSortModule,
    MatSidenavModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
    BookFormComponent,
  ],
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.scss',
})
export class BookListComponent implements OnInit, AfterViewInit {
  private readonly bookService = inject(BookService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly fb = inject(FormBuilder);

  @ViewChild(MatSort) private sort!: MatSort;
  @ViewChild(MatDrawer) private drawer!: MatDrawer;

  protected readonly displayedColumns = ['code', 'name', 'author', 'isbn', 'status', 'actions'];
  protected readonly dataSource = new MatTableDataSource<Book>([]);
  protected readonly loading = signal(false);
  protected readonly selectedBook = signal<Book | null>(null);

  protected readonly statuses: { value: BookStatus; label: string }[] = [
    { value: 'AVAILABLE', label: 'Disponível' },
    { value: 'RENTED', label: 'Locado' },
    { value: 'MAINTENANCE', label: 'Manutenção' },
  ];

  protected readonly filterForm = this.fb.nonNullable.group({
    name: [''],
    author: [''],
    isbn: [''],
    code: [''],
    status: ['' as BookStatus | ''],
  });

  ngOnInit(): void {
    this.setupFilter();
    this.loadBooks();
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
  }

  private setupFilter(): void {
    this.dataSource.filterPredicate = (data: Book, filterStr: string) => {
      const f: BookFilter = JSON.parse(filterStr);
      return (
        (!f.name || data.name.toLowerCase().includes(f.name.toLowerCase())) &&
        (!f.author || data.author.toLowerCase().includes(f.author.toLowerCase())) &&
        (!f.isbn || data.isbn.includes(f.isbn)) &&
        (!f.code || data.code.toLowerCase().includes(f.code.toLowerCase())) &&
        (!f.status || data.status === f.status)
      );
    };

    this.filterForm.valueChanges.subscribe(val => {
      this.dataSource.filter = JSON.stringify(val);
    });
  }

  private loadBooks(): void {
    this.loading.set(true);
    this.bookService.getBooks().subscribe({
      next: books => {
        this.dataSource.data = books;
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.snackBar.open('Erro ao carregar livros.', 'OK', { duration: 4000 });
      },
    });
  }

  protected openCreate(): void {
    this.selectedBook.set(null);
    this.drawer.open();
  }

  protected openEdit(book: Book): void {
    this.selectedBook.set(book);
    this.drawer.open();
  }

  protected onSaved(book: Book): void {
    this.drawer.close();
    const isNew = !this.selectedBook();
    if (isNew) {
      this.dataSource.data = [...this.dataSource.data, book];
    } else {
      this.dataSource.data = this.dataSource.data.map(b => (b.id === book.id ? book : b));
    }
    this.snackBar.open('Livro salvo com sucesso!', 'OK', { duration: 3000 });
  }

  protected onCancelled(): void {
    this.drawer.close();
  }

  protected onRent(book: Book): void {
    // Fase 5: abrir modal de locação
  }

  protected clearFilters(): void {
    this.filterForm.reset();
  }

  protected statusLabel(status: BookStatus): string {
    return this.statuses.find(s => s.value === status)?.label ?? status;
  }
}
