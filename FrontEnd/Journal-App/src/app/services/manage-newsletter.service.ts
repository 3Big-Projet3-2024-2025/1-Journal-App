import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Newsletter } from '../models/newsletter';

@Injectable({
  providedIn: 'root'
})
export class ManageNewsletterService {
  private apiUrl = 'http://localhost:8080/newsletters';

  constructor(private http: HttpClient) {}

  // Récupérer toutes les newsletters
  GetALlnewsletter(): Observable<any[]> {
    const urlApi = `${this.apiUrl}/all`;
    return this.http.get<any[]>(urlApi, {});
  }

  // Ajouter une nouvelle newsletter
  Addnewsletter(newsletter: Newsletter): Observable<Newsletter> {
    return this.http.post<Newsletter>(this.apiUrl, newsletter);
  }

  // Mettre à jour une newsletter
  Updatenewsletter(newsletterId: number, newsletter: Newsletter): Observable<Newsletter> {
    const url = `${this.apiUrl}/${newsletterId}`;
    return this.http.put<Newsletter>(url, newsletter);
  }

  // Supprimer une newsletter
  deletenewsletter(id: number): Observable<void> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(url);
  }

  // Récupérer une newsletter par son ID
  getnewsletterById(id: number): Observable<Newsletter> {
    return this.http.get<Newsletter>(`${this.apiUrl}/${id}`);
  }

  // Récupérer toutes les newsletters auxquelles un journaliste est associé
  getNewslettersForJournalist(userId: number): Observable<Newsletter[]> {
    const url = `${this.apiUrl}/journalist/${userId}`;
    return this.http.get<Newsletter[]>(url);
  }

  // Ajouter un journaliste à une newsletter
  addJournalistToNewsletter(newsletterId: number, userId: number): Observable<Newsletter> {
    const url = `${this.apiUrl}/${newsletterId}/addJournalist/${userId}`;
    return this.http.patch<Newsletter>(url, {});
  }

  // Retirer un journaliste d'une newsletter
  removeJournalistFromNewsletter(newsletterId: number, userId: number): Observable<Newsletter> {
    const url = `${this.apiUrl}/${newsletterId}/removeJournalist/${userId}`;
    return this.http.patch<Newsletter>(url, {});
  }

  // Ajouter un article à une newsletter
  addArticleToNewsletter(newsletterId: number, articleId: number): Observable<string> {
    const url = `${this.apiUrl}/${newsletterId}/addArticle`;
    return this.http.post<string>(url, { articleId });
  }

  // Supprimer un article d'une newsletter
  removeArticleFromNewsletter(newsletterId: number, articleId: number): Observable<string> {
    const url = `${this.apiUrl}/${newsletterId}/removeArticle/${articleId}`;
    return this.http.delete<string>(url);
  }

  // Vérifier si un article existe dans une newsletter
  doesArticleExistInNewsletter(newsletterId: number, articleId: number): Observable<boolean> {
    const url = `${this.apiUrl}/${newsletterId}/hasArticle/${articleId}`;
    return this.http.get<boolean>(url);
  }

  // Obtenir la liste des articles d'une newsletter
  getArticlesByNewsletterId(newsletterId: number): Observable<any[]> {
    const url = `${this.apiUrl}/${newsletterId}/articles`;
    return this.http.get<any[]>(url);
  }
  // Ajouter un journaliste à une newsletter via email
addJournalistToNewsletterByEmail(newsletterId: number, email: string): Observable<Newsletter> {
  const url = `${this.apiUrl}/${newsletterId}/addJournalistByEmail`;
  return this.http.post<Newsletter>(url, { email });
}

}
