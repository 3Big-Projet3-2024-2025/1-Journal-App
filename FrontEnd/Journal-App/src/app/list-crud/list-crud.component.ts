import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ManageNewsletterService } from '../services/manage-newsletter.service';
import { ArticleService } from '../services/article.service';
import { Article } from '../models/article';
import { Newsletter } from '../models/newsletter';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-list-crud',
  templateUrl: './list-crud.component.html',
  styleUrls: ['./list-crud.component.css'],
})
export class ListCrudComponent {
  type: string = '';
  titre: string = '';
  titretable: string = '';
  validArticles: Article[] = [];
  nonValidArticles: Article[] = [];
  newsletters: Newsletter[] = [];

  constructor(
    private route: ActivatedRoute,
    private articleService: ArticleService,
    private manageNewsletterService: ManageNewsletterService,
    private cookieService: CookieService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const isNewsletter = localStorage.getItem('newsletter') === '1';

    if (isNewsletter) {
      this.type = 'newsletters';
    } else {
      this.type = 'articles';
    }

    this.loadDataBasedOnType();
  }

  loadDataBasedOnType(): void {
    if (this.type === 'articles') {
      this.titre = 'Management of the Articles';
      this.titretable = 'articles';

      this.articleService.getArticles().subscribe(
        (articles) => {
          this.validArticles = articles.filter((article) => article.valid);
          this.nonValidArticles = articles.filter((article) => !article.valid);
        },
        (error) => {
          console.error('Error fetching articles:', error);
        }
      );
    } else if (this.type === 'newsletters') {
      this.titre = 'Management of the Newsletters';
      this.titretable = 'newsletters';

      this.manageNewsletterService.GetALlnewsletter().subscribe(
        (newsletters) => {
          this.newsletters = newsletters;
        },
        (error) => {
          console.error('Error fetching newsletters:', error);
        }
      );
    }
  }

  validateArticle(article: Article): void {
    article.valid = true;

    this.articleService.updateArticle(article.articleId, article).subscribe(
      (updatedArticle) => {
        this.validArticles.push(updatedArticle);
        this.nonValidArticles = this.nonValidArticles.filter(
          (a) => a.articleId !== article.articleId
        );
      },
      (error) => {
        console.error('Error validating article:', error);
      }
    );
  }

  confirmDelete(id: number, type: string): void {
    if (confirm('Are you sure you want to delete this item?')) {
      if (type === 'article') {
        this.articleService.deleteArticle(id).subscribe(
          () => {
            this.router.navigate(['/home'])
          },
          (error) => {console.error('Error deleting article:', error) ,this.router.navigate(['/home'])}
          
        );
      } else if (type === 'newsletter') {
        this.manageNewsletterService.deletenewsletter(id).subscribe(
          () => {
            this.router.navigate(['/home'])
          },
          (error) => {console.error('Error deleting article:', error) ,this.router.navigate(['/home'])}
        );
      }
    }
  }
  
}
