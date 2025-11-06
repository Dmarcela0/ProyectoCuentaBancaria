import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AuthResponse, LoginPayload, RegisterPayload } from '../models/auth.models';
import { environment } from '../../environments/environment';

const STORAGE_KEY = 'bankids-token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = environment.apiBaseUrl;
  private currentAuth$ = new BehaviorSubject<AuthResponse | null>(this.restore());

  constructor(private http: HttpClient) {}

  login(payload: LoginPayload): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/api/auth/login`, payload).pipe(
      tap(response => this.persist(response))
    );
  }

  register(payload: RegisterPayload): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/api/auth/register`, payload).pipe(
      tap(response => this.persist(response))
    );
  }

  logout(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.currentAuth$.next(null);
  }

  get token(): string | null {
    return this.currentAuth$.value?.token ?? null;
  }

  get authChanges(): Observable<AuthResponse | null> {
    return this.currentAuth$.asObservable();
  }

  private persist(response: AuthResponse): void {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(response));
    this.currentAuth$.next(response);
  }

  private restore(): AuthResponse | null {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as AuthResponse;
    } catch {
      return null;
    }
  }
}
