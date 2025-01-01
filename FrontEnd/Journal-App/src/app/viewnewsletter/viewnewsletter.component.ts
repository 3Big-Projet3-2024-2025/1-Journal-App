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
  fromarticledetail: string | null | undefined;
  display="display; inline";
  isButtonVisible=true;

  constructor(
    private newsletterService: ManageNewsletterService,
    private route: ActivatedRoute,
    private router: Router,
    private articleService: ArticleService
  ) {}

  ngOnInit(): void {
    const newsletterId = +localStorage.getItem('seeidnewsletter')!;
    this.getnewsletter(newsletterId);
    const fromArticleDetail = localStorage.getItem('fromArticleDetail');
    this.isButtonVisible = fromArticleDetail !== 'ok';
  }

  getnewsletter(id: number): void {
    this.newsletterService.getnewsletterById(id).subscribe(
      (data) => {
        this.newsletter = data;
        this.articleService.getArticles().subscribe(
          (articles) => {
            this.addedArticles = articles.filter(
              (article) => article.newsletter?.newsletterId === id
            );
            this.validArticles = articles.filter(
              (article) => article.valid && article.newsletter?.newsletterId !== id
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
  addArticleToNewsletter(article: Article): void {
    if (this.newsletter) {
      // Si l'article est déjà lié à une autre newsletter
      if (article.newsletter && article.newsletter.newsletterId !== this.newsletter.newsletterId) {
        // On demande confirmation à l'utilisateur en assignant articleToConfirm
        this.articleToConfirm = article;
        // NE PAS appeler confirmAddArticle(article) ici !
        // L'utilisateur doit confirmer manuellement, par exemple via un bouton dans le template.
      } else {
        // Si l'article n'est pas lié à une autre newsletter, on l'ajoute directement
        this.confirmAddArticle(article);
      }
    }
  }
  

  confirmAddArticle(article: Article): void {
    if (this.newsletter) {
      this.newsletterService.addArticleToNewsletter(this.newsletter.newsletterId, article.articleId).subscribe(
        () => {
          this.articleToConfirm = null;
          alert('Article ajouté avec succès.');
          this.getnewsletter(this.newsletter!.newsletterId);
        },
        (error) => {
          console.error("Erreur lors de l'ajout de l'article:", error);
        }
      );
    }
  }
  

  cancelAddArticle(): void {
    this.articleToConfirm = null;
  }

  deleteArticle(article: Article): void {
    if (this.newsletter) {
      this.newsletterService.removeArticleFromNewsletter(this.newsletter.newsletterId, article.articleId).subscribe(
        () => {
          // Après suppression, on peut aussi recharger la newsletter si nécessaire,
          // mais vous pouvez garder l'ancienne logique.
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
