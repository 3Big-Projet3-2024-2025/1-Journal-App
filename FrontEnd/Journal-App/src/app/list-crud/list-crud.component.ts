import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ManageNewsletterService } from '../services/manage-newsletter.service';
import { ArticleService } from '../services/article.service';
import { Article } from '../models/article';
import { Newsletter } from '../models/newsletter';

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
  newsletters: Newsletter[]=[];

  constructor(
    private route: ActivatedRoute,
    private Managenewsletter: ManageNewsletterService,
    private articleService: ArticleService,
    private manageNewsletter : ManageNewsletterService
  ) {}

  ngOnInit(): void {
    // Vérifier la variable 'newsletter' dans le localStorage
     const isNewsletter = localStorage.getItem('newsletter') === '1'

    //alert(isNewsletter)
    if (isNewsletter) {
      this.type = 'newsletters';
      this.loadDataBasedOnType();
    } else {
      this.type = 'articles';
      this.loadDataBasedOnType();
    }
  }


  loadDataBasedOnType() {
    if (this.type === 'articles') {
      console.log('Charger les articles');
      this.titre = 'Management of the articles';
      this.titretable = 'articles';

      this.articleService.getArticles().subscribe(
        (data) => {
          // Séparer les articles validés et non validés
          this.validArticles = data.filter(article => article.valid);
          this.nonValidArticles = data.filter(article => !article.valid);
        },
        (error) => {
          console.error('Erreur lors de la récupération des articles: ', error);
        }
      );
    } else if (this.type === 'newsletters') {
      console.log('Charger les newsletters');
      this.titre = 'Management of the Newsletter';
      this.titretable = 'newsletters';

      this.manageNewsletter.GetALlnewsletter().subscribe(
        (data) => {
          this.newsletters = data; // Stocker les newsletters récupérées
          console.log(data);
        },
        (error) => {
          console.error('Erreur lors de la récupération des newsletters: ', error);
        }
      );
    } else {
      console.error('Type invalide');
    }
  }


  validateArticle(article: Article) {
    article.valid = true;  
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
