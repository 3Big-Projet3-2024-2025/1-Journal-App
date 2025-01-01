import { Component, OnInit } from '@angular/core';
import { EmailService } from '../services/Email.service';
import { UsersService } from '../services/users.service';

@Component({
  selector: 'app-manage-rgpd-form',
  templateUrl: './manage-rgpd-form.component.html',
  styleUrls: ['./manage-rgpd-form.component.css']
})
export class ManageRgpdFormComponent implements OnInit {
  formData = {
    fullName: '',
    email: '',
    requestType: '',
    requestDetails: '',
    adminEmail: '' // Pré-rempli avec l'email de l'admin
  };

  constructor(private emailService: EmailService,private userservice:UsersService) {}

  ngOnInit() {
    // Récupérer l'email de l'admin avec l'ID 1
    this.userservice.getUserById(5).subscribe(
      (admin) => {
        if (admin && admin.email) {
          this.formData.adminEmail = admin.email;
          console.log(admin.email)
        } else {
          console.error('Admin with ID 1 does not have a valid email.');
          alert('Unable to retrieve admin email. Please contact support.');
        }
      },
      (error) => {
        console.error('Error fetching admin details:', error);
        alert('Failed to load admin details.');
      }
    );
  }

  onSubmit() {
    const { fullName, email, requestType, requestDetails, adminEmail } = this.formData;

    if (!fullName || !email || !requestType || !adminEmail || requestDetails.length > 1000) {
      alert('Please fill out all required fields.');
      return;
    }

    const subject = `RGPD Request from ${fullName}`;
    const content = `User: ${fullName} (${email})\nType: ${requestType}\nDetails: ${requestDetails}`;

    // Appeler le service pour envoyer l'email
    this.emailService.sendEmail(adminEmail, subject, content).subscribe(
      (response) => {
        alert('Email sent successfully!');
      },
      (error) => {
        console.error('Error sending email:', error);
        alert('Failed to send email.');
      }
    );
  }
}
