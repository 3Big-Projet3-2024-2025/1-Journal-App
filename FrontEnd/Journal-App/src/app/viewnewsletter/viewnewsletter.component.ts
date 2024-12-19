import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Newsletter } from '../models/newsletter';
import { ManageNewsletterService } from '../services/manage-newsletter.service';
import { Article } from '../models/article';
import { ArticleService } from '../services/article.service';

@Component({
  selector: 'app-viewnewsletter',
  templateUrl: './viewnewsletter.component.html',
  styleUrls: ['./viewnewsletter.component.css']
})
export class ViewnewsletterComponent implements OnInit {

  newsletter: Newsletter | null = null;
  validArticles: Article[] = []; // Articles disponibles pour ajout
  addedArticles: Article[] = []; // Articles associés à la newsletter
  showArticleList: boolean = false;

  // Pour la confirmation
  articleToConfirm: Article | null = null;

  constructor(
    private newsletterService: ManageNewsletterService,
    private route: ActivatedRoute,
    private router: Router,
    private articleService: ArticleService
  ) {}

  ngOnInit(): void {
    const newsletterId = +localStorage.getItem('seeidnewsletter')!;

    this.newsletterService.getnewsletterById(newsletterId).subscribe(
      (data) => {
        this.newsletter = data;

        this.articleService.getArticles().subscribe(
          (articles) => {
            this.addedArticles = articles.filter(
              (article) => article.newsletter?.newsletterId === newsletterId
            );

            this.validArticles = articles.filter(
              (article) => article.valid && article.newsletter?.newsletterId !== newsletterId
            );
          },
          (error) => console.error('Erreur lors de la récupération des articles:', error)
        );
      },
      (error) => console.error('Erreur lors de la récupération de la newsletter:', error)
    );
  }

  toggleArticleList(): void {
    this.showArticleList = !this.showArticleList;
  }

  // Ajouter un article à la newsletter
  addArticleToNewsletter(article: Article): void {
    if (this.newsletter) {
      // Vérifier si l'article est déjà lié à une autre newsletter
      if (article.newsletter && article.newsletter.newsletterId !== this.newsletter.newsletterId) {
        // Afficher une demande de confirmation
        this.articleToConfirm = article;
      } else {
        // Ajouter directement si non lié ou déjà lié à cette newsletter
        this.confirmAddArticle(article);
      }
    }
  }

  // Confirmer l'ajout de l'article à la newsletter
  confirmAddArticle(article: Article): void {
    if (this.newsletter) {
      this.newsletterService.addArticleToNewsletter(this.newsletter.newsletterId, article.articleId).subscribe(
        () => {
          this.addedArticles.push(article);
          this.validArticles = this.validArticles.filter((a) => a.articleId !== article.articleId);
          this.articleToConfirm = null;
          alert('Article ajouté avec succès.');
        },
        (error) => {
          console.error('Erreur lors de l\'ajout de l\'article:', error);
         
        }
      );
    }
  }

  // Annuler la confirmation
  cancelAddArticle(): void {
    this.articleToConfirm = null;
  }

  deleteArticle(article: Article): void {
    if (this.newsletter) {
      this.newsletterService.removeArticleFromNewsletter(this.newsletter.newsletterId, article.articleId).subscribe(
        () => {
          this.validArticles.push(article);
          this.addedArticles = this.addedArticles.filter((a) => a.articleId !== article.articleId);
         
        },
        (error) => {
          console.error('Erreur lors de la suppression de l\'article:', error);
        
        }
      );
    }
  }

  goBack(): void {
    localStorage.setItem('put', '');
    localStorage.setItem('idnewsletter', '');
    this.router.navigate(['/crud/newsletter']);
  }
}
