import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { ArticleService } from '../services/article.service';
import { Article } from '../models/article';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

  constructor(private authService: AuthService, private router: Router,private articleService: ArticleService) {}

  articles: Article[] = [];

  ngOnInit(): void {
    // Au chargement du composant, on récupère la liste des articles
    this.articleService.getArticles().subscribe({
      next: (data) => {
        this.articles = data;
        console.log("Articles loaded:", this.articles);
      },
      error: (err) => {
        console.error("Erreur lors du chargement des articles", err);
      }
    });
  }

 



}
