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
  validArticles: Article[] = []; // Articles valides récupérés depuis la DB
  addedArticles: Article[] = []; // Articles ajoutés
  showArticleList: boolean = false;

  constructor(
    private newsletterService: ManageNewsletterService,
    private route: ActivatedRoute,
    private router: Router,
    private articleService: ArticleService
  ) {}

  ngOnInit(): void {
    // Récupération de la newsletter
    const id = +localStorage.getItem('seeidnewsletter')!;
    this.newsletterService.getnewsletterById(id).subscribe(
      (data) => this.newsletter = data,
      (error) => console.error('Erreur lors de la récupération de la newsletter:', error)
    );

    // Récupération des articles valides
    this.articleService.getArticles().subscribe(
      (articles) => {
        this.validArticles = articles.filter((article) => article.valid);
      },
      (error) => console.error('Erreur lors de la récupération des articles:', error)
    );

    // Récupération des articles ajoutés depuis le localStorage
    const storedArticles = localStorage.getItem('addedArticles');
    if (storedArticles) {
      this.addedArticles = JSON.parse(storedArticles);
    }
  }

  // Afficher/Cacher la liste des articles disponibles
  toggleArticleList(): void {
    this.showArticleList = !this.showArticleList;
  }

  // Ajouter un article avec vérification des doublons
  addArticleToNewsletter(article: Article): void {
    const exists = this.addedArticles.some((a) => a.articleId === article.articleId);
    if (!exists) {
      this.addedArticles.push(article);
      this.updateLocalStorage();
    } else {
      alert('Cet article a déjà été ajouté.');
    }
  }

  // Supprimer un article ajouté
  deleteArticle(article: Article): void {
    this.addedArticles = this.addedArticles.filter((a) => a.articleId !== article.articleId);
    this.updateLocalStorage();
  }

  // Mettre à jour le localStorage
  updateLocalStorage(): void {
    localStorage.setItem('addedArticles', JSON.stringify(this.addedArticles));
  }

  goBack(): void {
    localStorage.setItem('put', '');
    localStorage.setItem('idnewsletter', '');
    this.router.navigate(['/crud/newsletter']);
  }
}
