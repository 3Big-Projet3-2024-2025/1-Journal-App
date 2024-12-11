import { Component } from '@angular/core';

@Component({
  selector: 'app-manage-newsletter-form',
  templateUrl: './manage-newsletter-form.component.html',
  styleUrl: './manage-newsletter-form.component.css'
})
export class ManageNewsletterFormComponent {

  formData = {
    title: '',
    subtitle: '',
    content: '',
    publicationDate: '',
    longitude: '',
    latitude: ''
  };

  onSubmit() {
    const { title, subtitle, content, publicationDate, longitude, latitude } = this.formData;

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

    if (!longitude || !latitude) {
      alert('Please provide geolocation data.');
      return;
    }

    // Si tout est valide
    alert('Newsletter successfully submitted!');
    console.log('Submitted data:', this.formData);
    // Envoyer les données au backend ici...
  }

  geolocalize() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          this.formData.longitude = position.coords.longitude.toFixed(6);
          this.formData.latitude = position.coords.latitude.toFixed(6);
          alert('Geolocation retrieved successfully!');
        },
        (error) => {
          alert('Unable to retrieve your location.');
          console.error(error);
        }
      );
    } else {
      alert('Geolocation is not supported by this browser.');
    }
  }

  


}
