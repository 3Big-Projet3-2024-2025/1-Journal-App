import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Newsletter } from '../models/newsletter';

@Injectable({
  providedIn: 'root'
})
export class ManageNewsletterService {
  private apiUrl = 'http://localhost:8080/newsletter';
  constructor(
    private http: HttpClient
  ) { }

  GetALlnewsletter(): Observable<any[]>{
    const urlApi= `${this.apiUrl}/all`;
    return this.http.get<any[]>(urlApi,{})
  }

  Addnewsletter(newsletter:Newsletter):Observable<Newsletter>{
  
    return this.http.post<Newsletter>(this.apiUrl,newsletter)
  }
  Updatenewsletter(id : number,newsletter:Newsletter):Observable<Newsletter>{
    const urlApi= `$http://localhost:8080/newsletters/${id}`;
    return this.http.put<Newsletter>(urlApi,newsletter)
  }

  deletenewsletter(id : number): Observable<void>{
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(url);
  }

   getnewsletterById(id: number): Observable<Newsletter> {
      const url = `${this.apiUrl}/${id}`;
      return this.http.get<Newsletter>(url);
    }

}
