import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from './api.config';
import { Category } from './models';

@Injectable({ providedIn: 'root' })
export class CategoryService {
  private http = inject(HttpClient);

  list(): Observable<Category[]> {
    return this.http.get<Category[]>(`${API_URL}/categories`);
  }

  create(body: { name: string; color: string }): Observable<Category> {
    return this.http.post<Category>(`${API_URL}/categories`, body);
  }

  update(id: number, body: { name: string; color: string }): Observable<Category> {
    return this.http.put<Category>(`${API_URL}/categories/${id}`, body);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/categories/${id}`);
  }
}
