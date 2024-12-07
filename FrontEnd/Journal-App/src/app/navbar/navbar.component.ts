import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { KeycloakProfile } from 'keycloak-js';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {

  userInfo: KeycloakProfile | null = null;  // Stocker les informations utilisateur
  isAuthenticated$: any;
  detailsVisible = false;



  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    // Initialisez isAuthenticated$ dans ngOnInit pour éviter l'accès avant l'injection
    this.isAuthenticated$ = this.authService.isAuthenticated$;

    if (this.authService.isAuthenticated()) {
      this.loadUserInfo();  // Charger les informations utilisateur si authentifié
    }
  }

  login() {
    this.authService.login();
  }

  logout() {
    this.authService.logout();
  }

 

   // Afficher les informations utilisateur (peut être utilisée dans un modal, un panneau, etc.)
   showUserInfo():void {
    this.detailsVisible = !this.detailsVisible;  // Inverser l'état de visibility des détails
    if (this.detailsVisible && !this.userInfo) {
      this.loadUserInfo();  // Charger les informations si elles ne sont pas déjà chargées
    }
  }

  
  loadUserInfo(): void {
    this.authService.getUserProfile().then(profile => {
      this.userInfo = profile;  // Stocker les informations dans la variable userInfo
    }).catch(error => {
      console.error('Erreur lors du chargement des informations utilisateur', error);
    });
  }



}
