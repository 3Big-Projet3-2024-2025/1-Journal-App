import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable, from, of } from 'rxjs';
import { KeycloakService } from 'keycloak-angular';
import { mergeMap } from 'rxjs/operators';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private keycloakService: KeycloakService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Si isLoggedIn() retourne un boolean directement, on utilise of(...)
    return of(this.keycloakService.isLoggedIn()).pipe(
      mergeMap((loggedIn: boolean) => {
        if (loggedIn) {
          // getToken() retourne un Promise<string> donc on peut utiliser from()
          return from(this.keycloakService.getToken()).pipe(
            mergeMap((token: string) => {
              if (token) {
                const authReq = request.clone({
                  setHeaders: {
                    Authorization: `Bearer ${token}`
                  }
                });
                return next.handle(authReq);
              } else {
                return next.handle(request);
              }
            })
          );
        } else {
          return next.handle(request);
        }
      })
    );
  }
}
