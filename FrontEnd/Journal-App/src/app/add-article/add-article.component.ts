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
  selector: 'app-add-article',
  templateUrl: './add-article.component.html',
  styleUrls: ['./add-article.component.css']
})
export class AddArticleComponent implements OnInit {
  successMessage: string = "";

  customIcon = L.icon({
    iconUrl: 'assets/warning_icon.png', 
    iconSize: [50, 50], // size icon
    iconAnchor: [25, 50], // size icon
    popupAnchor: [0, -50] 
  });

  map!: L.Map;// map leaflet
  marker!: L.Marker; // Marqueur for selected position

  constructor(
    private articleService: ArticleService, 
    private newsletterService: ManageNewsletterService,
    private keycloakService: KeycloakService,
    private userService: UsersService,
    private router: Router
  ) {}
  
  

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
    backgroundColor : '#ffffff',
    read : false
  };

  selectedFiles: File[] = []; 
  newsletters: Newsletter[] = []; 
  selectedNewsletterId: number | null = null; 


  ngOnInit(): void {
    localStorage.setItem('newsletter', '0');
    this.getUserId().then(() => {
      this.loadNewsletters();
      this.initializeMapForForm();
    });
  }

  // Init map
  initializeMapForForm(): void {
    const charleroiLat = 50.4106;
    const charleroiLng = 4.4447;

    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const latitude = position.coords.latitude;
          const longitude = position.coords.longitude;
          this.createFormMap([latitude, longitude], 13); // Position utilisateur
        },
        () => {
          this.createFormMap([charleroiLat, charleroiLng], 13); // Position par défaut : Charleroi
        }
      );
    } else {
      this.createFormMap([charleroiLat, charleroiLng], 13); // Position par défaut : Charleroi
    }
  }

  createFormMap(center: [number, number], zoom: number): void {
    this.map = L.map('map').setView(center, zoom);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.map);

    // Ajouter un marqueur draggable avec l'icône personnalisée
    this.marker = L.marker(center, { icon: this.customIcon, draggable: true }).addTo(this.map);

    this.marker.on('dragend', () => {
      const position = this.marker.getLatLng();
      this.articleToAdd.latitude = position.lat;
      this.articleToAdd.longitude = position.lng;
    });

    this.map.on('click', (e: L.LeafletMouseEvent) => {
      const position = e.latlng;
      this.marker.setLatLng(position); // Déplace le marqueur
      this.articleToAdd.latitude = position.lat;
      this.articleToAdd.longitude = position.lng;
    });

    this.articleToAdd.latitude = center[0];
    this.articleToAdd.longitude = center[1];
  }

  

  // Récupérer l'ID de l'utilisateur connecté via Keycloak
  getUserId(): Promise<void> {
    return new Promise((resolve, reject) => {
      const keycloakId = this.keycloakService.getKeycloakInstance().tokenParsed?.sub;
      if (keycloakId) {
        this.userService.getUserByKeycloakId(keycloakId).subscribe(
          (user) => {
            // Assigner l'ID de l'utilisateur au `user_id` de l'article
            this.articleToAdd.user_id = user.userId;
            //console.log('Utilisateur connecté:', user);
            resolve();
          },
          (error) => {
            console.error('Erreur lors de la récupération de l\'utilisateur:', error);
            reject(error);
          }
        );
      } else {
        reject('Keycloak ID non trouvé');
      }
    });
  }

 

  // Charger les newsletters disponibles
  loadNewsletters(): void {
    this.newsletterService.GetALlnewsletter().subscribe(
      (data: Newsletter[]) => {
        this.newsletters = data;
  
       //default newsletter
        if (this.newsletters.length > 0) {
          this.selectedNewsletterId = Number(this.newsletters[0].newsletterId);
        }
      },
      (error) => {
        console.error('Erreur lors du chargement des newsletters:', error);
      }
    );
  }

  // Validation du nombre de fichiers et type de fichiers
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

  // Conversion d'image en base64
  convertToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => {
        const base64String = (reader.result as string).split(',')[1]; // Supprimer le préfixe "data:image/...;base64,"
        resolve(base64String);
      };
      reader.onerror = (error) => reject(error);
    });
  }
  
  goBack(): void {
   

    this.router.navigate(['crud/article']);
  }

  addArticle(): void {
    this.getUserId();
    if (this.selectedNewsletterId !== null) {
      this.articleToAdd.newsletter_id = Number(this.selectedNewsletterId); 
    }
  
    /*if (this.articleToAdd.user_id === 0) {
      console.error('user id missing');
      return;
    }*/

    if (this.articleToAdd.newsletter_id === 0) {
      console.error('L\'ID de la newsletter est manquant');
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
            this.router.navigate(['crud/article']);
          },
          (error) => {
            console.error('Erreur lors de l\'ajout de l\'article:', error);
          }
        );
      },
      (error) => {
        console.error('Erreur lors de la récupération de la couleur de fond de la newsletter:', error);
      }
    );
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
      read: false,
    };
    this.selectedFiles = [];
    this.selectedNewsletterId = null;
    this.successMessage = "Article sent successfully";
  }

  

  async uploadImages(articleId: number): Promise<void> {
    for (const file of this.selectedFiles) {
      const base64Image = await this.convertToBase64(file);
      const imageToAdd: Image = {
        imageId: 0, // Valeur par défaut pour l'ID de l'image
        imagePath: base64Image, // Contenu encodé en Base64 (sans préfixe)
        articleId: articleId    // ID de l'article
      };
  
      this.articleService.addImage(imageToAdd).subscribe(
        (imageSaved) => {
          //console.log('Image ajoutée avec succès:', imageSaved);
        },
        (error) => {
          console.error('Erreur lors de l\'ajout de l\'image:', error);
        }
      );
    }
  }
}
