export interface Rental {
  id: number;
  bookId: number;
  bookName: string;
  bookCode: string;
  clientId: number;
  clientName: string;
  startDate: string;
  endDate: string;
  returnedAt: string | null;
  active: boolean;
}

export interface CreateRentalRequest {
  bookId: number;
  clientId: number;
  startDate: string;
  endDate: string;
}
