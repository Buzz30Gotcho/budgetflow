import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from './api.config';
import { DashboardSummary } from './models';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private http = inject(HttpClient);

  summary(year: number, month: number): Observable<DashboardSummary> {
    return this.http.get<DashboardSummary>(`${API_URL}/dashboard?year=${year}&month=${month}`);
  }
}
