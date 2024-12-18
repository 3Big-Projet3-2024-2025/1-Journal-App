import { Component, OnInit } from '@angular/core';
import { Newsletter } from '../models/newsletter';
import { User } from '../models/user';
import { ManageNewsletterService } from '../services/manage-newsletter.service';

@Component({
  selector: 'app-manage-journalist',
  templateUrl: './manage-journalist.component.html',
  styleUrls: ['./manage-journalist.component.css']
})
export class ManageJournalistComponent implements OnInit {

  titretable: string = 'journalists'; 
  newsletterId: number = 1; // L'ID de la newsletter à gérer (à adapter selon votre logique)
  journalists: User[] = [];
  journalistIdToAdd: number | null = null;

  constructor(private manageNewsletterService: ManageNewsletterService) {}

  ngOnInit(): void {
    this.loadJournalists();
  }

  // Charge la liste des journalistes pour la newsletter donnée
  loadJournalists(): void {
    this.manageNewsletterService.getnewsletterById(this.newsletterId).subscribe(
      (newsletter: Newsletter) => {
        this.journalists = newsletter.journalists || [];
      },
      (error) => {
        console.error('Error loading newsletter:', error);
      }
    );
  }

  // Ajoute un journaliste à la newsletter
  addJournalist(): void {
    if (!this.journalistIdToAdd) {
      console.error('No journalist ID provided');
      return;
    }

    this.manageNewsletterService.addJournalistToNewsletter(this.newsletterId, this.journalistIdToAdd).subscribe(
      (updatedNewsletter: Newsletter) => {
        this.journalists = updatedNewsletter.journalists || [];
        this.journalistIdToAdd = null; // Reset du champ
      },
      (error) => {
        console.error('Error adding journalist:', error);
      }
    );
  }

  // Supprime un journaliste de la newsletter
  removeJournalist(userId: number): void {
    this.manageNewsletterService.removeJournalistFromNewsletter(this.newsletterId, userId).subscribe(
      (updatedNewsletter: Newsletter) => {
        this.journalists = updatedNewsletter.journalists || [];
      },
      (error) => {
        console.error('Error removing journalist:', error);
      }
    );
  }

}
