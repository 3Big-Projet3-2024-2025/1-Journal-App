import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { ManageNewsletterComponent } from './manage-newsletter/manage-newsletter.component';
import { ManageNewsletterFormComponent } from './manage-newsletter-form/manage-newsletter-form.component';
import { ListCrudComponent } from './list-crud/list-crud.component';
import { AddArticleComponent } from './add-article/add-article.component';
import { UpdateArticleComponent } from './update-article/update-article.component';
import { ViewnewsletterComponent } from './viewnewsletter/viewnewsletter.component';


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
    path:'crud/:type',component:ListCrudComponent
  },
  {
    path: 'manage-newsletters', component: ManageNewsletterComponent
  },
  {
    path: 'manage-newsletter-form', component:ManageNewsletterFormComponent
  },
  {
    path: 'add-article', component:AddArticleComponent
  },
  {
    path: 'update-article', component:UpdateArticleComponent
  },
  { path: 'update-article/:id', component: UpdateArticleComponent },
  {
    path:'see-newsletter',component: ViewnewsletterComponent
  },
  { path: 'update-newsletter', component: ManageNewsletterFormComponent },

];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
