import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  register(userData: { name: string, email: string, password: string }): Observable<any> {
    const url = `${this.apiUrl}/user`; 
    return this.http.post(url, userData);
  }

  login(userData: { email: string, senha: string }): Observable<any> {
    const url = `${this.apiUrl}/auth/login`; 
    return this.http.post(url, userData);
  }

  saveToken(token: string): void {
    localStorage.setItem('auth_token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
