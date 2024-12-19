import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import * as L from 'leaflet';
import { ArticleService } from '../services/article.service';
import { Article } from '../models/article';
import 'leaflet.markercluster';
import { HttpErrorResponse } from '@angular/common/http';
import { Image } from '../models/image';
import { ImageService } from '../services/image.service';
import { AuthService } from '../services/auth.service';
import { CommentService } from '../services/comment.service';
import { Commentmap } from '../models/commentmap';
import { UsersService } from '../services/users.service';



@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements OnInit, OnChanges {
  isCommentFormVisible = false;
  newCommentContent = '';

  userId: number = 1; 

  @Input() articles: Article[] = []; // Articles à afficher sur la carte
  readArticles: Article[] = [];

  map!: L.Map;
  markersGroup!: L.MarkerClusterGroup;
  isLoading: boolean = true;
  locationError: string | null = null;
  selectedArticle: Article | null = null;
  authMessageVisible: boolean = false; 
  isMapVisible: boolean = true; 
  isDetailsVisible: boolean = false; 

  newsletterTitle: string | null = null;
  authorName: string | null = null;

  images: Image[] = []; // Images associées à l'article sélectionné

  isAuthenticated = false; 
  userInfo: any;
  userRole: string | null = null; 
  private roleHierarchy: string[] = ['ADMIN', 'EDITOR', 'JOURNALIST', 'READER'];
  isCommentsVisible = false; // Pour gérer l'affichage du tableau de commentaires
  comments: Commentmap[] = [];
 

  constructor(private articleService: ArticleService, private imageService: ImageService,private authService: AuthService,private commentService: CommentService, private userService:UsersService) {}

  ngOnInit(): void {
    this.initializeMap();
    

    this.authService.isAuthenticated$.subscribe(authenticated => {
      this.isAuthenticated = authenticated; 
      if (authenticated) {
        this.loadUserInfo();  
        this.loadReadArticles();
      }
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['articles'] && this.map) {
      this.updateMarkers();
    }
  }
  

  initializeMap(): void {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const latitude = position.coords.latitude;
          const longitude = position.coords.longitude;
          this.createMap([latitude, longitude], 13);
          this.addUserMarker([latitude, longitude]);
          this.updateMarkers();
          this.isLoading = false;
        },
        (error) => {
          console.error('Erreur de géolocalisation: ', error);
          this.locationError = 'Impossible de déterminer votre localisation. Affichage par défaut.';
          this.initializeFallbackMap();
          this.updateMarkers();
          this.isLoading = false;
        }
      );
    } else {
      console.error('La géolocalisation n\'est pas supportée par ce navigateur.');
      this.locationError = 'La géolocalisation n\'est pas supportée par votre navigateur. Affichage par défaut.';
      this.initializeFallbackMap();
      this.updateMarkers();
      this.isLoading = false;
    }
  }

  loadReadArticles(): void {
    this.articleService.getReadArticles().subscribe(
      (readArticles) => {
        this.readArticles = readArticles;
        console.log('Articles marqués comme lus:', this.readArticles);
      },
      (error: HttpErrorResponse) => {
        console.error('Erreur lors du chargement des articles marqués comme lus:', error);
      }
    );
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

    this.markersGroup = L.markerClusterGroup();
    this.map.addLayer(this.markersGroup);
  }

  addUserMarker(position: [number, number], popupText: string = 'You are here'): void {
    const userIcon = L.icon({
      iconUrl: 'assets/user.png',
      iconSize: [60, 60],
      iconAnchor: [20, 40],
      popupAnchor: [0, -40]
    });

    L.marker(position, { icon: userIcon }).addTo(this.map)
      .bindPopup(popupText)
      .openPopup();
  }

  updateMarkers(): void {
    if (this.markersGroup) {
      this.markersGroup.clearLayers();
    }

    this.articles.forEach((article) => {
      if (article.latitude && article.longitude) {
        const articlePosition: [number, number] = [article.latitude, article.longitude];

        const articleIcon = L.icon({
          iconUrl: 'assets/article.png',
          iconSize: [55, 55],
          iconAnchor: [17, 45],
          popupAnchor: [0, -40]
        });

        const articleMarker = L.marker(articlePosition, { icon: articleIcon });

        articleMarker.bindPopup(`
          <b>${article.title}</b><br>
          ${this.truncateContent(article.content, 100)}<br>
          <button class="btn-learn-more" data-article-id="${article.articleId}">Learn More</button>
        `);

        this.markersGroup.addLayer(articleMarker);

        articleMarker.on('popupopen', () => {
          const popupElement = document.querySelector('.btn-learn-more');
          if (popupElement) {
            popupElement.addEventListener('click', (event) => {
              const button = event.target as HTMLElement;
              const articleId = button.getAttribute('data-article-id');
              if (articleId) {
                this.getArticleDetails(Number(articleId));
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
        this.isMapVisible = false;
        this.isDetailsVisible = true;
  
        // Récupérer le titre de la newsletter
        this.getNewsletterTitle(articleId);
  
        // Récupérer le nom de l'auteur
        this.getAuthorName(articleId);
  
        // Charger les images associées à cet article
        this.imageService.getImagesByArticleId(articleId).subscribe(
          (images: Image[]) => {
            this.images = images;
          },
          (error: HttpErrorResponse) => {
            console.error('Erreur lors de la récupération des images:', error);
            this.images = [];
          }
        );
  
        // Récupérer l'état de lecture pour l'utilisateur connecté
        this.articleService.getArticleReadStatus(articleId).subscribe(
          (isRead: boolean) => {
            if (this.selectedArticle) {
              this.selectedArticle.read = isRead;
            }
          },
          (error) => {
            console.error('Erreur lors de la récupération de l\'état de lecture:', error);
          }
        );
      },
      (error) => {
        if (error.status === 401) {
          console.error('Erreur 401 : Non authentifié');
          this.authMessageVisible = true;
        } else {
          console.error('Erreur lors de la récupération de l\'article:', error);
        }
      }
    );
  }

  toggleDetails(): void {
    this.isDetailsVisible = !this.isDetailsVisible;
    this.isMapVisible = !this.isDetailsVisible;

    if (this.isMapVisible) {
      this.initializeMap();
    }
  }

  truncateContent(content: string, maxLength: number): string {
    return content.length > maxLength ? content.substring(0, maxLength) + '...' : content;
  }

  getNewsletterTitle(articleId: number): void {
    this.articleService.getNewsletterTitleByArticleId(articleId).subscribe(
      (title) => {
        this.newsletterTitle = title;
      },
      (error) => {
        console.error('Erreur lors de la récupération du titre de la newsletter:', error);
        this.newsletterTitle = 'Unknown';
      }
    );
  }

  getAuthorName(articleId: number): void {
    this.articleService.getAuthorNameByArticleId(articleId).subscribe(
      (name) => {
        this.authorName = name;
      },
      (error) => {
        console.error('Erreur lors de la récupération du nom de l\'auteur:', error);
        this.authorName = 'Unknown';
      }
    );
  }

  markArticleAsRead(articleId: number | undefined): void {
    if (!articleId) return;
    this.articleService.markAsRead(articleId).subscribe(() => {
      if (this.selectedArticle) this.selectedArticle.read = true;
      console.log(`Article ${articleId} marked as read.`);
      this.loadReadArticles();
    });
  }
  
  markArticleAsUnread(articleId: number | undefined): void {
    if (!articleId) return;
    this.articleService.markAsUnread(articleId).subscribe(() => {
      if (this.selectedArticle) this.selectedArticle.read = false;
      console.log(`Article ${articleId} marked as unread.`);
      this.loadReadArticles();
    });
  }
  

  loadUserInfo(): void {
    this.authService.getUserProfile().then(profile => {
      this.userInfo = profile;  // Stocker les informations dans la variable userInfo
      this.setUserRole();  // Déterminer et afficher le rôle principal
    }).catch(error => {
      console.error('Erreur lors du chargement des informations utilisateur', error);
    });
  }

  private setUserRole(): void {
    const roles = this.authService.getRoles();  // Récupérer les rôles de l'utilisateur
    for (let role of this.roleHierarchy) {
      if (roles.includes(role)) {
        this.userRole = role;  // Assigner le rôle principal
        break;
      }
    }
  }
  addCommet() {
    this.isCommentFormVisible = true; // affiche le formulaire
  }

  submitComment() {
    if (!this.selectedArticle?.articleId) {
      console.error("No article selected");
      return;
    }

    const commentData = {
      content: this.newCommentContent,
      publicationDate: new Date().toISOString().split('T')[0], // par exemple la date du jour
      user_id: this.userId,
      article_id: this.selectedArticle.articleId
    };

    this.commentService.addComment(commentData).subscribe({
      next: (createdComment) => {
        console.log("Comment created:", createdComment);
        this.isCommentFormVisible = false;
        this.newCommentContent = '';
        // Vous pouvez ici recharger la liste des commentaires si nécessaire
      },
      error: (err) => {
        console.error("Error creating comment:", err);
      }
    });
  }

  
  toggleComments() {
    this.isCommentsVisible = !this.isCommentsVisible;
    if (this.isCommentsVisible) {
      this.loadComments();
    }
  }

  loadComments() {
    this.commentService.getAllCommentsmap().subscribe({
      next: (comments) => {
        this.comments = comments;
  
        // Pour chaque commentaire, faire un appel pour récupérer l'utilisateur associé
        this.comments.forEach(comment => {
          this.userService.getUserById(comment.user_id).subscribe({
            next: (user) => {
              comment.user = user; // On stocke l'utilisateur directement dans l'objet commentaire
            },
            error: (err) => {
              console.error("Error loading user for comment:", err);
            }
          });
        });
      },
      error: (err) => {
        console.error("Error loading comments:", err);
      }
    });
  }
  
  
}
