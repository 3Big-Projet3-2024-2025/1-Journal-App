import { ArticleService } from "../services/article.service";
import { Article } from "./article";
import { User } from "./user";

export interface Commentmap {
  content: string,
  publicationDate: Date, // par exemple la date du jour
  user_id: number,
  article_id: number,
  user?:User
  article?:Article

  }