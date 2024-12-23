import { Component, OnInit } from '@angular/core';
import { Article } from '../models/article';
import { ArticleService } from '../services/article.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-my-read-articles',
  templateUrl: './my-read-articles.component.html',
  styleUrls: ['./my-read-articles.component.css']
})
export class MyReadArticlesComponent implements OnInit {
  readArticles: Article[] = [];

  constructor(private articleService: ArticleService) {}

  ngOnInit(): void {
    this.loadReadArticles(); // Charge les articles au démarrage
  }

  loadReadArticles(): void {
    this.articleService.getReadArticles().subscribe(
      (readArticles) => {
        this.readArticles = readArticles;
        console.log('Articles marqués comme lus:', this.readArticles);
      },
      (error: HttpErrorResponse) => {
        console.error('Erreur lors du chargement des articles marqués comme lus:', error);
      }
    );
  }

  markArticleAsUnread(articleId: number | undefined): void {
    if (!articleId) return; // Si aucun ID, on quitte
    this.articleService.markAsUnread(articleId).subscribe(() => {
      // Supprime l'article de la liste locale
      this.readArticles = this.readArticles.filter(article => article.articleId !== articleId);
      console.log(`Article ${articleId} marked as unread.`);
    });
  }
}
