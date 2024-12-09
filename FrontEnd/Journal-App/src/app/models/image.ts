import { Article } from "./article";

export interface Image {
    imageId: number;
    imagePath: string;  // (généralement en base64 ou une URL)
    article: Article;
}
