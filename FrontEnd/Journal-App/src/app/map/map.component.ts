import { Component, OnInit } from '@angular/core';
import * as L from 'leaflet';
import { ArticleService } from '../services/article.service';
import { Article } from '../models/article';
import 'leaflet.markercluster';
import { HttpErrorResponse } from '@angular/common/http';


@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements OnInit {

  

  map!: L.Map;
  articles: Article[] = [];
  markersGroup!: L.MarkerClusterGroup;
  isLoading: boolean = true;
  locationError: string | null = null;
  selectedArticle: Article | null = null; // Article sélectionné
  authMessageVisible: boolean = false; // Variable pour contrôler l'affichage du message
  isMapVisible: boolean = true; // Par défaut, la carte est visible
  isDetailsVisible: boolean = false; // Par défaut, les détails sont masqués

  newsletterTitle: string | null = null;
  authorName: string | null = null;


  constructor(private articleService: ArticleService) { }

  ngOnInit(): void {
    this.initializeMap();
  }

  initializeMap(): void {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const latitude = position.coords.latitude;
          const longitude = position.coords.longitude;
          this.createMap([latitude, longitude], 13);
          this.addUserMarker([latitude, longitude]);
          this.loadArticles();
          this.isLoading = false;
        },
        (error) => {
          console.error('Erreur de géolocalisation: ', error);
          this.locationError = 'Impossible de déterminer votre localisation. Affichage par défaut.';
          this.initializeFallbackMap();
          this.loadArticles();
          this.isLoading = false;
        }
      );
    } else {
      console.error('La géolocalisation n\'est pas supportée par ce navigateur.');
      this.locationError = 'La géolocalisation n\'est pas supportée par votre navigateur. Affichage par défaut.';
      this.initializeFallbackMap();
      this.loadArticles();
      this.isLoading = false;
    }
  }

  initializeFallbackMap(): void {
    const charleroiLat = 50.4106;
    const charleroiLng = 4.4447;
    this.createMap([charleroiLat, charleroiLng], 13);
    this.addUserMarker([charleroiLat, charleroiLng], 'Charleroi, Belgique');
  }

  createMap(center: [number, number], zoom: number): void {
    this.map = L.map('map', {
      center: center,
      zoom: zoom
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.map);

    // Initialiser le groupe de clusters
    this.markersGroup = L.markerClusterGroup();
    this.map.addLayer(this.markersGroup);
  }

  addUserMarker(position: [number, number], popupText: string = 'You are here'): void {
    const userIcon = L.icon({
      iconUrl: 'assets/user.png',
      iconSize: [60, 60], // Taille personnalisée
      iconAnchor: [20, 40], // Point d'ancrage de l'icône
      popupAnchor: [0, -40] // Position de la popup
    });

    L.marker(position, { icon: userIcon }).addTo(this.map)
      .bindPopup(popupText)
      .openPopup();
  }

  loadArticles(): void {
    this.articleService.getArticles().subscribe(
      (data: Article[]) => {
        this.articles = data;
        this.addArticleMarkers();
      },
      (error) => {
        console.error('Erreur lors de la récupération des articles: ', error);
      }
    );
  }

 addArticleMarkers(): void {
  this.articles.forEach(article => {
    if (article.latitude && article.longitude) {
      const articlePosition: [number, number] = [article.latitude, article.longitude];

      const articleIcon = L.icon({
        iconUrl: 'assets/article.png',
        iconSize: [55, 55],
        iconAnchor: [17, 45],
        popupAnchor: [0, -40]
      });

      const articleMarker = L.marker(articlePosition, { icon: articleIcon });

      // Ajoute un bouton avec un data-article-id dans la popup
      articleMarker.bindPopup(`
        <b>${article.title}</b><br>
        ${this.truncateContent(article.content, 100)}<br>
        <button class="btn-learn-more" data-article-id="${article.articleId}">Learn More</button>
      `);

      this.markersGroup.addLayer(articleMarker);

      // Ajouter un écouteur sur l'événement popupopen
      articleMarker.on('popupopen', () => {
        const popupElement = document.querySelector('.btn-learn-more');
        if (popupElement) {
          popupElement.addEventListener('click', (event) => {
            const button = event.target as HTMLElement;
            const articleId = button.getAttribute('data-article-id');
            if (articleId) {
              this.getArticleDetails(Number(articleId)); // Convertir en nombre
            }
          });
        }
      });
    }
  });
}

  
  
getArticleDetails(articleId: number): void {
  this.articleService.getArticleById(articleId).subscribe(
    (data: Article) => {
      this.selectedArticle = data;
      this.isMapVisible = false; // Masque la carte
      this.isDetailsVisible = true; // Affiche les détails

      // Récupérer le titre de la newsletter
      this.getNewsletterTitle(articleId);

      // Récupérer le nom de l'auteur
      this.getAuthorName(articleId);
    },
    (error) => {
      if (error.status === 401) {
        console.error('Erreur 401 : Non authentifié');
        this.authMessageVisible = true; // Affiche le message d'authentification
      } else {
        console.error('Erreur lors de la récupération de l\'article:', error);
      }
    }
  );
}


  truncateContent(content: string, maxLength: number): string {
    return content.length > maxLength ? content.substring(0, maxLength) + '...' : content;
  }

  toggleDetails(): void {
    this.isDetailsVisible = !this.isDetailsVisible; // Inverse l'état des détails
    this.isMapVisible = !this.isDetailsVisible; // Inverse l'état de la carte
    this.initializeMap();
  }



  getNewsletterTitle(articleId: number): void {
    this.articleService.getNewsletterTitleByArticleId(articleId).subscribe(
      (title) => {
        this.newsletterTitle = title; // Stocker le titre
      },
      (error) => {
        console.error('Erreur lors de la récupération du titre de la newsletter:', error);
        this.newsletterTitle = 'Unknown'; // Gérer les erreurs
      }
    );
  }

  getAuthorName(articleId: number): void {
    this.articleService.getAuthorNameByArticleId(articleId).subscribe(
      (name) => {
        this.authorName = name; // Stocker le nom de l'auteur
      },
      (error) => {
        console.error('Erreur lors de la récupération du nom de l\'auteur:', error);
        this.authorName = 'Unknown'; // Gérer les erreurs
      }
    );
  }

}
