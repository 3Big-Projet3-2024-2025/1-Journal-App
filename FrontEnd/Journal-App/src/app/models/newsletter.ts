// src/app/models/newsletter.ts
import { Article } from "./article";
import { User } from "./user";

export interface Newsletter {
    backgroundColor: string; // Utiliser string pour les couleurs (ex: "#ffffff")
    font: string; // Utiliser string pour les noms de polices
    articles: Article[]; // Utiliser un tableau d'objets Article
    
    newsletterId: number;
    title: string;
    subtitle: string;
    publicationDate: string;  // (format: "YYYY-MM-DD")
    creator: number; 
}
