import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ManageNewsletterService {

  constructor(
    private http: HttpClient
  ) { }

  GetALlnewsletter(): Observable<any[]>{
    const urlApi= "http://localhost:8080/newsletters/all"
    return this.http.get<any[]>(urlApi,{})
  }
}
