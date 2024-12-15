import { Component } from '@angular/core';
import { NgClass, } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-manage-rgpd-form',
  templateUrl: './manage-rgpd-form.component.html',
  styleUrls: ['./manage-rgpd-form.component.css']
})
export class ManageRgpdFormComponent {
  formData = {
    fullName: '',
    email: '',
    requestType: '',
    requestDetails: ''
  };

  onSubmit() {
    const { fullName, email, requestType, requestDetails } = this.formData;

    // Validation des champs
    if (!fullName || fullName.trim().length < 2) {
      alert('Full name is required and must be at least 2 characters.');
      return;
    }

    if (!email || !this.isValidEmail(email)) {
      alert('Please enter a valid email address.');
      return;
    }

    if (!requestType) {
      alert('Please select a request type.');
      return;
    }

    if (requestDetails.length > 1000) {
      alert('Request details cannot exceed 1000 characters.');
      return;
    }

    // Si tout est valide
    alert('Form successfully submitted!');
    console.log('Submitted data:', this.formData);
    // Envoyer les données au backend ici...
  }

  // Fonction pour vérifier si l'email est valide
  isValidEmail(email: string): boolean {
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailPattern.test(email);
  }
}
