import { Component, OnInit } from '@angular/core';
import { Article } from '../models/article';
import { ArticleService } from '../services/article.service';
import { ActivatedRoute, Router } from '@angular/router';
import * as L from 'leaflet';
import { ImageService } from '../services/image.service';
import { Image } from '../models/image';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-view-article-detail',
  templateUrl: './view-article-detail.component.html',
  styleUrls: ['./view-article-detail.component.css']
})
export class ViewArticleDetailComponent implements OnInit {
  successMessage: string = "";
  articleToView!: Article;
  newsletterTitle: string | null = null;
  authorName: string | null = null;
  images: Image[] = [];

  map!: L.Map; 
  marker!: L.Marker;

  customIcon = L.icon({
    iconUrl: 'assets/warning_icon.png',
    iconSize: [50, 50],
    iconAnchor: [25, 50],
    popupAnchor: [0, -50]
  });

  constructor(
    private articleService: ArticleService,
    private route: ActivatedRoute,
    private router: Router,
    private imageService: ImageService
  ) {}

  ngOnInit(): void {
    this.loadArticle();
  }

  loadArticle(): void {
    const articleId = Number(this.route.snapshot.paramMap.get('id'));
    if (!articleId) {
      console.error('Invalid article ID');
      return;
    }

    this.articleService.getArticleById(articleId).subscribe(
      (article) => {
        this.articleToView = article;
        this.initializeMap([article.latitude, article.longitude]);
        this.loadAdditionalDetails(articleId);
      },
      (error) => {
        console.error('Error loading article:', error);
      }
    );
  }

  initializeMap(center: [number, number]): void {
    this.map = L.map('map', { scrollWheelZoom: false }).setView(center, 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.map);

    this.marker = L.marker(center, { icon: this.customIcon, draggable: false }).addTo(this.map);
  }

  loadAdditionalDetails(articleId: number): void {
    this.articleService.getNewsletterTitleByArticleId(articleId).subscribe(
      (title) => (this.newsletterTitle = title),
      () => (this.newsletterTitle = 'Unknown')
    );

    this.articleService.getAuthorNameByArticleId(articleId).subscribe(
      (name) => (this.authorName = name),
      () => (this.authorName = 'Unknown')
    );

    this.imageService.getImagesByArticleId(articleId).subscribe(
      (images: Image[]) => (this.images = images),
      (error: HttpErrorResponse) => {
        console.error('Error loading images:', error);
        this.images = [];
      }
    );
  }

  goBack(): void {
    this.router.navigate(['crud/article']);
  }


  
}
