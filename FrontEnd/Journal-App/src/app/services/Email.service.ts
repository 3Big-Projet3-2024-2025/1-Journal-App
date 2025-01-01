import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EmailService {
  private apiUrl = 'http://localhost:8080/api/emails/send'; // L'URL de votre endpoint backend

  constructor(private http: HttpClient) {}
  sendEmail(to: string, subject: string, content: string): Observable<any> {
    const emailRequest = { to, subject, content };
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });

    return this.http.post(this.apiUrl, emailRequest, { headers });
  }
}
