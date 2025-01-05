import { Component, OnInit } from '@angular/core';
import { Newsletter } from '../models/newsletter';
import { User } from '../models/user';
import { ManageNewsletterService } from '../services/manage-newsletter.service';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UsersService } from '../services/users.service';

@Component({
  selector: 'app-manage-journalist',
  templateUrl: './manage-journalist.component.html',
  styleUrls: ['./manage-journalist.component.css']
})
export class ManageJournalistComponent implements OnInit {

  titretable: string = 'journalists'; 
  newsletterId: number = NaN;
  newsletters: Newsletter[] = []; // Liste des newsletters
  selectedNewsletterId: number | null = null; // Newsletter sélectionnée
  journalists: User[] = [];
  journalistEmailToAdd: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private manageNewsletterService: ManageNewsletterService,
    private authService: AuthService,
    private userService: UsersService
  ) {}
  
  ngOnInit(): void {
    this.loadEditorNewsletters(); 
  }
  
  // Charger les newsletters créées par l'éditeur connecté
  async loadEditorNewsletters(): Promise<void> {
    try {
      const editorEmail = await this.authService.getUserEmail(); // Attendre que l'email soit récupéré
  
      if (!editorEmail) {
        console.error('Email de l\'éditeur non disponible');
        return;
      }
  
      this.manageNewsletterService.getNewslettersByEditorEmail(editorEmail).subscribe(
        (newsletters: Newsletter[]) => {
          this.newsletters = newsletters;
          if (this.newsletters.length > 0) {
            this.selectedNewsletterId = this.newsletters[0].newsletterId; // Sélectionner la première newsletter
            this.loadJournalists(this.selectedNewsletterId); // Charger les journalistes associés
          }
        },
        (error) => {
          console.error('Erreur lors du chargement des newsletters:', error);
        }
      );
    } catch (error) {
      console.error('Erreur lors de la récupération de l\'email de l\'éditeur :', error);
    }
  }
  


  // Charger toutes les newsletters disponibles
  loadNewsletters(): void {
    this.manageNewsletterService.GetALlnewsletter().subscribe(
      (newsletters: Newsletter[]) => {
        this.newsletters = newsletters;
        if (this.newsletters.length > 0) {
          this.selectedNewsletterId = this.newsletters[0].newsletterId; // Sélectionner la première newsletter
          this.loadJournalists(this.selectedNewsletterId); // Charger les journalistes associés
        }
      },
      (error) => {
        console.error('Erreur lors du chargement des newsletters:', error);
      }
    );
  }

  // Charger les journalistes associés à une newsletter donnée
  loadJournalists(newsletterId: number): void {
    this.manageNewsletterService.getnewsletterById(newsletterId).subscribe(
      (newsletter: Newsletter) => {
        this.journalists = newsletter.journalists || [];
      },
      (error) => {
        console.error('Erreur lors du chargement des journalistes:', error);
      }
    );
  }

  addJournalist(): void {
    if (!this.journalistEmailToAdd || !this.selectedNewsletterId) {
      console.error('Email ou newsletter non sélectionnée');
      return;
    }
  
    this.manageNewsletterService.addJournalistToNewsletterByEmail(
      this.selectedNewsletterId,
      this.journalistEmailToAdd
    ).subscribe(
      (updatedNewsletter: Newsletter) => {
        // Recharger les journalistes après ajout
        this.loadJournalists(this.selectedNewsletterId!);
  
        // Change le rôle de l'utilisateur dans la base après l'ajout
        this.userService.getUserByEmail(this.journalistEmailToAdd!).subscribe(
          (user) => {
            const updatedUser = {
              ...user,
              role: {
                roleId: 3, // ID du rôle JOURNALIST
                roleName: 'JOURNALIST',
              },
            };
  
            this.userService.updateUser(user.userId, updatedUser).subscribe(
              () => {
                console.log('Role updated to JOURNALIST for:', user.email);
                alert('Le journaliste a été ajouté et son rôle mis à jour avec succès.');
              },
              (error) => {
                console.error('Erreur lors de la mise à jour du rôle:', error);
              }
            );
          },
          (error) => {
            console.error('Erreur lors de la récupération de l\'utilisateur:', error);
          }
        );
  
        this.journalistEmailToAdd = null; // Réinitialiser l'email
      },
      (error) => {
        console.error('Erreur lors de l\'ajout du journaliste:', error);
      }
    );
  }
  
  

// Supprimer un journaliste de la newsletter
removeJournalist(userId: number): void {
  if (!this.selectedNewsletterId) {
    console.error('Newsletter non sélectionnée');
    return;
  }

  const confirmDelete = confirm('Êtes-vous sûr de vouloir supprimer ce journaliste de la newsletter ?');
  if (confirmDelete) {
    this.manageNewsletterService.removeJournalistFromNewsletter(
      this.selectedNewsletterId,
      userId
    ).subscribe(
      (updatedNewsletter: Newsletter) => {
        // Recharger les journalistes après suppression
        this.loadJournalists(this.selectedNewsletterId!);
        alert('Le journaliste a été supprimé avec succès.');
      },
      (error) => {
        console.error('Erreur lors de la suppression du journaliste:', error);
        alert('Une erreur est survenue lors de la suppression.');
      }
    );
  } else {
    console.log('Suppression annulée par l\'utilisateur.');
  }
}


  // Gérer le changement de newsletter
  onNewsletterChange(): void {
    if (this.selectedNewsletterId) {
      this.loadJournalists(this.selectedNewsletterId);
    }
  }
}
