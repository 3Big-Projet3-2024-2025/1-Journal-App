import { Component, OnInit } from '@angular/core';
import { Article } from '../models/article';
import { ArticleService } from '../services/article.service';
import { ActivatedRoute, Router } from '@angular/router';
import * as L from 'leaflet';

@Component({
  selector: 'app-update-article',
  templateUrl: './update-article.component.html',
  styleUrls: ['./update-article.component.css']
})
export class UpdateArticleComponent implements OnInit {
  successMessage: string = "";
  
  customIcon = L.icon({
    iconUrl: 'assets/warning_icon.png',
    iconSize: [50, 50], // Taille de l'icône
    iconAnchor: [25, 50],
    popupAnchor: [0, -50]
  });

  map!: L.Map; // Carte Leaflet
  marker!: L.Marker; // Marqueur pour la position sélectionnée

  articleToUpdate: Article = {
    articleId: 0,
    title: '',
    content: '',
    publicationDate: new Date(),
    longitude: 0.0,
    latitude: 0.0,
    user_id: 0,
    newsletter_id: 0,
    valid: false,
    backgroundColor: '#ffffff',
    read: false,
  };

  selectedFiles: File[] = [];

  constructor(
    private articleService: ArticleService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadArticle();
  }

  // Charger l'article existant et initialiser la carte
  loadArticle(): void {
    const articleId = Number(this.route.snapshot.paramMap.get('id')); // ID de l'article dans l'URL
    if (!articleId) {
      console.error('Invalid article ID');
      return;
    }

    this.articleService.getArticleById(articleId).subscribe(
      (article) => {
        this.articleToUpdate = article;
        this.initializeMap([article.latitude, article.longitude]); // Initialiser la carte à la position de l'article
      },
      (error) => {
        console.error('Error loading article:', error);
      }
    );
  }

  // Initialisation de la carte
  initializeMap(center: [number, number]): void {
    const zoomLevel = 13;
    this.map = L.map('map').setView(center, zoomLevel);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.map);

    this.marker = L.marker(center, { icon: this.customIcon, draggable: true }).addTo(this.map);

    // Mise à jour des coordonnées en déplaçant le marqueur
    this.marker.on('dragend', () => {
      const position = this.marker.getLatLng();
      this.articleToUpdate.latitude = position.lat;
      this.articleToUpdate.longitude = position.lng;
    });

    // Mise à jour des coordonnées en cliquant sur la carte
    this.map.on('click', (e: L.LeafletMouseEvent) => {
      const position = e.latlng;
      this.marker.setLatLng(position);
      this.articleToUpdate.latitude = position.lat;
      this.articleToUpdate.longitude = position.lng;
    });
  }

  // Valider les fichiers
  validateFileCount(event: any): void {
    const files = event.target.files;

    if (files.length > 3) {
      alert('You can upload a maximum of 3 images.');
      event.target.value = '';
      return;
    }

    for (const file of files) {
      if (!file.type.startsWith('image/')) {
        alert('Only image files are allowed.');
        event.target.value = '';
        return;
      }
    }

    this.selectedFiles = Array.from(files);
    alert(`${files.length} file(s) selected successfully.`);
  }

  // Mettre à jour l'article
  updateArticle(): void {
    const articleId = this.articleToUpdate.articleId;

    this.articleService.updateArticle(articleId, this.articleToUpdate).subscribe(
      async (updatedArticle) => {
        console.log('Article updated successfully:', updatedArticle);

        // Mettre à jour les images si nécessaires
        if (this.selectedFiles.length > 0) {
          await this.uploadImages(articleId);
        }

        this.successMessage = "Update article successfully";
        this.router.navigate(['crud/article']);
      },
      (error) => {
        console.error('Error updating article:', error);
      }
    );
  }

  // Téléverser les nouvelles images
  async uploadImages(articleId: number): Promise<void> {
    for (const file of this.selectedFiles) {
      const base64Image = await this.convertToBase64(file);
      const imageToAdd = {
        imageId: 0,
        imagePath: base64Image,
        articleId: articleId
      };

      this.articleService.addImage(imageToAdd).subscribe(
        (imageSaved) => {
          console.log('Image updated successfully:', imageSaved);
        },
        (error) => {
          console.error('Error updating image:', error);
        }
      );
    }
  }

  // Conversion d'image en Base64
  convertToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => {
        const base64String = (reader.result as string).split(',')[1];
        resolve(base64String);
      };
      reader.onerror = (error) => reject(error);
    });
  }

  goBack(): void {
    this.router.navigate(['crud/article']);
  }
}
