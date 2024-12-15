import { Injectable } from "@angular/core";
import { HttpClient , HttpHeaders } from "@angular/common/http";
import { Router } from "@angular/router";
import { CookieService } from "ngx-cookie-service";
import { Observable } from "rxjs";
import { User } from "../models/user";

@Injectable({
    
        providedIn: 'root'
})
export class UsersService {

    
    private apiUrl = 'http://localhost:8080';



    constructor(private http: HttpClient, private router: Router, private cookieService: CookieService) {}

   

    getUserByKeycloakId(keycloakId: string): Observable<User> {
        return this.http.get<User>(`${this.apiUrl}/users/keycloak/${keycloakId}`);
      }

    getUserById(id: number): Observable<any> {
        const url = `${this.apiUrl}/${id}`;
        const headers = this.getAuthHeaders(); 
        return this.http.get(url, { headers });
    }
    

    addUser(newUser: User): Observable<User> {
        const headers = this.getAuthHeaders();
        return this.http.post<User>(this.apiUrl, newUser, { headers });
    }
    getUsers(): Observable<User[]> {
        const headers = this.getAuthHeaders();
        return this.http.get<User[]>(this.apiUrl, { headers });
    }

    updateUser(id: number, updatedUser: User): Observable<User> {
        const url = `${this.apiUrl}/${id}`;
        const headers = this.getAuthHeaders();
        return this.http.put<User>(url, updatedUser, { headers });
    }

    deleteUser(id: number): Observable<void> {
        const url = `${this.apiUrl}/${id}`;
        const headers = this.getAuthHeaders();
        return this.http.delete<void>(url, { headers });
    }

    private getAuthHeaders(): HttpHeaders {
        const token = this.cookieService.get('token');  // Récupère le token JWT depuis les cookies
        console.log("Token récupéré:", token);  // Affiche le token dans la console pour vérification
        if (!token) {
            console.error('Token absent dans les cookies');
             // Si le token est manquant, redirige vers la page de login
        }
        return new HttpHeaders({
            Authorization: token ? `Bearer ${token}` : ''  // Ajoute le token dans l'en-tête Authorization
        });
    }

   
    

 


}
