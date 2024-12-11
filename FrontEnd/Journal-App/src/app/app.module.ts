import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS, HttpClient } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { NavbarComponent } from './navbar/navbar.component';
import { FooterComponent } from './footer/footer.component';
import { AuthComponent } from './auth/auth.component';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from './services/auth.service';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { MapComponent } from './map/map.component';
import { ManageArticleComponent } from './manage-article/manage-article.component';
import { ManageNewsletterComponent } from './manage-newsletter/manage-newsletter.component';
import { ListCrudComponent } from './list-crud/list-crud.component';
import { ManageNewsletterFormComponent } from './manage-newsletter-form/manage-newsletter-form.component';
import { ManageRgpdFormComponent } from './manage-rgpd-form/manage-rgpd-form.component';

// IMPORT DE L'INTERCEPTOR
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    FooterComponent,
    HomeComponent,
    AuthComponent,
    MapComponent,
    ManageArticleComponent,
    ManageNewsletterComponent,
    ListCrudComponent,
    ManageNewsletterFormComponent,
    ManageRgpdFormComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RouterOutlet,
    RouterLink,
    KeycloakAngularModule,
    HttpClientModule,
    FormsModule 
  ],
  providers: [
    AuthService,
    KeycloakService,
    HttpClient,
    // Déclaration de l'intercepteur HTTP
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
