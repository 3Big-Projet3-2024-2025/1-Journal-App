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
import { ManageNewsletterComponent } from './manage-newsletter/manage-newsletter.component';
import { ListCrudComponent } from './list-crud/list-crud.component';
import { ManageNewsletterFormComponent } from './manage-newsletter-form/manage-newsletter-form.component';
import { ManageRgpdFormComponent } from './manage-rgpd-form/manage-rgpd-form.component';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatCardModule } from '@angular/material/card';

// IMPORT DE L'INTERCEPTOR
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { FormsModule } from '@angular/forms';
import { AddArticleComponent } from './add-article/add-article.component';
import { UpdateArticleComponent } from './update-article/update-article.component';

import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { ViewnewsletterComponent } from './viewnewsletter/viewnewsletter.component';
import { CreateArticleJournalistComponent } from './create-article-journalist/create-article-journalist.component';
import { ManageJournalistComponent } from './manage-journalist/manage-journalist.component';
import { ViewMyArticlesComponent } from './view-my-articles/view-my-articles.component';
import { MyReadArticlesComponent } from './my-read-articles/my-read-articles.component';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    FooterComponent,
    HomeComponent,
    AuthComponent,
    MapComponent,
    ManageNewsletterComponent,
    ListCrudComponent,
    ManageNewsletterFormComponent,
    ManageRgpdFormComponent,
    AddArticleComponent,
    UpdateArticleComponent,
    ViewnewsletterComponent,
    CreateArticleJournalistComponent,
    ManageJournalistComponent,
    ViewMyArticlesComponent,
    MyReadArticlesComponent,
    
   

  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RouterOutlet,
    RouterLink,
    KeycloakAngularModule,
    HttpClientModule,
    FormsModule ,
    MatFormFieldModule, // Ajouter MatFormFieldModule
    MatInputModule, // Ajouter MatInputModule
    MatSelectModule, // Ajouter MatSelectModule si vous utilisez mat-select
    MatButtonModule, // Ajouter MatButtonModule si vous utilisez des boutons Material
    MatIconModule, // Ajouter MatIconModule si vous utilisez des icônes Material
    MatToolbarModule, // Ajouter MatToolbarModule si vous utilisez des toolbars Material
    MatCardModule,
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
    },
    provideAnimationsAsync()
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
