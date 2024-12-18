import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

@Injectable({
  providedIn: 'root',
})
export class RoleGuard implements CanActivate {
  constructor(private keycloakService: KeycloakService, private router: Router) {}

  async canActivate(route: ActivatedRouteSnapshot): Promise<boolean> {
    const expectedRoles: string[] = route.data['roles']; // Récupère les rôles attendus de la route
    const isLoggedIn = await this.keycloakService.isLoggedIn();

    if (isLoggedIn) {
      const userRoles = this.keycloakService.getUserRoles();

      // Vérifie si l'utilisateur est ADMIN ou a un des rôles attendus
      if (userRoles.includes('ADMIN') || expectedRoles.some(role => userRoles.includes(role))) {
        return true;
      }
    }

    // Rediriger si l'utilisateur n'a pas les permissions
    this.router.navigate(['/home']);
    return false;
  }
}
