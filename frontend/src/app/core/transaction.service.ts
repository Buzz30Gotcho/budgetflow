import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from './api.config';
import { Transaction, TransactionType } from './models';

export interface TransactionBody {
  type: TransactionType;
  amount: number;
  description: string | null;
  date: string;
  categoryId: number | null;
}

@Injectable({ providedIn: 'root' })
export class TransactionService {
  private http = inject(HttpClient);

  list(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${API_URL}/transactions`);
  }

  create(body: TransactionBody): Observable<Transaction> {
    return this.http.post<Transaction>(`${API_URL}/transactions`, body);
  }

  update(id: number, body: TransactionBody): Observable<Transaction> {
    return this.http.put<Transaction>(`${API_URL}/transactions/${id}`, body);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/transactions/${id}`);
  }
}
