import { Component, OnInit } from '@angular/core';
import { ArticleService } from '../services/article.service';
import { Article } from '../models/article';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  allArticles: Article[] = [];
  searchResults: Article[] = []; 
  searchTerm: string = ''; 

  constructor(private articleService: ArticleService, private router: Router) {}

  ngOnInit(): void {
    this.loadAllArticles();
  }

  loadAllArticles(): void {
    this.articleService.getAvailableArticles().subscribe(
      (data: Article[]) => {
        this.allArticles = data; 
      },
      (error) => {
        console.error('Erreur lors du chargement des articles:', error);
      }
    );
  }

  onSearchChange(term: string): void {
    if (term.trim()) {
      // Rechercher les articles correspondants
      this.articleService.searchValidArticles(term).subscribe(
        (data: Article[]) => {
          this.searchResults = data; 
        },
        (error) => {
          console.error('Erreur lors de la recherche des articles:', error);
        }
      );
    } else {
      this.searchResults = [];
    }
  }

  viewArticle(id: number): void {
    this.router.navigate(['/articles', id]);
  }
}
