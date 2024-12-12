import { Component } from '@angular/core';
import { Newsletter } from '../models/newsletter';
import { User } from '../models/user';
import { AuthService } from '../services/auth.service';
import { ManageNewsletterService } from '../services/manage-newsletter.service';

@Component({
  selector: 'app-manage-newsletter-form',
  templateUrl: './manage-newsletter-form.component.html',
  styleUrl: './manage-newsletter-form.component.css'
})
export class ManageNewsletterFormComponent {

  userInfo: any;

  constructor(private auth:AuthService,
    private ManageNewsletterService:ManageNewsletterService){

  }

  ngOnInit(): void {
    this.auth.getUserProfile().then(profile => {
      this.userInfo = profile;  // Stocker les informations dans la variable userInfo
      console.log(profile)
      
    }).catch(error => {
      console.error('Erreur lors du chargement des informations utilisateur', error);
    });

  }

  formData = {
    title: '',
    subtitle: '',
    content: '',
    publicationDate: '',
    longitude: '',
    latitude: ''
  };


  onSubmit() {
    const { title, subtitle, content, publicationDate} = this.formData;

    // Validation des champs
    if (!title || title.trim().length < 2) {
      alert('Title is required and must be at least 2 characters.');
      return;
    }

    if (!subtitle || subtitle.trim().length < 2) {
      alert('Subtitle is required and must be at least 2 characters.');
      return;
    }

    if (!content || content.trim().length < 10) {
      alert('Content is required and must be at least 10 characters.');
      return;
    }

    if (!publicationDate) {
      alert('Publication date is required.');
      return;
    }

    // Si tout est valide
    alert('Newsletter successfully submitted!');
    console.log('Submitted data:', this.formData);
    // Envoyer les données au backend ici... 

  
    
    const newsletter: Newsletter={
      newsletterId: 0,
      title: '',
      subtitle: '',
      publicationDate: '',
      creator: '',
      articles:[]
    }
    
    this.ManageNewsletterService.Addnewsletter(newsletter).subscribe({
      next(value) {
        console.log(value)
      },
    })

  }

 

  

  


}
