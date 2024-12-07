import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular'; // Importer KeycloakService
import { KeycloakProfile } from 'keycloak-js'; // Importer KeycloakProfile pour les informations utilisateur
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private isAuthenticatedSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private keycloakService: KeycloakService) {}

  

  initKeycloak() {
    return new Promise<void>((resolve, reject) => {
      this.keycloakService.init({
        config: {
          url: 'http://localhost:8082/',
          realm: 'journalapp',
          clientId: 'journalapp-frontend',
        },
        initOptions: {
          onLoad: 'check-sso', // Permet de vérifier si l'utilisateur est déjà authentifié
          checkLoginIframe: false
        }
      }).then(() => {
        this.isAuthenticatedSubject.next(this.keycloakService.isLoggedIn()); // Mettez à jour l'état de l'authentification
        resolve();
      }).catch(err => {
        this.isAuthenticatedSubject.next(false);
        reject(err);
      });
    });
  }

  // Vérifier si l'utilisateur est authentifié
  isAuthenticated(): boolean {
    return this.keycloakService.isLoggedIn();
  }

  // Connexion de l'utilisateur
  login(): void {
    if (this.keycloakService) {
      this.keycloakService.login(); // Appelle Keycloak pour l'authentification
    } else {
      console.error('KeycloakService n\'est pas initialisé');
    }
  }

   // Déconnexion de l'utilisateur
   logout(): void {
    this.keycloakService.logout('http://localhost:4200/home');
  }

  // Récupérer les informations de l'utilisateur connecté
  getUserProfile(): Promise<KeycloakProfile> {
    return this.keycloakService.loadUserProfile();
  }
  

  // Récupérer le token d'accès (JWT)
  getToken(): Promise<string> {
    return this.keycloakService.getToken();
  }

  // Rafraîchir le token si nécessaire
  updateToken(): Promise<boolean> {
    return this.keycloakService.updateToken();
  }
}
