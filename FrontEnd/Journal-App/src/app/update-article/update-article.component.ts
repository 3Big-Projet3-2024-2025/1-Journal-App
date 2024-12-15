import { Component, OnInit } from '@angular/core';
import { Article } from '../models/article';
import { ArticleService } from '../services/article.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-update-article',
  templateUrl: './update-article.component.html',
  styleUrls: ['./update-article.component.css']
})
export class UpdateArticleComponent implements OnInit {
  articleToUpdate: Article = {
    articleId: 0,
    title: '',
    content: '',
    publicationDate: new Date(),
    longitude: 0.0,
    latitude: 0.0,
    user_id: 0,
    newsletter_id: 0,
    valid: false
  };

  selectedFiles: File[] = [];

  constructor(
    private articleService: ArticleService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadArticle();
  }

  // Charger l'article existant à partir de l'ID
  loadArticle(): void {
    const articleId = Number(this.route.snapshot.paramMap.get('id')); // ID de l'article dans l'URL
    if (!articleId) {
      console.error('Invalid article ID');
      return;
    }

    this.articleService.getArticleById(articleId).subscribe(
      (article) => {
        this.articleToUpdate = article;
      },
      (error) => {
        console.error('Error loading article:', error);
      }
    );
  }

  // Valider les fichiers
  validateFileCount(event: any): void {
    const files = event.target.files;

    if (files.length > 3) {
      alert('You can upload a maximum of 3 images.');
      event.target.value = '';
      return;
    }

    for (const file of files) {
      if (!file.type.startsWith('image/')) {
        alert('Only image files are allowed.');
        event.target.value = '';
        return;
      }
    }

    this.selectedFiles = Array.from(files);
    alert(`${files.length} file(s) selected successfully.`);
  }

  // Mettre à jour l'article
  updateArticle(): void {
    const articleId = this.articleToUpdate.articleId;

    this.articleService.updateArticle(articleId, this.articleToUpdate).subscribe(
      async (updatedArticle) => {
        console.log('Article updated successfully:', updatedArticle);

        // Mettre à jour les images si nécessaires
        if (this.selectedFiles.length > 0) {
          await this.uploadImages(articleId);
        }

        // Retourner à la liste des articles
        this.router.navigate(['/crud/articles']);
      },
      (error) => {
        console.error('Error updating article:', error);
      }
    );
  }

  

  // Téléverser les nouvelles images
  async uploadImages(articleId: number): Promise<void> {
    for (const file of this.selectedFiles) {
      const base64Image = await this.convertToBase64(file);
      const imageToAdd = {
        imageId: 0,
        imagePath: base64Image,
        articleId: articleId
      };

      this.articleService.addImage(imageToAdd).subscribe(
        (imageSaved) => {
          console.log('Image updated successfully:', imageSaved);
        },
        (error) => {
          console.error('Error updating image:', error);
        }
      );
    }
  }

  // Conversion d'image en Base64
  convertToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => {
        const base64String = (reader.result as string).split(',')[1];
        resolve(base64String);
      };
      reader.onerror = (error) => reject(error);
    });
  }
}
