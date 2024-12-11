import { Component, OnInit } from '@angular/core';
import { ArticleService } from '../services/article.service';
import { Article } from '../models/article';

@Component({
  selector: 'app-manage-article',
  templateUrl: './manage-article.component.html',
  styleUrls: ['./manage-article.component.css']
})
export class ManageArticleComponent implements OnInit {
  
  articles: Article[] = [];

  constructor(private articleService: ArticleService) {}

  ngOnInit(): void {
    // Au chargement du composant, on récupère la liste des articles
    this.getArticleDetails(1);
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

  validateFileCount(event: any): void {
    const files = event.target.files;
    
    // Vérifie si plus de 3 fichiers sont sélectionnés
    if (files.length > 3) {
      alert("You can upload a maximum of 3 images.");
      event.target.value = ""; // Réinitialise la sélection
      return;
    }

    // Vérifie si tous les fichiers sont des images
    for (const file of files) {
      if (!file.type.startsWith('image/')) {
        alert("Only image files are allowed.");
        event.target.value = ""; // Réinitialise la sélection
        return;
      }
    }

    // Si tout est correct
    alert(`${files.length} file(s) selected successfully.`);
  }
  getArticleDetails(articleId: number): void {
    this.articleService.getArticleById(articleId).subscribe(
      (data: Article) => {
        
      },
      (error) => {
        if (error.status === 401) {
          console.error('Erreur 401 : Non authentifié');

        } else {
          
        }
      }
    );
  }
}
