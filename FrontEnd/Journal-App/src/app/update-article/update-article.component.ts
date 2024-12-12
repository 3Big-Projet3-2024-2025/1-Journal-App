import { Component } from '@angular/core';

@Component({
  selector: 'app-update-article',
  templateUrl: './update-article.component.html',
  styleUrl: './update-article.component.css'
})
export class UpdateArticleComponent {

  validateFileCount(event: any): void {
    const files = event.target.files;
    
    // Vérifie si plus de 3 fichiers sont sélectionnés
    if (files.length > 3) {
      alert("You can upload a maximum of 3 images.");
      event.target.value = ""; // Réinitialise la sélection
      return;
    }

    // Vérifie si tous les fichiers sont des images
    for (const file of files) {
      if (!file.type.startsWith('image/')) {
        alert("Only image files are allowed.");
        event.target.value = ""; // Réinitialise la sélection
        return;
      }
    }

    // Si tout est correct
    alert(`${files.length} file(s) selected successfully.`);
  }

}
