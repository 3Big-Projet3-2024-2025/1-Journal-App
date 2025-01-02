import { Component, OnInit } from '@angular/core';
import { Article } from '../models/article';
import { ArticleService } from '../services/article.service';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { Newsletter } from '../models/newsletter';

@Component({
  selector: 'app-view-my-articles',
  templateUrl: './view-my-articles.component.html',
  styleUrls: ['./view-my-articles.component.css'],
})
export class ViewMyArticlesComponent implements OnInit {
  myArticles: Article[] = [];
  filteredArticles: Article[] = [];
  newsletters: Newsletter[] = [];
  loading: boolean = true;
  selectedNewsletterId: number | null = null;

  constructor(
    private articleService: ArticleService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    console.log("Initialisation du composant...");
    this.authService.getUserProfile().then((profile) => {
      const email = profile.email;
  
      if (!email) {
        console.error("Email non défini pour l’utilisateur connecté.");
        this.loading = false;
        return;
      }
  
      this.articleService.getArticlesByAuthorEmail(email).subscribe(
        (articles) => {
          console.log("Articles récupérés :", articles);
          this.myArticles = articles;
  
          // Extraire les newsletters uniques
          this.newsletters = this.getUniqueNewsletters(this.myArticles);
          console.log("Newsletters extraites :", this.newsletters);
  
          // Si des newsletters existent, sélectionner la première par défaut
          if (this.newsletters.length > 0) {
            this.selectedNewsletterId = this.newsletters[0].newsletterId;
            console.log(
              "Newsletter sélectionnée par défaut :",
              this.selectedNewsletterId
            );
            this.onNewsletterChange();
          }
  
          this.loading = false;
        },
        (error) => {
          console.error("Erreur lors de la récupération des articles :", error);
          this.loading = false;
        }
      );
    });
  }
  
  getUniqueNewsletters(articles: Article[]): Newsletter[] {
    const newslettersMap: { [key: number]: Newsletter } = {};
  
    articles.forEach((article) => {
      if (article.newsletter) {
        newslettersMap[article.newsletter.newsletterId] = article.newsletter;
      }
    });
  
    return Object.values(newslettersMap);
  }
  
  onNewsletterChange(): void {
    console.log("Newsletter sélectionnée :", this.selectedNewsletterId);
    this.filteredArticles = this.myArticles.filter(
      (article) =>
        article.newsletter?.newsletterId === Number(this.selectedNewsletterId)
    );
    console.log("Articles filtrés :", this.filteredArticles);
  }
  
  deleteArticle(articleId: number): void {
    if (confirm("Are you sure you want to delete this item?")) {
      this.articleService.deleteArticle(articleId).subscribe(
        () => {
          alert("The article has been successfully deleted.");
          this.myArticles = this.myArticles.filter(
            (article) => article.articleId !== articleId
          );
          this.onNewsletterChange(); // Rafraîchir les articles filtrés
        },
        (error) => {
          console.error("Error deleting article:", error);
        }
      );
    }
  }
}  
