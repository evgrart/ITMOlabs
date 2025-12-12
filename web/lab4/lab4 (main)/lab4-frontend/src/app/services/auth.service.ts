import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = 'http://localhost:8080/api/auth';
  private tokenKey = 'auth_token';
  private isBrowser: boolean;

  constructor(
    private http: HttpClient, 
    private router: Router,
    @Inject(PLATFORM_ID) platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
  }

  register(credentials: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, credentials);
  }

  login(credentials: any): Observable<any> {
    return this.http.post<{token: string}>(`${this.baseUrl}/login`, credentials).pipe(
      tap(res => {
        if (res.token && this.isBrowser) {
          localStorage.setItem(this.tokenKey, res.token);
        }
      })
    );
  }

  logout() {
    if (this.isBrowser) {
      localStorage.removeItem(this.tokenKey);
    }
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    if (this.isBrowser) {
      return localStorage.getItem(this.tokenKey);
    }
    return null;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}