import { Component, EventEmitter, Input, OnChanges, Output, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { Book, BookStatus } from '../../../core/models/book.model';
import { BookService } from '../../../core/services/book.service';

@Component({
  selector: 'app-book-form',
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './book-form.component.html',
  styleUrl: './book-form.component.scss',
})
export class BookFormComponent implements OnChanges {
  private readonly bookService = inject(BookService);
  private readonly fb = inject(FormBuilder);

  @Input() book: Book | null = null;
  @Output() readonly saved = new EventEmitter<Book>();
  @Output() readonly cancelled = new EventEmitter<void>();

  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);

  protected readonly statuses: { value: BookStatus; label: string }[] = [
    { value: 'AVAILABLE', label: 'Disponível' },
    { value: 'RENTED', label: 'Locado' },
    { value: 'MAINTENANCE', label: 'Manutenção' },
  ];

  protected readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    author: ['', Validators.required],
    isbn: ['', [Validators.required, Validators.pattern(/^\d{10}$|^\d{13}$/)]],
    status: ['AVAILABLE' as BookStatus, Validators.required],
  });

  get isEdit(): boolean {
    return !!this.book;
  }

  ngOnChanges(): void {
    if (this.book) {
      this.form.patchValue(this.book);
    } else {
      this.form.reset({ name: '', author: '', isbn: '', status: 'AVAILABLE' });
    }
    this.error.set(null);
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    const { name, author, isbn, status } = this.form.getRawValue();

    const obs$ = this.book
      ? this.bookService.updateBook(this.book.id, { name, author, isbn, status })
      : this.bookService.createBook({ name, author, isbn });

    obs$.subscribe({
      next: book => {
        this.loading.set(false);
        this.saved.emit(book);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Erro ao salvar. Verifique os dados e tente novamente.');
      },
    });
  }

  protected cancel(): void {
    this.cancelled.emit();
  }
}
