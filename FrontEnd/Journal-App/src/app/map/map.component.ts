import { Component, OnInit } from '@angular/core';
import * as L from 'leaflet';
import { ArticleService } from '../services/article.service';
import { Article } from '../models/article';
import 'leaflet.markercluster';

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
    L.marker(position).addTo(this.map)
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
        const markerIcon = L.icon({
          iconUrl: 'assets/marker-icon.png',
          shadowUrl: 'assets/marker-shadow.png',
          iconSize: [25, 41],
          iconAnchor: [13, 41],
          popupAnchor: [1, -34],
          shadowSize: [41, 41]
        });

        const articleMarker = L.marker(articlePosition, { icon: markerIcon })
          .bindPopup(`
            <b>${article.title}</b><br>
            ${this.truncateContent(article.content, 100)}<br>
            <a href="/articles/${article.article_id}">Lire plus</a>
          `);

        this.markersGroup.addLayer(articleMarker);
      }
    });
  }

  truncateContent(content: string, maxLength: number): string {
    return content.length > maxLength ? content.substring(0, maxLength) + '...' : content;
  }
}
