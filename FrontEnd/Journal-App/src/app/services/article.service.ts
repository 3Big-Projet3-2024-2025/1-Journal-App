import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { Article } from "../models/article";
import { map, tap } from "rxjs/operators";
import { Image } from "../models/image";

@Injectable({
  providedIn: 'root'
})
export class ArticleService {

  private apiUrl = 'http://localhost:8080/articles'; // URL de base pour les articles
  private apiUrlImage = 'http://localhost:8080/images'; // URL de base pour les images

  constructor(private http: HttpClient) {}

  // Récupérer un article par son ID
  getArticleById(id: number): Observable<Article> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<Article>(url);
  }

  // Récupérer les articles associés à un éditeur via son ID
  getArticlesByEditorId(editorId: number): Observable<Article[]> {
    const url = `${this.apiUrl}/by-editor/${editorId}`;
    return this.http.get<Article[]>(url);
  }

  // Ajouter une image
  addImage(image: Image): Observable<Image> {
    return this.http.post<Image>(this.apiUrlImage, image);
  }

  // Ajouter un article
  addArticle(newArticle: Article): Observable<Article> {
    return this.http.post<Article>(this.apiUrl, newArticle);
  }

  // Récupérer tous les articles
  getArticles(): Observable<Article[]> {
    const url = `${this.apiUrl}/all`;
    return this.http.get<Article[]>(url);
  }

  // Mettre à jour un article
  updateArticle(id: number, updatedArticle: Article): Observable<Article> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.put<Article>(url, updatedArticle);
  }

  // Supprimer un article
  deleteArticle(articleId: number): Observable<string> {
    const url = `${this.apiUrl}/${articleId}`;
    return this.http.delete(url, { responseType: 'text' }); // Spécifie que la réponse est en texte
  }

  // Valider un article
  validateArticle(id: number): Observable<Article> {
    const url = `${this.apiUrl}/${id}/validate`;
    return this.http.patch<Article>(url, {});
  }

  // Récupérer le titre de la newsletter associée à un article
  getNewsletterTitleByArticleId(articleId: number): Observable<string> {
    const url = `${this.apiUrl}/${articleId}/newsletter-title`;
    return this.http.get<{ title: string }>(url).pipe(
      map(response => response.title)
    );
  }

  // Récupérer le nom de l'auteur d'un article
  getAuthorNameByArticleId(articleId: number): Observable<string> {
    const url = `${this.apiUrl}/${articleId}/author-name`;
    return this.http.get<{ name: string }>(url).pipe(
      map(response => response.name)
    );
  }

  // Récupérer les articles valides
  getAvailableArticles(): Observable<Article[]> {
    const url = `${this.apiUrl}/available`;
    return this.http.get<Article[]>(url);
  }

  // Récupérer les articles invalides
  getUnavailableArticles(): Observable<Article[]> {
    const url = `${this.apiUrl}/unavailable`;
    return this.http.get<Article[]>(url);
  }

  // Rechercher des articles valides par mot-clé
  searchValidArticles(query: string): Observable<Article[]> {
    const url = `${this.apiUrl}/search?query=${encodeURIComponent(query)}`;
    return this.http.get<Article[]>(url);
  }

  // Marquer un article comme lu
  markAsRead(articleId: number): Observable<void> {
    const url = `${this.apiUrl}/${articleId}/mark-read`;
    return this.http.patch<void>(url, {});
  }

  // Marquer un article comme non lu
  markAsUnread(articleId: number): Observable<void> {
    const url = `${this.apiUrl}/${articleId}/mark-unread`;
    return this.http.patch<void>(url, {});
  }

  // Obtenir le statut de lecture d'un article
  getArticleReadStatus(articleId: number): Observable<boolean> {
    const url = `${this.apiUrl}/${articleId}/status`;
    return this.http.get<{ isRead: boolean }>(url).pipe(
      map(response => response.isRead)
    );
  }

  // Récupérer la couleur d'arrière-plan d'une newsletter par son ID
  getNewsletterBackgroundColor(newsletterId: number): Observable<string> {
    const url = `${this.apiUrl}/${newsletterId}/background-color`;
    return this.http.get<{ backgroundColor: string }>(url).pipe(
      map(response => response.backgroundColor)
    );
  }

  // Récupérer les articles lus par l'utilisateur
  getReadArticles(): Observable<Article[]> {
    const url = `${this.apiUrl}/read`;
    return this.http.get<Article[]>(url).pipe(
      tap(() => console.log('Fetching read articles'))
    );
  }

  // Vérifier si un journaliste fait partie d'une newsletter
  isJournalistInNewsletter(newsletterId: number, userId: number): Observable<boolean> {
    const url = `http://localhost:8080/newsletters/${newsletterId}/journalists/${userId}`;
    return this.http.get<boolean>(url);
  }

  // Récupérer les articles créés par un auteur via son ID
  getArticlesByAuthor(userId: number): Observable<Article[]> {
    const url = `${this.apiUrl}/author/${userId}`;
    return this.http.get<Article[]>(url);
  }

  // Récupérer les articles créés par un auteur via son email
  getArticlesByAuthorEmail(email: string): Observable<Article[]> {
    const url = `${this.apiUrl}/author/email/${email}`;
    return this.http.get<Article[]>(url);
  }
}
