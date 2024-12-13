import { Component, OnInit } from '@angular/core';
import { Newsletter } from '../models/newsletter';
import { AuthService } from '../services/auth.service';
import { ManageNewsletterService } from '../services/manage-newsletter.service';
import { UsersService } from '../services/users.service';

@Component({
  selector: 'app-manage-newsletter-form',
  templateUrl: './manage-newsletter-form.component.html',
  styleUrls: ['./manage-newsletter-form.component.css']
})
export class ManageNewsletterFormComponent implements OnInit {
  userInfo: any = null;
  useridbykey: number | null = null;

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
    textAlign: ''
  };

  constructor(
    private auth: AuthService,
    private manageNewsletterService: ManageNewsletterService,
    private userservice: UsersService
  ) {}

  ngOnInit(): void {
    localStorage.setItem('newsletter', '0');
    this.auth.getUserProfile().then(profile => {
      this.userInfo = profile;
      this.getUserId();
    }).catch(error => {
      console.error('Error while loading user information', error);
    });
  }

  private getUserId(): void {
    if (!this.userInfo?.id) {
      console.error('User ID not found.');
      return;
    }

    this.userservice.getUserByKeycloakId(this.userInfo.id).subscribe({
      next: (data) => {
        this.useridbykey = data.userId;
      },
      error: (err) => {
        console.error('Error while retrieving user ID', err);
      }
    });
  }

  onSubmit(): void {
    if (!this.isFormValid()) {
      return;
    }

    const newsletter: Newsletter = {
      newsletterId: 0,
      title: this.formData.title,
      subtitle: this.formData.subtitle,
      publicationDate: new Date(),
      creator: this.useridbykey || 0,
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
      textAlign: this.formData.textAlign
    };

    this.manageNewsletterService.Addnewsletter(newsletter).subscribe({
      next: (value) => {
        console.log('Newsletter successfully added', value);
        alert('Newsletter successfully added!');
      },
      error: (err) => {
        console.error('Error while adding the newsletter', err);
        alert('Error while adding the newsletter.');
      }
    });
  }

  updateNewsletter(newsletterId: number): void {
    if (!this.isFormValid()) {
      return;
    }

    const updatedNewsletter: Newsletter = {
      newsletterId:1,
      title: this.formData.title,
      subtitle: this.formData.subtitle,
      publicationDate: new Date(),
     
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
      textAlign: this.formData.textAlign
    };

    this.manageNewsletterService.Updatenewsletter(newsletterId, updatedNewsletter).subscribe({
      next: (value) => {
        console.log('Newsletter successfully updated', value);
        alert('Newsletter successfully updated!');
      },
      error: (err) => {
        console.error('Error while updating the newsletter', err);
        alert('Error while updating the newsletter.');
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
}
