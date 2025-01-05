import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Router } from "@angular/router";
import { CookieService } from "ngx-cookie-service";
import { Observable } from "rxjs";
import { User } from "../models/user";
import { Role } from "../models/role";

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  private apiUrl = 'http://localhost:8080';
  private baseUrl = 'http://localhost:8080/users';
  private rolesUrl = 'http://localhost:8080/roles';

  constructor(
    private http: HttpClient, 
    private router: Router, 
    private cookieService: CookieService
  ) {}

  // Fetch a user by Keycloak ID
  getUserByKeycloakId(keycloakId: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/users/keycloak/${keycloakId}`);
  }

  // Fetch a user by ID
  getUserById(id: number): Observable<any> {
    const url = `${this.apiUrl}/users/${id}`;
    const headers = this.getAuthHeaders();
    return this.http.get(url, { headers });
  }

  // Fetch all users
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.baseUrl}`);
  }

  // Update a user's details
  updateUser(userId: number, user: User): Observable<any> {
    return this.http.patch(`${this.baseUrl}/${userId}/update`, user);
  }

  // Delete a user by ID
  deleteUser(userId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${userId}`);
  }

  // Add a new user
  addUser(user: Partial<User>): Observable<User> {
    return this.http.post<User>(`${this.baseUrl}`, user);
  }

  // Fetch all roles
  getRoles(): Observable<Role[]> {
    return this.http.get<Role[]>(this.rolesUrl);
  }

  // Add a GDPR request for a user
  addGdprRequest(userId: number, gdprRequest: string): Observable<any> {
    const url = `${this.baseUrl}/${userId}/gdpr-requests/add`;
    return this.http.patch(url, gdprRequest, { headers: this.getAuthHeaders() });
  }

  // Remove a GDPR request from a user
  removeGdprRequest(userId: number, gdprRequest: string): Observable<any> {
    const url = `${this.baseUrl}/${userId}/gdpr-requests/remove`;
    return this.http.patch(url, gdprRequest, { headers: this.getAuthHeaders() });
  }

  // NEW: Fetch users with GDPR requests
  getUsersWithGdprRequests(): Observable<User[]> {
    const url = `${this.baseUrl}/with-gdpr-requests`;
    return this.http.get<User[]>(url, { headers: this.getAuthHeaders() });
  }

  // Helper method to get authorization headers
  private getAuthHeaders(): HttpHeaders {
    const token = this.cookieService.get('token');
    if (!token) {
      console.error('Token is missing from cookies');
    }
    return new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : ''
    });
  }
  getUserByEmail(email: string): Observable<User> {
    const url = `${this.baseUrl}/email/${email}`;
    return this.http.get<User>(url);
  }
}
