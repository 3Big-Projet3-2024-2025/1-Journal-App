import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { Observable } from "rxjs";
import { Article } from "../models/article";

@Injectable({
  providedIn: 'root'
})
export class ArticleService {

  private apiUrl = 'http://localhost:8080/articles'; // URL de base, sans /api/article-controller

  constructor(private http: HttpClient) {}

  getArticleById(id: number): Observable<Article> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<Article>(url);
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

  deleteArticle(id: number): Observable<void> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(url);
  }

  validateArticle(id: number): Observable<Article> {
    const url = `${this.apiUrl}/${id}/validate`;
    return this.http.patch<Article>(url, {});
  }
}
