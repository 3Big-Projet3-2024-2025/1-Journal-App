import { Component, OnInit } from '@angular/core';
import { Article } from '../models/article';
import { ArticleService } from '../services/article.service';
import { Image } from '../models/image';
import { ManageNewsletterService } from '../services/manage-newsletter.service';
import { Newsletter } from '../models/newsletter';
import { KeycloakService } from 'keycloak-angular';
import { UsersService } from '../services/users.service';
import { Router } from '@angular/router';
import * as L from 'leaflet';

@Component({
  selector: 'app-create-article-journalist',
  templateUrl: './create-article-journalist.component.html',
  styleUrls: ['./create-article-journalist.component.css']
})
export class CreateArticleJournalistComponent implements OnInit {
  articleToAdd: Article = {
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
    read: false
  };

  successMessage: string = "";
  selectedFiles: File[] = [];

  map!: L.Map; // Carte Leaflet
  marker!: L.Marker; // Marqueur interactif
  customIcon = L.icon({
    iconUrl: 'assets/warning_icon.png', // Icône personnalisée
    iconSize: [50, 50],
    iconAnchor: [25, 50],
    popupAnchor: [0, -50]
  });

  constructor(
    private articleService: ArticleService,
    private newsletterService: ManageNewsletterService,
    private keycloakService: KeycloakService,
    private userService: UsersService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initializeComponent();
    this.initializeMap();
  }

  private async initializeComponent(): Promise<void> {
    try {
      await this.getUserId();
      this.setUserNewsletter();
    } catch (error) {
      console.error('Erreur lors de l\'initialisation du composant:', error);
    }
  }

  initializeMap(): void {
    const defaultLocation: [number, number] = [50.4106, 4.4447]; // Position par défaut : Charleroi
    this.map = L.map('map').setView(defaultLocation, 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.map);

    this.marker = L.marker(defaultLocation, {
      icon: this.customIcon,
      draggable: true
    }).addTo(this.map);

    // Mettre à jour les coordonnées sur drag-and-drop
    this.marker.on('dragend', () => {
      const position = this.marker.getLatLng();
      this.articleToAdd.latitude = position.lat;
      this.articleToAdd.longitude = position.lng;
    });

    // Permettre le clic sur la carte pour déplacer le marqueur
    this.map.on('click', (e: L.LeafletMouseEvent) => {
      const position = e.latlng;
      this.marker.setLatLng(position);
      this.articleToAdd.latitude = position.lat;
      this.articleToAdd.longitude = position.lng;
    });
  }

  getUserId(): Promise<void> {
    return new Promise((resolve, reject) => {
      const keycloakId = this.keycloakService.getKeycloakInstance().tokenParsed?.sub;
      if (!keycloakId) {
        reject('Aucun ID Keycloak trouvé');
        return;
      }
      this.userService.getUserByKeycloakId(keycloakId).subscribe(
        (user) => {
          this.articleToAdd.user_id = user.userId;
          resolve();
        },
        (error) => {
          console.error('Erreur lors de la récupération de l\'utilisateur:', error);
          reject(error);
        }
      );
    });
  }

  setUserNewsletter(): void {
    if (this.articleToAdd.user_id === 0) {
      console.warn('Impossible de récupérer la newsletter car l\'user_id est manquant.');
      return;
    }

    this.newsletterService.getNewslettersForJournalist(this.articleToAdd.user_id).subscribe(
      (newsletters: Newsletter[]) => {
        if (newsletters && newsletters.length > 0) {
          this.articleToAdd.newsletter_id = newsletters[0].newsletterId;
        } else {
          console.error("L'utilisateur n'est journaliste dans aucune newsletter.");
        }
      },
      (error) => {
        console.error('Erreur lors de la récupération des newsletters du journaliste:', error);
      }
    );
  }

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

  async addArticle(): Promise<void> {
    if (this.articleToAdd.user_id === 0 || this.articleToAdd.newsletter_id === 0) {
      console.error('User ID ou Newsletter ID manquant');
      return;
    }

    this.articleService.getNewsletterBackgroundColor(this.articleToAdd.newsletter_id).subscribe(
      (backgroundColor: string) => {
        this.articleToAdd.backgroundColor = backgroundColor;
        this.articleService.addArticle(this.articleToAdd).subscribe(
          async (newArticle) => {
            if (this.selectedFiles.length > 0) {
              await this.uploadImages(newArticle.articleId);
            }
            this.resetForm();
            this.successMessage = "Article sent successfully";
            this.router.navigate(['/home'])
          },
          (error) => console.error('Erreur lors de l\'ajout de l\'article:', error)
        );
      },
      (error) => console.error('Erreur lors de la récupération de la couleur de fond:', error)
    );
  }

  async uploadImages(articleId: number): Promise<void> {
    for (const file of this.selectedFiles) {
      const base64Image = await this.convertToBase64(file);
      const imageToAdd: Image = { imageId: 0, imagePath: base64Image, articleId: articleId };

      this.articleService.addImage(imageToAdd).subscribe(
        () => console.log('Image ajoutée avec succès'),
        (error) => console.error('Erreur lors de l\'ajout de l\'image:', error)
      );
    }
  }

  resetForm(): void {
    this.articleToAdd = {
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
      read: false
    };
    this.selectedFiles = [];
  }

  convertToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve((reader.result as string).split(',')[1]);
      reader.onerror = (error) => reject(error);
    });
  }
}
