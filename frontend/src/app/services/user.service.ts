import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getUsers(
    size: number,
    page: number,
    sortBy: string,
    direction: string
  ): Observable<any> {
    const params = new HttpParams()
      .set('size', size)
      .set('page', page)
      .set('sortBy', sortBy)
      .set('direction', direction);

    return this.http.get<any>(`${this.apiUrl}/user`, { params });
  }

  deleteUser(id: string) {
    return this.http.delete<any>(`${this.apiUrl}/user/${id}`);

  }
}
