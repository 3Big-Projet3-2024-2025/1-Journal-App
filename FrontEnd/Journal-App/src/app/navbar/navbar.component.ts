import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  userInfo: any; // Stocker les informations utilisateur
  isAuthenticated = false;  // Variable pour stocker l'état d'authentification
  userRole: string | null = null;  // Stocker le rôle principal
  detailsVisible = false;

  private roleHierarchy: string[] = ['ADMIN', 'EDITOR', 'JOURNALIST', 'READER'];

  constructor(private authService: AuthService,private cook:CookieService) {}

  ngOnInit(): void {
    this.authService.isAuthenticated$.subscribe(authenticated => {
      this.isAuthenticated = authenticated; // Mettre à jour l'état d'authentification
      //localStorage.setItem('newsletter', '0');
      //console.log('Newsletter variable set to 0 in localStorage');
      if (authenticated) {
        this.loadUserInfo();  // Charger les informations utilisateur si authentifié
      }
    });
  }
  setNewsletterInLocalStorage() {
    if(localStorage.getItem('newsletter') !== '0'){
      window.location.reload()
    }
    localStorage.setItem("put","")
    localStorage.setItem('newsletter', '1');
    console.log('Newsletter variable set to 1 in localStorage');
    
    
  }
  setArticleInLocalStorage() {
    if(localStorage.getItem('newsletter') !== '0'){
      window.location.reload()
    }
    localStorage.setItem('newsletter', '2');
    console.log('Article variable set to 1 in localStorage');
    
  }

  login() {
    this.authService.login();
  }

  logout() {
    this.authService.logout();
  }

  showUserInfo(): void {
    this.detailsVisible = !this.detailsVisible;  // Inverser l'état de visibilité des détails
    if (this.detailsVisible && !this.userInfo) {
      this.loadUserInfo();  // Charger les informations si elles ne sont pas déjà chargées
    }
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
}
