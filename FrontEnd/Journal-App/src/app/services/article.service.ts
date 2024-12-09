import { Injectable } from "@angular/core";
import { HttpClient , HttpHeaders } from "@angular/common/http";
import { Router } from "@angular/router";
import { CookieService } from "ngx-cookie-service";
import { Observable } from "rxjs";
import { Article } from "../models/article";

@Injectable({
    
        providedIn: 'root'
})
export class ArticleService {

    private apiUrl = 'http://localhost:8080/api/article-controller';

    constructor(private http: HttpClient, private router: Router, private cookieService: CookieService) {}

   

    getArticleById(id: number): Observable<any> {
        const url = `${this.apiUrl}/${id}`;
        const headers = this.getAuthHeaders(); // important pour etre auth
        return this.http.get(url, { headers });
    }

    addArticle(newArticle: Article): Observable<Article> {
        const headers = this.getAuthHeaders();
        return this.http.post<Article>(this.apiUrl, newArticle, { headers });
    }

    getArticles(): Observable<Article[]> {
        const headers = this.getAuthHeaders();
        return this.http.get<Article[]>(this.apiUrl, { headers });
    }

    updateArticle(id: number, updatedArticle: Article): Observable<Article> {
        const url = `${this.apiUrl}/${id}`;
        const headers = this.getAuthHeaders();
        return this.http.put<Article>(url, updatedArticle, { headers });
    }

    deleteArticle(id: number): Observable<void> {
        const url = `${this.apiUrl}/${id}`;
        const headers = this.getAuthHeaders();
        return this.http.delete<void>(url, { headers });
    }

    private getAuthHeaders(): HttpHeaders {
       const token = this.cookieService.get('token');
        return new HttpHeaders({
        Authorization: `Bearer ${token}`
        });
    }

 


}
