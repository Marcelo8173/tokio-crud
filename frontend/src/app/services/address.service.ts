import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AddressService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  getAddressById(
    size: number,
    page: number,
    sortBy: string,
    direction: string,
    id: string,
  ): Observable<any> {
    const params = new HttpParams()
      .set('size', size)
      .set('page', page)
      .set('sortBy', sortBy)
      .set('direction', direction);

    return this.http.get<any>(`${this.apiUrl}/address/${id}`, { params });
  }

  deleteById(id: string): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/address/${id}`)
  }

  updateAddress(id: string, updated: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/address/${id}`, updated)
  }

  createAddress(create: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/address`, create)
  }

}
