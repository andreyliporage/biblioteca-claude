import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { debounceTime, distinctUntilChanged, filter, finalize, switchMap, tap } from 'rxjs';
import { Book } from '../../../core/models/book.model';
import { Client, CreateClientRequest } from '../../../core/models/client.model';
import { CreateRentalRequest, Rental } from '../../../core/models/rental.model';
import { ClientService } from '../../../core/services/client.service';
import { RentalService } from '../../../core/services/rental.service';

export interface RentalDialogData {
  book: Book;
}

function minRentalDurationValidator(minDays: number): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const start = group.get('startDate')?.value as Date | null;
    const end = group.get('endDate')?.value as Date | null;
    if (!start || !end) return null;
    const diffDays = Math.floor((end.getTime() - start.getTime()) / 86400000);
    return diffDays >= minDays ? null : { minRentalDuration: true };
  };
}

function toISODate(date: Date): string {
  return date.toISOString().split('T')[0];
}

function addDays(date: Date, days: number): Date {
  const d = new Date(date);
  d.setDate(d.getDate() + days);
  return d;
}

@Component({
  selector: 'app-rental-dialog',
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,
    MatDatepickerModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './rental-dialog.component.html',
  styleUrl: './rental-dialog.component.scss',
})
export class RentalDialogComponent implements OnInit {
  private readonly clientService = inject(ClientService);
  private readonly rentalService = inject(RentalService);
  private readonly dialogRef = inject<MatDialogRef<RentalDialogComponent, Rental>>(MatDialogRef);
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(FormBuilder);

  protected readonly book = inject<RentalDialogData>(MAT_DIALOG_DATA).book;
  protected readonly today = new Date();

  protected readonly clientSearch = new FormControl<string | Client>('');
  protected readonly filteredClients = signal<Client[]>([]);
  protected readonly searchingClients = signal(false);
  protected readonly selectedClient = signal<Client | null>(null);

  protected readonly showNewClientForm = signal(false);
  protected readonly savingClient = signal(false);
  protected readonly clientError = signal<string | null>(null);

  protected readonly loading = signal(false);

  protected readonly rentalForm = this.fb.group(
    {
      startDate: [new Date(), Validators.required],
      endDate: [null as Date | null, Validators.required],
    },
    { validators: minRentalDurationValidator(5) },
  );

  protected readonly newClientForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: [''],
  });

  ngOnInit(): void {
    this.clientSearch.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        tap(() => this.selectedClient.set(null)),
        filter(v => typeof v === 'string' && v.trim().length >= 2),
        switchMap(v => {
          this.searchingClients.set(true);
          return this.clientService.searchClients(v as string).pipe(
            finalize(() => this.searchingClients.set(false)),
          );
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe(clients => this.filteredClients.set(clients));
  }

  get minEndDate(): Date {
    const start = this.rentalForm.get('startDate')?.value as Date | null;
    return addDays(start ?? new Date(), 5);
  }

  protected displayClient = (value: Client | string | null): string => {
    if (!value || typeof value === 'string') return (value as string) ?? '';
    return `${value.name} — ${value.email}`;
  };

  protected onClientSelected(client: Client): void {
    this.selectedClient.set(client);
    this.showNewClientForm.set(false);
  }

  protected toggleNewClientForm(): void {
    this.showNewClientForm.update(v => !v);
    if (this.showNewClientForm()) {
      this.newClientForm.reset();
      this.clientError.set(null);
    }
  }

  protected saveNewClient(): void {
    if (this.newClientForm.invalid) {
      this.newClientForm.markAllAsTouched();
      return;
    }
    this.savingClient.set(true);
    this.clientError.set(null);
    const { name, email, phone } = this.newClientForm.getRawValue();
    const req: CreateClientRequest = { name, email, phone: phone || null };
    this.clientService.createClient(req).subscribe({
      next: client => {
        this.savingClient.set(false);
        this.selectedClient.set(client);
        this.clientSearch.setValue(client);
        this.showNewClientForm.set(false);
      },
      error: () => {
        this.savingClient.set(false);
        this.clientError.set('Erro ao cadastrar cliente. Verifique os dados.');
      },
    });
  }

  protected submit(): void {
    const client = this.selectedClient();
    if (!client) {
      this.clientSearch.markAsTouched();
      return;
    }
    if (this.rentalForm.invalid) {
      this.rentalForm.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    const { startDate, endDate } = this.rentalForm.getRawValue();
    const req: CreateRentalRequest = {
      bookId: this.book.id,
      clientId: client.id,
      startDate: toISODate(startDate!),
      endDate: toISODate(endDate!),
    };
    this.rentalService.createRental(req).subscribe({
      next: rental => {
        this.loading.set(false);
        this.dialogRef.close(rental);
      },
      error: () => this.loading.set(false),
    });
  }
}
