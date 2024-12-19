import { Component, OnInit } from '@angular/core';
import { Article } from '../models/article';
import { ArticleService } from '../services/article.service';
import { AuthService } from '../services/auth.service';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-view-my-articles',
  templateUrl: './view-my-articles.component.html',
  styleUrls: ['./view-my-articles.component.css'],
})
export class ViewMyArticlesComponent implements OnInit {
  myArticles: Article[] = []; // Liste des articles créés par le journaliste
  loading: boolean = true; // Indicateur de chargement


  constructor(private articleService: ArticleService, private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
 // Récupérer l'email de l'utilisateur connecté depuis Keycloak
 this.authService.getUserProfile().then((profile) => {
  const email = profile.email;

  if (!email) {
    console.error('Email non défini pour l’utilisateur connecté.');
    this.loading = false;
    return;
  }

  // Récupérer les articles créés par cet utilisateur via son email
  this.articleService.getArticlesByAuthorEmail(email).subscribe(
    (articles) => {
      this.myArticles = articles;
      this.loading = false;
    },
    (error) => {
      console.error('Erreur lors de la récupération des articles :', error);
      this.loading = false;
    }
  );
});
    
  }
  deleteArticle(articleId: number): void {
    if (confirm('Are you sure you want to delete this item?')) {
     
        this.articleService.deleteArticle(articleId).subscribe(
          () => {
            
            alert('The article has been successfully deleted.');
            this.router.navigate(['/home']);
          },
          (error) => {
            console.error('Error deleting article:', error);
            this.router.navigate(['/viewmyarticle']);
          }
        );
  
  
}}}
