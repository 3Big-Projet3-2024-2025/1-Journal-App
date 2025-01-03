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
  validArticles: Article[] = []; 
  addedArticles: Article[] = []; 
  showArticleList: boolean = false;

  
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
              
            );console.log(articles)
            this.validArticles = articles.filter(
              (article) => article.valid && article.newsletter?.newsletterId === id
            );

            
  this.validArticles = this.validArticles.filter(
    (validArticle) =>
      !this.addedArticles.some(
        (addedArticle) => addedArticle.articleId !== validArticle.articleId
      )
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
     
      if (article.newsletter && article.newsletter.newsletterId !== this.newsletter.newsletterId) {
       
        this.articleToConfirm = article;
       
      } else {
       
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
