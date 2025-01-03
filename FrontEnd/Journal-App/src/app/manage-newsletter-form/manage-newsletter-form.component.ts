import { Component, OnInit } from '@angular/core';
import { Newsletter } from '../models/newsletter';
import { AuthService } from '../services/auth.service';
import { ManageNewsletterService } from '../services/manage-newsletter.service';
import { UsersService } from '../services/users.service';
import { Router } from '@angular/router';
import { User } from '../models/user';

@Component({
  selector: 'app-manage-newsletter-form',
  templateUrl: './manage-newsletter-form.component.html',
  styleUrls: ['./manage-newsletter-form.component.css']
})
export class ManageNewsletterFormComponent implements OnInit {
  userInfo: any = null;
  useridbykey: number | null = null;
  newsletterId: number | null = null;  // ID de la newsletter (si modif)
  activeTab: string = 'title-settings';
  isEditing: boolean = false;

  journalistIdToAdd: number | null = null; // champ pour ajouter un journaliste par son ID

  formData = {
    title: '',
    subtitle: '',
    backgroundColor: '#ffffff',
    titleFont: 'Arial',
    titleFontSize: 24,
    titleColor: '#000000',
    titleBold: false,
    titleUnderline: false,
    subtitleFont: 'Arial',
    subtitleFontSize: 18,
    subtitleColor: '#000000',
    subtitleBold: false,
    subtitleItalic: false,
    textAlign: '',
    // On stocke ici les journalistes sous forme de tableau d'objets User
    journalists: [] as User[]
  };
  successMessage: string | undefined;

  constructor(
    private auth: AuthService,
    private manageNewsletterService: ManageNewsletterService,
    private userservice: UsersService,
    private router: Router
  ) {}

  ngOnInit(): void {
    
    // Récupérer l'ID de la newsletter à partir de localStorage
    this.newsletterId = +localStorage.getItem('idnewsletter')!;
    this.isEditing = !!this.newsletterId;
    // Si un ID est trouvé dans localStorage, récupérer la newsletter correspondante
    if (this.isEditing) {
      this.manageNewsletterService.getnewsletterById(this.newsletterId).subscribe(
        (newsletter) => {
          // Remplir les champs du formulaire avec les données récupérées
          this.formData.title = newsletter.title;
          this.formData.subtitle = newsletter.subtitle;
          this.formData.backgroundColor = newsletter.backgroundColor || '';
          this.formData.titleFont = newsletter.titleFont || '';
          this.formData.titleFontSize = newsletter.titleFontSize || 0;
          this.formData.titleColor = newsletter.titleColor || '';
          this.formData.titleBold = newsletter.titleBold || false;
          this.formData.titleUnderline = newsletter.titleUnderline || false;
          this.formData.subtitleFont = newsletter.subtitleFont || '';
          this.formData.subtitleFontSize = newsletter.subtitleFontSize || 0;
          this.formData.subtitleColor = newsletter.subtitleColor || '';
          this.formData.subtitleBold = newsletter.subtitleBold || false;
          this.formData.subtitleItalic = newsletter.subtitleItalic || false;
          this.formData.textAlign = newsletter.textAlign || '';
          this.formData.journalists = newsletter.journalists || [];
        },
        (error) => {
          console.error('Erreur lors de la récupération de la newsletter:', error);
        }
      );
    } else {
      // Réinitialiser le formulaire pour le mode création
      localStorage.removeItem('idnewsletter'); // Supprimer l'ID de `localStorage`
      this.resetForm();
    }
  
    

    // Récupérer les informations de l'utilisateur
    this.auth.getUserProfile().then(profile => {
      this.userInfo = profile || {};  // Assurez-vous que userInfo soit toujours défini
      if (this.userInfo.id) {
        
        this.userservice.getUserByKeycloakId(this.userInfo.id).subscribe({
          next: (data) => {
            this.useridbykey = data.userId;
           
          },
          error: (err) => {
            console.error('Error while retrieving user ID', err);
          }
        });
      } else {
        console.error('User info or ID is missing');
      }
    }).catch(error => {
      console.error('Error while loading user information', error);
    })
  }

