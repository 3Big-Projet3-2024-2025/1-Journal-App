import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { ListCrudComponent } from './list-crud/list-crud.component';
import { ManageNewsletterComponent } from './manage-newsletter/manage-newsletter.component';
import { ManageNewsletterFormComponent } from './manage-newsletter-form/manage-newsletter-form.component';
import { AddArticleComponent } from './add-article/add-article.component';
import { UpdateArticleComponent } from './update-article/update-article.component';
import { ViewnewsletterComponent } from './viewnewsletter/viewnewsletter.component';
import { RoleGuard } from './guards/role.guard';
import { CreateArticleJournalistComponent } from './create-article-journalist/create-article-journalist.component';
import { ManageJournalistComponent } from './manage-journalist/manage-journalist.component';

const routes: Routes = [
  // Route par défaut
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },

  {
    path: 'managejournalist',
    component: ManageJournalistComponent,
    canActivate: [RoleGuard],
    data: { roles: ["ADMIN","EDITOR"] },
  },
  {
    path: 'createarticlejournalist',
    component: CreateArticleJournalistComponent,
    canActivate: [RoleGuard],
    data: { roles: ["ADMIN","EDITOR","JOURNALIST"] },
  },

  {
    path: 'crud/article',
    component: ListCrudComponent,
    canActivate: [RoleGuard],
    data: { roles: ["ADMIN","EDITOR"] },
  },
  
  {
    path: 'update-newsletter',
    component: ManageNewsletterFormComponent,
    canActivate: [RoleGuard],
    data: { roles: ["ADMIN","EDITOR"] },

  },

  {
    path: 'update-newsletter/:id',
    component: ManageNewsletterFormComponent,
    canActivate: [RoleGuard],
    data: { roles: ["ADMIN","EDITOR"] },

  },
  {
    path: 'crud/newsletter',
    component: ListCrudComponent,
    canActivate: [RoleGuard],
    data: { roles: ['ADMIN',"EDITOR"] },
  },

  {
    path: 'crud/newsletter/:id',
    component: ListCrudComponent,
    canActivate: [RoleGuard],
    data: { roles: ['ADMIN',"EDITOR"] },
  },

  {
    path: 'manage-newsletters',
    component: ManageNewsletterComponent,
    canActivate: [RoleGuard],
    data: { roles: ['ADMIN', 'EDITOR'] },
  },
  {
    path: 'manage-newsletter-form',
    component: ManageNewsletterFormComponent,
    canActivate: [RoleGuard],
    data: { roles: ['ADMIN', 'EDITOR'] },
  },

  // Routes JOURNALIST
  {
    path: 'add-article',
    component: AddArticleComponent,
    canActivate: [RoleGuard],
    data: { roles: ['ADMIN', 'JOURNALIST',"EDITOR"] },
  },
  {
    path: 'update-article',
    component: UpdateArticleComponent,
    canActivate: [RoleGuard],
    data: { roles: ['ADMIN', 'JOURNALIST'] },
  },
  { 
    path: 'update-article/:id', 
    component: UpdateArticleComponent,
    canActivate: [RoleGuard],
    data: { roles: ['ADMIN', 'JOURNALIST'] },
  },

  // Routes partagées
  {
    path: 'see-newsletter',
    component: ViewnewsletterComponent,
    canActivate: [RoleGuard],
    data: { roles: ['ADMIN', 'EDITOR', 'JOURNALIST'] },
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}