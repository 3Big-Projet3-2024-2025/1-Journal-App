import { Component } from '@angular/core';
import { Newsletter } from '../models/newsletter';
import { User } from '../models/user';
import { AuthService } from '../services/auth.service';
import { ManageNewsletterService } from '../services/manage-newsletter.service';
import { UsersService } from '../services/users.service';

@Component({
  selector: 'app-manage-newsletter-form',
  templateUrl: './manage-newsletter-form.component.html',
  styleUrl: './manage-newsletter-form.component.css'
})
export class ManageNewsletterFormComponent {

  userInfo: any;
  useridbykey: any;

  constructor(
    private auth: AuthService,
    private manageNewsletterService: ManageNewsletterService,
    private userservice: UsersService
  ) {}

  ngOnInit(): void {
    localStorage.setItem('newsletter', '0');
    this.auth.getUserProfile().then(profile => {
      this.userInfo = profile; // Stocker les informations dans la variable userInfo
      console.log(profile);
      this.getid();
    }).catch(error => {
      console.error('Erreur lors du chargement des informations utilisateur', error);
    });
  }

  formData = {
    title: '',
    subtitle: '',
    content: '',
    publicationDate: '',
  };

  getid() {
    this.userservice.getUserByKeycloakId(this.userInfo.id).subscribe({
      next: (data) => {
        this.useridbykey = data.userId;
      },
      error: (err) => {
        console.error('Erreur lors de la récupération de l\'ID utilisateur', err);
      }
    });
  }

  onSubmit() {
    const { title, subtitle, content, publicationDate } = this.formData;

    // Validation des champs
    if (!title || title.trim().length < 2) {
      alert('Title is required and must be at least 2 characters.');
      return;
    }

    if (!subtitle || subtitle.trim().length < 2) {
      alert('Subtitle is required and must be at least 2 characters.');
      return;
    }

    if (!publicationDate) {
      alert('Publication date is required.');
      return;
    }

    // Création de l'objet newsletter
    const newsletter: Newsletter = {
      newsletterId: 0,
      title: title,
      subtitle: subtitle,
      publicationDate: publicationDate,
      creator: this.useridbykey,
      backgroundColor: '',
      font: '',
      articles: []
    };

    // Appel au service pour ajouter une newsletter
    this.manageNewsletterService.Addnewsletter(newsletter).subscribe({
      next: (value) => {
        console.log('Newsletter ajoutée avec succès', value);
      },
      error: (err) => {
        console.error('Erreur lors de l\'ajout de la newsletter', err);
      }
    });
  }

  updateNewsletter(newsletterId: number) {
    const updatedNewsletter: Newsletter = {
      newsletterId: newsletterId,
      title: this.formData.title,
      subtitle: this.formData.subtitle,
      publicationDate: this.formData.publicationDate,
      creator: this.useridbykey,
      backgroundColor: '',
      font: '',
      articles: []
    };

    this.manageNewsletterService.Updatenewsletter(newsletterId, updatedNewsletter).subscribe({
      next: (value) => {
        console.log('Newsletter mise à jour avec succès', value);
      },
      error: (err) => {
        console.error('Erreur lors de la mise à jour de la newsletter', err);
      }
    });
  }

  deleteNewsletter(newsletterId: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette newsletter ?')) {
      this.manageNewsletterService.deletenewsletter(newsletterId).subscribe({
        next: () => {
          console.log('Newsletter supprimée avec succès');
        },
        error: (err) => {
          console.error('Erreur lors de la suppression de la newsletter', err);
        }
      });
    }
  }
}
