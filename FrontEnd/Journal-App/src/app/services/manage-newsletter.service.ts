import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Newsletter } from '../models/newsletter';

@Injectable({
  providedIn: 'root'
})
export class ManageNewsletterService {
  private apiUrl = 'http://localhost:8080/newsletters'; 
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
    // Méthode pour mettre à jour une newsletter
    Updatenewsletter(newsletterId: number, newsletter: Newsletter): Observable<Newsletter> {
      const url = `${this.apiUrl}/${newsletterId}`;
      return this.http.put<Newsletter>(url, newsletter);
    }

  deletenewsletter(id : number): Observable<void>{
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(url);
  }

   getnewsletterById(id: number): Observable<Newsletter> {
      const url = `${this.apiUrl}/${id}`;
      
        return this.http.get<Newsletter>(`${this.apiUrl}/${id}`);
    
    }

      // Récupère toutes les newsletters auxquelles un journaliste est associé
  getNewslettersForJournalist(userId: number): Observable<Newsletter[]> {
    const url = `${this.apiUrl}/journalist/${userId}`;
    return this.http.get<Newsletter[]>(url);
  }

  // Ajoute un journaliste à une newsletter
  addJournalistToNewsletter(newsletterId: number, userId: number): Observable<Newsletter> {
    const url = `${this.apiUrl}/${newsletterId}/addJournalist/${userId}`;
    return this.http.patch<Newsletter>(url, {});
  }

  // Retire un journaliste d'une newsletter
  removeJournalistFromNewsletter(newsletterId: number, userId: number): Observable<Newsletter> {
    const url = `${this.apiUrl}/${newsletterId}/removeJournalist/${userId}`;
    return this.http.patch<Newsletter>(url, {});
  }

}
