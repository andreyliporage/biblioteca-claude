import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Rental } from '../../../core/models/rental.model';
import { RentalService } from '../../../core/services/rental.service';

interface HistoryFilter {
  book: string;
  client: string;
}

@Component({
  selector: 'app-rental-list',
  imports: [
    DatePipe,
    ReactiveFormsModule,
    MatTableModule,
    MatTabsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './rental-list.component.html',
  styleUrl: './rental-list.component.scss',
})
export class RentalListComponent implements OnInit {
  private readonly rentalService = inject(RentalService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly fb = inject(FormBuilder);

  protected readonly activeColumns = ['bookCode', 'bookName', 'clientName', 'startDate', 'endDate', 'actions'];
  protected readonly historyColumns = ['bookCode', 'bookName', 'clientName', 'startDate', 'endDate', 'returnedAt'];

  protected readonly activeDataSource = new MatTableDataSource<Rental>([]);
  protected readonly historyDataSource = new MatTableDataSource<Rental>([]);

  protected readonly loadingActive = signal(false);
  protected readonly loadingHistory = signal(false);
  protected readonly returningId = signal<number | null>(null);

  protected readonly historyFilterForm = this.fb.nonNullable.group({
    book: [''],
    client: [''],
  });

  ngOnInit(): void {
    this.setupHistoryFilter();
    this.loadActiveRentals();
    this.loadHistory();
  }

  private setupHistoryFilter(): void {
    this.historyDataSource.filterPredicate = (data: Rental, filterStr: string) => {
      const f: HistoryFilter = JSON.parse(filterStr);
      const bookMatch = !f.book ||
        data.bookName.toLowerCase().includes(f.book.toLowerCase()) ||
        data.bookCode.toLowerCase().includes(f.book.toLowerCase());
      const clientMatch = !f.client ||
        data.clientName.toLowerCase().includes(f.client.toLowerCase());
      return bookMatch && clientMatch;
    };

    this.historyFilterForm.valueChanges.subscribe(val => {
      this.historyDataSource.filter = JSON.stringify(val);
    });
  }

  private loadActiveRentals(): void {
    this.loadingActive.set(true);
    this.rentalService.getActiveRentals().subscribe({
      next: rentals => {
        this.activeDataSource.data = rentals;
        this.loadingActive.set(false);
      },
      error: () => this.loadingActive.set(false),
    });
  }

  private loadHistory(): void {
    this.loadingHistory.set(true);
    this.rentalService.getHistory().subscribe({
      next: rentals => {
        this.historyDataSource.data = rentals;
        this.loadingHistory.set(false);
      },
      error: () => this.loadingHistory.set(false),
    });
  }

  protected returnBook(rental: Rental): void {
    this.returningId.set(rental.id);
    this.rentalService.returnBook(rental.id).subscribe({
      next: () => {
        this.returningId.set(null);
        this.snackBar.open('Livro devolvido com sucesso!', 'OK', { duration: 3000 });
        this.loadActiveRentals();
        this.loadHistory();
      },
      error: () => {
        this.returningId.set(null);
        this.snackBar.open('Erro ao registrar devolução.', 'OK', { duration: 4000 });
      },
    });
  }

  protected clearHistoryFilters(): void {
    this.historyFilterForm.reset();
  }
}
