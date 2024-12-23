import { Component } from '@angular/core';
import { AuthService } from './services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'Journal-App';

  constructor(private authService: AuthService, private router: Router) {}


  ngOnInit() {
    this.authService.initKeycloak().then(() => {
      // L'authentification est prête, l'état est mis à jour
     // console.log("Keycloak initialisé et état de l'utilisateur vérifié");
    }).catch(err => {
      console.error("Erreur lors de l'initialisation de Keycloak", err);
    });
  }}
