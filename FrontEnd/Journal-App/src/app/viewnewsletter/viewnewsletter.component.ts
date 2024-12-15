import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Newsletter } from '../models/newsletter';  // L'interface Newsletter
import { ManageNewsletterService } from '../services/manage-newsletter.service';

@Component({
  selector: 'app-viewnewsletter',
  templateUrl: './viewnewsletter.component.html',
  styleUrls: ['./viewnewsletter.component.css']
})
export class ViewnewsletterComponent implements OnInit {

  newsletter: Newsletter | null = null;

  constructor(
    private newsletterService:  ManageNewsletterService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Récupère l'ID de la newsletter à partir de l'URL
    const id =+localStorage.getItem('seeidnewsletter')! ;
    
    // Appel au service pour récupérer la newsletter
    this.newsletterService.getnewsletterById(id).subscribe(
      (data) => {
        this.newsletter = data;
      },
      (error) => {
        console.error('Erreur lors de la récupération de la newsletter:', error);
      }
    );
  }

  // Méthode pour afficher ou personnaliser la page (ajouter des articles, etc.)
  addArticle(): void {
    console.log('Bouton ajouter un article');
    // Vous pouvez rediriger l'utilisateur vers une page d'ajout d'article ou afficher un formulaire
  }
}
