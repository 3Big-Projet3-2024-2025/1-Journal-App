import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { Observable } from "rxjs";
import { Article } from "../models/article";
import { map  } from "rxjs";
import { Image } from "../models/image";
import { tap } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ArticleService {

  private apiUrl = 'http://localhost:8080/articles'; // URL de base, sans /api/article-controller
   private apiUrlImage = 'http://localhost:8080/images'

  constructor(private http: HttpClient) {}

  getArticleById(id: number): Observable<Article> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<Article>(url);
  }

  addImage(image: Image): Observable<Image> {
    return this.http.post<Image>(this.apiUrlImage, image);
  }

  addArticle(newArticle: Article): Observable<Article> {
    return this.http.post<Article>(this.apiUrl, newArticle);
  }

  getArticles(): Observable<Article[]> {
    // Note: pour récupérer tous les articles, on appelle /all
    const url = `${this.apiUrl}/all`;
    return this.http.get<Article[]>(url);
  }

  updateArticle(id: number, updatedArticle: Article): Observable<Article> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.put<Article>(url, updatedArticle);
  }

  deleteArticle(id: number): Observable<string> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<string>(url);
  }

  validateArticle(id: number): Observable<Article> {
    const url = `${this.apiUrl}/${id}/validate`;
    return this.http.patch<Article>(url, {});
  }

  getNewsletterTitleByArticleId(articleId: number): Observable<string> {
    const url = `${this.apiUrl}/${articleId}/newsletter-title`;
    return this.http.get<{ title: string }>(url).pipe(map(response => response.title));
  }

  getAuthorNameByArticleId(articleId: number): Observable<string> {
    const url = `${this.apiUrl}/${articleId}/author-name`;
    return this.http.get<{ name: string }>(url).pipe(map(response => response.name));
  }


   getAvailableArticles(): Observable<Article[]> {
    const url = `${this.apiUrl}/available`;
    return this.http.get<Article[]>(url);
  }

 
  getUnavailableArticles(): Observable<Article[]> {
    const url = `${this.apiUrl}/unavailable`;
    return this.http.get<Article[]>(url);
  }

  searchValidArticles(query: string): Observable<Article[]> {
    const url = `${this.apiUrl}/search?query=${encodeURIComponent(query)}`;
    return this.http.get<Article[]>(url);
  }

  markAsRead(articleId: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${articleId}/mark-read`, {});
  }
  
  markAsUnread(articleId: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${articleId}/mark-unread`, {});
  }
  
  getArticleReadStatus(articleId: number): Observable<boolean> {
    return this.http.get<{ isRead: boolean }>(`${this.apiUrl}/${articleId}/status`).pipe(
      map(response => response.isRead)
    );
  }
  
  getNewsletterBackgroundColor(newsletterId: number): Observable<string> {
    const url = `${this.apiUrl}/${newsletterId}/background-color`;
    return this.http.get<{ backgroundColor: string }>(url).pipe(
      map(response => response.backgroundColor)
    );
  }
  getReadArticles(): Observable<Article[]> {
    return this.http.get<Article[]>(`${this.apiUrl}/read`).pipe(
      tap(() => console.log('Fetching read articles with token'))
    );
  }

}
