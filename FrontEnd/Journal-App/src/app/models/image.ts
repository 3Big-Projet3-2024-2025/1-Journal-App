export interface Image {
    imageId: number;      // ID de l'image (généré par le backend)
    imagePath: string;    // Contenu encodé en Base64
    articleId: number;    // ID de l'article associé
  }
  