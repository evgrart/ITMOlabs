import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  getPoints(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/points`);
  }

  addPoint(point: {x: number, y: number, r: number}): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/points`, point);
  }
}