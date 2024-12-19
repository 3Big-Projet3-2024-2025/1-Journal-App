import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Commentmap } from '../models/commentmap';
import { Comments } from '../models/comment';

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  private baseUrl = 'http://localhost:8080/comments'; // URL de base de l'API pour les commentaires

  constructor(private http: HttpClient) {}


  getAllComments(): Observable<Comments[]> {
    return this.http.get<Comments[]>(`${this.baseUrl}`);
  }

  getAllCommentsmap(): Observable<Commentmap[]> {
    return this.http.get<Commentmap[]>(`${this.baseUrl}`);
  }
  
  deleteComment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

 
  addComment(commentData: any): Observable<any> {
  return this.http.post<any>(`${this.baseUrl}`, commentData);
}

 
  updateComment(id: number, comment: Commentmap): Observable<Comment> {
    return this.http.put<Comment>(`${this.baseUrl}/${id}`, comment);
  }

  getCommentById(id: number): Observable<Commentmap> {
    return this.http.get<Commentmap>(`${this.baseUrl}/${id}`);
  }
}
