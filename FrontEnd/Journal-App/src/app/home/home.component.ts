import { Component, OnInit } from '@angular/core';
import { ArticleService } from '../services/article.service';
import { Article } from '../models/article';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  allArticles: Article[] = [];
  allArticles2: Article[] = [];
  searchResults: Article[] = []; 
  searchTerm: string = ''; 

  constructor(private articleService: ArticleService, private router: Router,private cook:CookieService) {}

  ngOnInit(): void {
    this.loadAllArticlesAvailable()
  }

  loadAllArticlesAvailable(): void {
    localStorage.setItem('newsletter', '0');
    this.articleService.getAvailableArticles().subscribe(
      (data: Article[]) => {
        this.allArticles = data; 

      },
      (error) => {
        console.error('Error loading articles:', error);
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
          console.error('Error searching articles:', error);
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
