import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

@Injectable({
  providedIn: 'root',
})
export class RoleGuard implements CanActivate {
  constructor(private keycloakService: KeycloakService, private router: Router) {}

  async canActivate(route: ActivatedRouteSnapshot): Promise<boolean> {
    const expectedRoles: string[] = route.data['roles']; // Retrieve the expected roles from the route
    const isLoggedIn = await this.keycloakService.isLoggedIn();

    if (isLoggedIn) {
      const userRoles = this.keycloakService.getUserRoles();

   // Check if the user is ADMIN or has one of the expected roles
      if (userRoles.includes('ADMIN') || expectedRoles.some(role => userRoles.includes(role))) {
        return true;
      }
    }

    // Redirect if the user does not have permissions
    this.router.navigate(['/home']);
    return false;
  }
}
