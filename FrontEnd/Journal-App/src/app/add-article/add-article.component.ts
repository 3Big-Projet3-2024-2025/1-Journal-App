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
          this.createFormMap([latitude, longitude], 13); // User position
        },
        () => {
          this.createFormMap([charleroiLat, charleroiLng], 13); // Default position: Charleroi
        }
      );
    } else {
      this.createFormMap([charleroiLat, charleroiLng], 13); // // Default position: Charleroi
    }
  }

  createFormMap(center: [number, number], zoom: number): void {
    this.map = L.map('map').setView(center, zoom);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.map);

// Add a draggable marker with the custom icon
    this.marker = L.marker(center, { icon: this.customIcon, draggable: true }).addTo(this.map);

    this.marker.on('dragend', () => {
      const position = this.marker.getLatLng();
      this.articleToAdd.latitude = position.lat;
      this.articleToAdd.longitude = position.lng;
    });

    this.map.on('click', (e: L.LeafletMouseEvent) => {
      const position = e.latlng;
      this.marker.setLatLng(position); // Move the marker
      this.articleToAdd.latitude = position.lat;
      this.articleToAdd.longitude = position.lng;
    });

    this.articleToAdd.latitude = center[0];
    this.articleToAdd.longitude = center[1];
  }

  

   // Retrieve the ID of the connected user via Keycloak
  getUserId(): Promise<void> {
    return new Promise((resolve, reject) => {
      const keycloakId = this.keycloakService.getKeycloakInstance().tokenParsed?.sub;
      if (keycloakId) {
        this.userService.getUserByKeycloakId(keycloakId).subscribe(
          (user) => {
            // Assign the user's ID to the `user_id` of the article
            this.articleToAdd.user_id = user.userId;
            //console.log('Connected user:', user);
            resolve();
          },
          (error) => {
            console.error('Error retrieving the user:', error);
            reject(error);
          }
        );
      } else {
        reject('Keycloak ID not found');
      }
    });
  }

 

  // Load available newsletters
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
        console.error('Error loading newsletters:', error);
      }
    );
  }

 // Validate the number and type of files
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

  // Convert image to base64
  convertToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => {
        const base64String = (reader.result as string).split(',')[1]; // Remove the "data:image/...;base64," prefix
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
      console.error('The newsletter ID is missing');
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
            console.error('The newsletter ID is missing', error);
          }
        );
      },
      (error) => {
        console.error('Error retrieving the newsletter background color:', error);
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
        imageId: 0, // Default value for image ID
        imagePath: base64Image, // Base64 encoded content (without prefix)
        articleId: articleId    // Article ID
      };
  
      this.articleService.addImage(imageToAdd).subscribe(
        (imageSaved) => {
          
        },
        (error) => {
          console.error('Error adding the image:', error);
        }
      );
    }
  }
}
