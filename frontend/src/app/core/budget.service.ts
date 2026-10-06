import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from './api.config';
import { Budget } from './models';

@Injectable({ providedIn: 'root' })
export class BudgetService {
  private http = inject(HttpClient);

  list(year: number, month: number): Observable<Budget[]> {
    return this.http.get<Budget[]>(`${API_URL}/budgets?year=${year}&month=${month}`);
  }

  create(body: { categoryId: number; amount: number; year: number; month: number }): Observable<Budget> {
    return this.http.post<Budget>(`${API_URL}/budgets`, body);
  }

  update(id: number, body: { categoryId: number; amount: number; year: number; month: number }): Observable<Budget> {
    return this.http.put<Budget>(`${API_URL}/budgets/${id}`, body);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/budgets/${id}`);
  }
}
