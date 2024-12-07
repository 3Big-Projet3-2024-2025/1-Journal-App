import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { ManageArticleComponent } from './manage-article/manage-article.component';
import { ManageNewsletterComponent } from './manage-newsletter/manage-newsletter.component';


const routes: Routes = [
  {
    path: '', // Route par défaut
    redirectTo: '/home', // Rediriger vers la route 'home'
    pathMatch: 'full' // Assurez-vous que la redirection se fait quand l'URL est vide
  },
  {
    path: 'home', component: HomeComponent
  },
  {
    path: 'manage-articles', component: ManageArticleComponent
  },
  {
    path: 'manage-newsletters', component: ManageNewsletterComponent
  }
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
