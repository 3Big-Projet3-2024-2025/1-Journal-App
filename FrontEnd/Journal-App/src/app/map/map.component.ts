import { Component, OnInit } from '@angular/core';
import * as L from 'leaflet';
import { ArticleService } from '../services/article.service';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements OnInit {

  map!: L.Map;

  constructor(private articleService: ArticleService) { }

  ngOnInit(): void {
    this.initializeMap();
  }

  initializeMap(): void {
    // Vérifie si la géolocalisation est disponible dans le navigateur
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          // Récupère les coordonnées de l'utilisateur
          const latitude = position.coords.latitude;
          const longitude = position.coords.longitude;

          // Initialise la carte avec la position de l'utilisateur
          this.map = L.map('map', {
            center: [latitude, longitude], // Centre sur la position de l'utilisateur
            zoom: 13
          });

          // Ajoute un fond de carte (tile layer)
          L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; OpenStreetMap contributors'
          }).addTo(this.map);

          // Ajoute un marqueur à la position de l'utilisateur
          L.marker([latitude, longitude]).addTo(this.map)
            .bindPopup('You are here')
            .openPopup();
        },
        (error) => {
          // Si la géolocalisation échoue, affiche un message d'erreur
          console.error('Erreur de géolocalisation: ', error);
          // Centrer la carte sur Charleroi par défaut
          this.initializeFallbackMap();
        }
      );
    } else {
      // Si la géolocalisation n'est pas supportée
      console.error('La géolocalisation n\'est pas supportée par ce navigateur.');
      this.initializeFallbackMap();
    }
  }

  initializeFallbackMap(): void {
    // Si la géolocalisation échoue, on centre la carte sur Charleroi
    const charleroiLat = 50.4106;  // Latitude de Charleroi
    const charleroiLng = 4.4447;   // Longitude de Charleroi

    this.map = L.map('map', {
      center: [charleroiLat, charleroiLng], // Coordonnées de Charleroi
      zoom: 13
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.map);

    // Ajoute un marqueur à Charleroi
    L.marker([charleroiLat, charleroiLng]).addTo(this.map)
      .bindPopup('Charleroi, Belgique')
      .openPopup();
  }

}
