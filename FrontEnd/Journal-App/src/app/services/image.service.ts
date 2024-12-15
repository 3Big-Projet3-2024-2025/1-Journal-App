
import { Injectable } from "@angular/core";
import { HttpClient  } from "@angular/common/http";
import { Observable } from "rxjs";
import { Image } from "../models/image";

@Injectable({
  providedIn: 'root',
})

export class ImageService {

    private apiUrl = 'http://localhost:8080/images';

    constructor(private http:HttpClient ){}

    
    getImagesByArticleId(articleId: number): Observable<Image[]> {
        return this.http.get<Image[]>(`${this.apiUrl}/article/${articleId}`);
    }

    addImage(image: Image): Observable<Image> {
        return this.http.post<Image>(this.apiUrl, image);
    }

    deleteImage(imageId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${imageId}`);
    }
    
}