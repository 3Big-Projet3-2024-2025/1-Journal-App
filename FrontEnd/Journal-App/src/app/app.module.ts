import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { NavbarComponent } from './navbar/navbar.component';
import { FooterComponent } from './footer/footer.component';
import { AuthComponent } from './auth/auth.component';
import { HttpClient } from '@angular/common/http';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from './services/auth.service';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { MapComponent } from './map/map.component';
import { ManageArticleComponent } from './manage-article/manage-article.component';
import { ManageNewsletterComponent } from './manage-newsletter/manage-newsletter.component';
import { ListCrudComponent } from './list-crud/list-crud.component';

@NgModule({
  declarations: [// component
    AppComponent,
    NavbarComponent,
    FooterComponent,
    HomeComponent,
    AuthComponent,
    MapComponent,
    ManageArticleComponent,
    ManageNewsletterComponent,
    ListCrudComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RouterOutlet,
    RouterLink,
    KeycloakAngularModule,
    HttpClientModule,
    
    
  ],
  providers: [ // servies
    AuthService,
   KeycloakService,
   HttpClient
    
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
