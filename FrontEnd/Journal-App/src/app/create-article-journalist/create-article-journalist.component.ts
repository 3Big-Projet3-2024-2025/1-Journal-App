import { Component, OnInit } from '@angular/core';
import { Article } from '../models/article';
import { ArticleService } from '../services/article.service';
import { Image } from '../models/image';
import { ManageNewsletterService } from '../services/manage-newsletter.service';
import { Newsletter } from '../models/newsletter';
import { KeycloakService } from 'keycloak-angular';
import { UsersService } from '../services/users.service';
import { Router } from '@angular/router';

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

  selectedFiles: File[] = [];

  constructor(
    private articleService: ArticleService,
    private newsletterService: ManageNewsletterService,
    private keycloakService: KeycloakService,
    private userService: UsersService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initializeComponent();
  }

  /**
   * Méthode d'initialisation du composant.
   * Récupère l'ID de l'utilisateur, sa localisation et la newsletter qui lui est associée.
   */
  private async initializeComponent(): Promise<void> {
    try {
      await this.getUserId();
      this.getUserLocation();
      this.setUserNewsletter();
    } catch (error) {
      console.error('Erreur lors de l\'initialisation du composant:', error);
    }
  }

  /**
   * Récupère l'ID de l'utilisateur connecté via Keycloak, puis va chercher l'utilisateur associé dans la BDD.
   */
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

  /**
   * Récupère la localisation de l'utilisateur (latitude/longitude).
   */
  getUserLocation(): void {
    if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          this.articleToAdd.latitude = position.coords.latitude;
          this.articleToAdd.longitude = position.coords.longitude;
        },
        (error) => {
          console.error('Erreur de géolocalisation:', error);
        },
        {
          enableHighAccuracy: true,
          timeout: 10000,
          maximumAge: 0
        }
      );
    } else {
      console.error('Géolocalisation non supportée par ce navigateur.');
    }
  }

  /**
   * Détermine la newsletter à laquelle appartient le journaliste.
   * On prend la première newsletter retournée par le service.
   */
  setUserNewsletter(): void {
    if (this.articleToAdd.user_id === 0) {
      console.warn('Impossible de récupérer la newsletter car l\'user_id est manquant.');
      return;
    }

    this.newsletterService.getNewslettersForJournalist(this.articleToAdd.user_id).subscribe(
      (newsletters: Newsletter[]) => {
        if (newsletters && newsletters.length > 0) {
          // On associe la première newsletter trouvée
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

  /**
   * Valide le nombre et le type des fichiers sélectionnés.
   */
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

  /**
   * Convertit un fichier image en base64 (sans préfixe data).
   */
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

  /**
   * Ajoute un article, puis envoie les images si présentes.
   */
  addArticle(): void {
    if (this.articleToAdd.user_id === 0) {
      console.error('User ID missing, impossible de créer l\'article.');
      return;
    }

    if (this.articleToAdd.newsletter_id === 0) {
      console.error('Newsletter ID missing, impossible de créer l\'article.');
      return;
    }

    // Récupère la couleur de fond de la newsletter avant d'ajouter l'article
    this.articleService.getNewsletterBackgroundColor(this.articleToAdd.newsletter_id).subscribe(
      (backgroundColor: string) => {
        this.articleToAdd.backgroundColor = backgroundColor; 
        this.articleService.addArticle(this.articleToAdd).subscribe(
          async (newArticle) => {
            console.log('Article ajouté avec succès:', newArticle);

            if (this.selectedFiles.length > 0) {
              await this.uploadImages(newArticle.articleId);
            }

            // Réinitialisation
            this.resetForm();
            this.router.navigate(['/crud/articles']);
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

  /**
   * Envoie les images sélectionnées au back-end, associé à l'ID de l'article créé.
   */
  async uploadImages(articleId: number): Promise<void> {
    for (const file of this.selectedFiles) {
      const base64Image = await this.convertToBase64(file);
      const imageToAdd: Image = {
        imageId: 0,
        imagePath: base64Image,
        articleId: articleId
      };

      this.articleService.addImage(imageToAdd).subscribe(
        () => {
          // Image ajoutée avec succès
        },
        (error) => {
          console.error('Erreur lors de l\'ajout de l\'image:', error);
        }
      );
    }
  }

  /**
   * Réinitialise le formulaire après création de l'article.
   */
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

}
