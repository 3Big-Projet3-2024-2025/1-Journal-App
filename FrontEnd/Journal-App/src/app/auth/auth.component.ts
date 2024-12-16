import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css']
})
export class AuthComponent {
  isAuthenticated = false;

  constructor(private authService: AuthService) {}

  ngOnInit() {
    // Initialisation de Keycloak
    this.authService.initKeycloak().then(() => {
      this.isAuthenticated = this.authService.isAuthenticated();
      if (this.isAuthenticated) {
        this.authService.getUserProfile().then(profile => {
          console.log('Utilisateur connecté:', profile);
        });
      }
    }).catch(error => {
      console.error('Erreur d\'initialisation de Keycloak:', error);
    });
  }

  login() {
    this.authService.login();
  }

  logout() {
    this.authService.logout();
  }
}
