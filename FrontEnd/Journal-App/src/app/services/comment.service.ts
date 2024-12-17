import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Comment } from '../models/comment';

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  private baseUrl = 'http://localhost:8080/comments'; // URL de base de l'API pour les commentaires

  constructor(private http: HttpClient) {}

  /**
   * Récupère tous les commentaires.
   * @returns Observable<Comment[]>
   */
  getAllComments(): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.baseUrl}`);
  }

  /**
   * Supprime un commentaire par son ID.
   * @param id Identifiant du commentaire à supprimer
   * @returns Observable<void>
   */
  deleteComment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * Ajoute un nouveau commentaire.
   * @param comment Données du commentaire à ajouter
   * @returns Observable<Comment>
   */
  addComment(comment: Comment): Observable<Comment> {
    return this.http.post<Comment>(`${this.baseUrl}`, comment);
  }

  /**
   * Met à jour un commentaire existant.
   * @param id Identifiant du commentaire
   * @param comment Données mises à jour du commentaire
   * @returns Observable<Comment>
   */
  updateComment(id: number, comment: Comment): Observable<Comment> {
    return this.http.put<Comment>(`${this.baseUrl}/${id}`, comment);
  }

  /**
   * Récupère un commentaire spécifique par son ID.
   * @param id Identifiant du commentaire
   * @returns Observable<Comment>
   */
  getCommentById(id: number): Observable<Comment> {
    return this.http.get<Comment>(`${this.baseUrl}/${id}`);
  }
}
