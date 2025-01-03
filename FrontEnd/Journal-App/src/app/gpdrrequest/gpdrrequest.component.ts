import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { UsersService } from '../services/users.service';

@Component({
  selector: 'app-gpdrrequest',
  templateUrl: './gpdrrequest.component.html',
  styleUrls: ['./gpdrrequest.component.css']
})
export class GpdrrequestComponent implements OnInit {
  userId!: string;
  gdprRequestType: string | null = null; // Stores the type of the GDPR request
  gdprRequestDetails: string | null = null; // Stores the details of the GDPR request
  userName: string = ''; // Stores the user's name
  isLoading: boolean = true; // Indicates whether the data is being loaded
  errorMessage: string | null = null; // Error message, if any

  constructor(
    private route: ActivatedRoute,
    private usersService: UsersService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Retrieve the userId from the route parameters
    this.userId = this.route.snapshot.paramMap.get('id') || '';
    this.loadGdprRequest();
  }

  // Load GDPR request details for the user
  loadGdprRequest(): void {
    if (this.userId) {
      this.usersService.getUserById(Number(this.userId)).subscribe({
        next: (user) => {
          const gdprRequest = user.gdprRequests[0] || 'No GDPR request available.';

          // Extract the type and details of the request
          const [type, ...details] = gdprRequest.split(':');
          this.gdprRequestType = type.trim();
          this.gdprRequestDetails = details.join(':').trim();

          this.userName = `${user.firstName} ${user.lastName}`;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading GDPR request:', err);
          this.errorMessage = 'Failed to load GDPR request. Please try again later.';
          this.isLoading = false;
        }
      });
    } else {
      this.errorMessage = 'Invalid user ID.';
      this.isLoading = false;
    }
  }

  // Navigate back to the previous page
  goBack(): void {
    this.router.navigate(['/rgpdrequest']);
  }

  // Delete the GDPR request for the user
  deleteGdprRequest(): void {
    if (confirm('Are you sure you want to delete this GDPR request?')) {
      this.usersService.removeGdprRequest(Number(this.userId), `${this.gdprRequestType}: ${this.gdprRequestDetails}` || '').subscribe({
        next: () => {
          alert('GDPR request deleted successfully.');
          this.router.navigate(['/rgpdrequest']);
        },
        error: (err) => {
          console.error('Error deleting GDPR request:', err);
          alert('Failed to delete GDPR request. Please try again later.');
        }
      });
    }
  }
}