  onSubmit(): void {
    if (!this.isFormValid()) {
        return;
    }

    const newsletter: Newsletter = {
      newsletterId: this.newsletterId || 0,
      title: this.formData.title,
      subtitle: this.formData.subtitle,
      publicationDate: new Date(),
      creator: this.useridbykey || 0, // ID de l'utilisateur
      backgroundColor: this.formData.backgroundColor,
      titleFont: this.formData.titleFont,
      titleFontSize: this.formData.titleFontSize,
      titleColor: this.formData.titleColor,
      titleBold: this.formData.titleBold,
      titleUnderline: this.formData.titleUnderline,
      subtitleFont: this.formData.subtitleFont,
      subtitleFontSize: this.formData.subtitleFontSize,
      subtitleColor: this.formData.subtitleColor,
      subtitleBold: this.formData.subtitleBold,
      subtitleItalic: this.formData.subtitleItalic,
      textAlign: this.formData.textAlign,
      journalists: this.formData.journalists
  };
  
  
    if (this.newsletterId) {
        // Mise à jour de la newsletter
        this.manageNewsletterService.Updatenewsletter(this.newsletterId, newsletter).subscribe({
            next: (value) => {
                console.log('Newsletter successfully updated', value);
                this.successMessage = "Newsletter successfully updated!";
                setTimeout(() => {
                    this.router.navigate(['crud/article']);
                }, 2000);
            },
            error: (err) => {
                console.error('Error while updating the newsletter', err);
                alert('Error while updating the newsletter.');
            }
        });
    } else {
        // Création d'une nouvelle newsletter
        this.manageNewsletterService.Addnewsletter(newsletter).subscribe({
            next: (newNewsletter) => {
                console.log('Newsletter successfully added', newNewsletter);
                this.addCreatorAsJournalist(newNewsletter.newsletterId);
                this.successMessage = "Newsletter successfully created";
                setTimeout(() => {
                    this.router.navigate(['crud/article']);
                }, 2000);
            },
            error: (err) => {
                console.error('Error while adding the newsletter', err);
                alert('Error while adding the newsletter.');
            }
        });
    }
}

private addCreatorAsJournalist(newsletterId: number): void {
  if (!this.useridbykey) {
      console.error('User ID (creator) is missing');
      return;
  }

  this.manageNewsletterService.addJournalistToNewsletter(newsletterId, this.useridbykey).subscribe({
      next: () => {
          console.log(`Creator (User ID: ${this.useridbykey}) added as journalist to newsletter (ID: ${newsletterId})`);
      },
      error: (err) => {
          console.error('Error while adding the creator as journalist:', err);
      }
  });
}


  deleteNewsletter(newsletterId: number): void {
    if (confirm('Are you sure you want to delete this newsletter?')) {
      this.manageNewsletterService.deletenewsletter(newsletterId).subscribe({
        next: () => {
          console.log('Newsletter successfully deleted');
          alert('Newsletter successfully deleted.');
        },
        error: (err) => {
          console.error('Error while deleting the newsletter', err);
          alert('Error while deleting the newsletter.');
        }
      });
    }
  }

  private isFormValid(): boolean {
    const { title, subtitle } = this.formData;

    if (!title || title.trim().length < 2) {
      alert('Title is required and must be at least 2 characters.');
      return false;
    }

    if (!subtitle || subtitle.trim().length < 2) {
      alert('Subtitle is required and must be at least 2 characters.');
      return false;
    }

    return true;
  }

  goBack(): void {
    // Redirige vers /crud/newsletter et réinitialise les données du localStorage
    localStorage.setItem('put', '');
    localStorage.removeItem('idnewsletter'); 
    this.router.navigate(['crud/newsletter']);
  }

  // Ajout d'un journaliste dans la liste du formulaire (sans appel direct à l'API)
  addJournalistToForm(): void {
    if (!this.journalistIdToAdd) {
      alert('Please provide a valid journalist ID.');
      return;
    }

    // On crée un objet "User" minimaliste avec uniquement l'ID
    const newJournalist: User = {
      userId: this.journalistIdToAdd,
      firstName: '',
      lastName: '',
      keycloakId: '',
      role: {
        roleId: 0,
        roleName: ''
      },
      gdprRequests:[]
    };
    

    // On ajoute ce journaliste à la liste
    this.formData.journalists.push(newJournalist);
    this.journalistIdToAdd = null;
  }

  // Supprime un journaliste de la liste du formulaire
  removeJournalistFromForm(userId: number): void {
    this.formData.journalists = this.formData.journalists.filter(j => j.userId !== userId);
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }
  private resetForm(): void {
    this.formData = {
      title: '',
      subtitle: '',
      backgroundColor: '#ffffff',
      titleFont: 'Arial',
      titleFontSize: 24,
      titleColor: '#000000',
      titleBold: false,
      titleUnderline: false,
      subtitleFont: 'Arial',
      subtitleFontSize: 18,
      subtitleColor: '#000000',
      subtitleBold: false,
      subtitleItalic: false,
      textAlign: '',
      journalists: this.formData.journalists
      
    };
  }
  

}

