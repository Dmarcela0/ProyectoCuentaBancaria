import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AccountSummary } from '../models/account-summary.model';
import { environment } from '../../environments/environment';

export interface TransactionPayload {
  description: string;
  amount: number;
  type: 'deposit' | 'withdraw';
}

export interface GoalPayload {
  name: string;
  targetAmount: number;
  targetDate: string;
}

@Injectable({ providedIn: 'root' })
export class AccountService {
  private apiUrl = `${environment.apiBaseUrl.replace(/\/$/, '')}/account`;

  constructor(private http: HttpClient) {}

  getSummary(): Observable<AccountSummary> {
    return this.http.get<AccountSummary>(`${this.apiUrl}/api/summary`);
  }

  addTransaction(payload: TransactionPayload): Observable<AccountSummary> {
    return this.http.post<AccountSummary>(`${this.apiUrl}/api/transactions`, payload);
  }

  addGoal(payload: GoalPayload): Observable<AccountSummary> {
    return this.http.post<AccountSummary>(`${this.apiUrl}/api/goals`, payload);
  }
}
