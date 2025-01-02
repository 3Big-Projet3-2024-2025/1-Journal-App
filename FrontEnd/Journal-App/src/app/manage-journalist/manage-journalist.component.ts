import { Component, OnInit } from '@angular/core';
import { Newsletter } from '../models/newsletter';
import { User } from '../models/user';
import { ManageNewsletterService } from '../services/manage-newsletter.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-manage-journalist',
  templateUrl: './manage-journalist.component.html',
  styleUrls: ['./manage-journalist.component.css']
})
export class ManageJournalistComponent implements OnInit {

  titretable: string = 'journalists'; 
  newsletterId:number = NaN;
  journalists: User[] = [];
  journalistIdToAdd: number | null = null;



  constructor(private route: ActivatedRoute, private manageNewsletterService: ManageNewsletterService) {}
  
  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.newsletterId = +params['newsletterId']; // Convertir en nombre
      this.loadJournalists();
    });
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

  journalistEmailToAdd: string | null = null;

  // Ajoute un journaliste à la newsletter
  addJournalist(): void {
    if (!this.journalistEmailToAdd) {
      console.error('No journalist email provided');
      return;
    }
  
    this.manageNewsletterService.addJournalistToNewsletterByEmail(this.newsletterId, this.journalistEmailToAdd).subscribe(
      (updatedNewsletter: Newsletter) => {
        this.journalists = updatedNewsletter.journalists || [];
        this.journalistEmailToAdd = null; // Reset du champ
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
