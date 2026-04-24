export interface Client {
  id: number;
  name: string;
  email: string;
  phone: string | null;
}

export interface CreateClientRequest {
  name: string;
  email: string;
  phone: string | null;
}
