import { Injectable } from "@angular/core";
import { HttpClient , HttpHeaders } from "@angular/common/http";
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
  




    constructor(private http: HttpClient, private router: Router, private cookieService: CookieService) {}

   

    getUserByKeycloakId(keycloakId: string): Observable<User> {
        return this.http.get<User>(`${this.apiUrl}/users/keycloak/${keycloakId}`);
}

    getUserById(id: number): Observable<any> {
        const url = `${this.apiUrl}/users/${id}`;
        const headers = this.getAuthHeaders(); 
        return this.http.get(url, { headers });
    }
    


    // new
    getAllUsers(): Observable<User[]> {
        return this.http.get<User[]>(`${this.baseUrl}`);
      }
    
    //   updateUser(userId: number, user: Partial<User>): Observable<any> {
    //     return this.http.patch(`${this.baseUrl}/${userId}/update`, user);
    //   }
    updateUser(userId: number, user: User): Observable<any> {
        return this.http.patch(`${this.baseUrl}/${userId}/update`, user);
      }
    
      deleteUser(userId: number): Observable<any> {
        return this.http.delete(`${this.baseUrl}/${userId}`);
      }
    
      addUser(user: Partial<User>): Observable<User> {
        return this.http.post<User>(`${this.baseUrl}`, user);
      }
      getRoles(): Observable<Role[]> {
        return this.http.get<Role[]>(this.rolesUrl);
      }

// new end 


    

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
