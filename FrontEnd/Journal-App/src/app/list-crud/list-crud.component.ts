import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ManageNewsletterService } from '../services/manage-newsletter.service';
import { ArticleService } from '../services/article.service';
import { Article } from '../models/article';

@Component({
  selector: 'app-list-crud',
  templateUrl: './list-crud.component.html',
  styleUrl: './list-crud.component.css',
})
export class ListCrudComponent {
  type: string = ''; 
  titre: string = '';
  titretable: string = '';
  validArticles: Article[] = [];
  nonValidArticles: Article[] = [];

  constructor(
    private route: ActivatedRoute,
    private Managenewsletter: ManageNewsletterService,
    private articleService: ArticleService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.type = params['type']; 
      this.loadDataBasedOnType(); 
      this.articleService.getArticles().subscribe(
        (data: Article[]) => {
          // Séparer les articles validés et non validés
          this.validArticles = data.filter(article => article.valid);
          this.nonValidArticles = data.filter(article => !article.valid);
        },
        (error) => {
          console.error('Erreur lors de la récupération des articles: ', error);
        }
      );
    });
  }

  loadDataBasedOnType() {
    if (this.type === 'articles') {
      console.log('Charger les articles');
      this.titre = 'Management of the articles';
      this.titretable = 'articles';
     
    } else if (this.type === 'newsletters') {
      console.log('Charger les newsletters');
      this.titre = 'Management of the Newsletter';
      this.titretable = 'newsletters';

      this.Managenewsletter.GetALlnewsletter().subscribe({
        next: (data) => {
          console.log(data);
        },
      });

    } else {
      console.error('Type invalide');
    }
  }

  // Méthode pour valider l'article
  validateArticle(article: Article) {
    article.valid = true;  // Mettre à jour l'article en le validant
    this.articleService.updateArticle(article.articleId,article).subscribe(
      (updatedArticle) => {
        console.log('Article validé', updatedArticle);
        // Recharger les articles après validation
        this.validArticles.push(updatedArticle);
        this.nonValidArticles = this.nonValidArticles.filter(a => a !== updatedArticle);
      },
      (error) => {
        console.error('Erreur lors de la validation de l\'article:', error);
      }
    );
  }
}
