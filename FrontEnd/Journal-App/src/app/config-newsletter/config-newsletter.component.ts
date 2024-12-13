// src/app/conf-newsletter/conf-newsletter.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { Newsletter } from '../models/newsletter';
import { Article } from '../models/article';


@Component({
  selector: 'app-conf-newsletter',
  templateUrl: './config-newsletter.component.html',
  styleUrls: ['./config-newsletter.component.css']
})
export class ConfNewsletterComponent implements OnInit {

  newsletterForm: FormGroup;
  newsletter: Newsletter;
  availableFonts: string[] = ['Arial', 'Courier New', 'Georgia', 'Times New Roman', 'Verdana'];
  nextArticleId: number = 1;

  constructor(
    private fb: FormBuilder,
    
  ) {
    // Initialisation de la newsletter avec des valeurs par défaut
    this.newsletter = {
      title: 'Titre par défaut',
      subtitle: 'Sous-titre par défaut',
      backgroundColor: '#ffffff',
      font: 'Arial',
      articles: [],
      newsletterId: 0, // Définir une valeur par défaut appropriée
      publicationDate: new Date().toISOString().split('T')[0], // Date actuelle au format "YYYY-MM-DD"
      creator: 0 // Définir cet ID correctement si nécessaire
    };

    // Initialisation du formulaire réactif
    this.newsletterForm = this.fb.group({
      title: [this.newsletter.title, [Validators.required, Validators.minLength(2)]],
      subtitle: [this.newsletter.subtitle, [Validators.required, Validators.minLength(2)]],
      backgroundColor: [this.newsletter.backgroundColor, Validators.required],
      font: [this.newsletter.font, Validators.required],
      articleTitle: ['', [Validators.required, Validators.minLength(2)]],
      articleContent: ['', [Validators.required, Validators.minLength(5)]]
    });

    // Charger les configurations depuis le localStorage si disponibles
    this.loadFromLocalStorage();
  }

  ngOnInit(): void {
  }

  // Méthode pour ajouter un article
  addArticle(): void {
    if (this.newsletterForm.get('articleTitle')?.invalid || this.newsletterForm.get('articleContent')?.invalid) {
      
      return;
    }

    const newArticle: Article = {
      //id: this.nextArticleId++,
      title: this.newsletterForm.value.articleTitle,
      content: this.newsletterForm.value.articleContent,
      articleId: 0,
      //publicationDate,
      longitude: 0,
      latitude: 0,
      user_id: 0,
      newsletter_id: 0,
      valid: false,
      
    };

    this.newsletter.articles.push(newArticle);
    this.saveToLocalStorage();
    this.newsletterForm.patchValue({ articleTitle: '', articleContent: '' });
   
  }

  // Méthode pour supprimer un article
  deleteArticle(index: number): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer l'article "${this.newsletter.articles[index].title}" ?`)) {
      this.newsletter.articles.splice(index, 1);
      this.saveToLocalStorage();
      
    }
  }

  // Méthode pour gérer le drag-and-drop des articles
  drop(event: CdkDragDrop<Article[]>): void {
    moveItemInArray(this.newsletter.articles, event.previousIndex, event.currentIndex);
    this.saveToLocalStorage();
   
  }

  // Méthode pour sauvegarder les configurations dans le localStorage
  saveToLocalStorage(): void {
    localStorage.setItem('newsletterConfig', JSON.stringify(this.newsletter));
  }

  // Méthode pour charger les configurations depuis le localStorage
  loadFromLocalStorage(): void {
    const savedConfig = localStorage.getItem('newsletterConfig');
    if (savedConfig) {
      this.newsletter = JSON.parse(savedConfig);
      this.newsletterForm.patchValue({
        title: this.newsletter.title,
        subtitle: this.newsletter.subtitle,
        backgroundColor: this.newsletter.backgroundColor,
        font: this.newsletter.font
      });

      // Mettre à jour le nextArticleId pour éviter les conflits d'ID
     // if (this.newsletter.articles.length > 0) {
    //    this.nextArticleId = Math.max(...this.newsletter.articles.map((a: Article) => a.id)) + 1;
     // }
    }
  }

  // Méthode pour mettre à jour les configurations
  updateNewsletter(): void {
    if (this.newsletterForm.invalid) {
     
      return;
    }

    this.newsletter.title = this.newsletterForm.value.title;
    this.newsletter.subtitle = this.newsletterForm.value.subtitle;
    this.newsletter.backgroundColor = this.newsletterForm.value.backgroundColor;
    this.newsletter.font = this.newsletterForm.value.font;

    this.saveToLocalStorage();
   
  }

  // Méthode pour réinitialiser les configurations
  resetNewsletter(): void {
    if (confirm('Êtes-vous sûr de vouloir réinitialiser la newsletter à ses paramètres par défaut?')) {
      this.newsletter = {
        title: 'Titre par défaut',
        subtitle: 'Sous-titre par défaut',
        backgroundColor: '#ffffff',
        font: 'Arial',
        articles: [],
        newsletterId: 0,
        publicationDate: new Date().toISOString().split('T')[0],
        creator: 0
      };
      this.newsletterForm.reset({
        title: this.newsletter.title,
        subtitle: this.newsletter.subtitle,
        backgroundColor: this.newsletter.backgroundColor,
        font: this.newsletter.font,
        articleTitle: '',
        articleContent: ''
      });
      localStorage.removeItem('newsletterConfig');
      this.nextArticleId = 1;
      
    }
  }

}
