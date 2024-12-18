import { User } from "./user";
import { Newsletter } from "./newsletter";

export interface Comment {
    commentId: number;
    content: string;
    publicationDate: string;  // (format: "YYYY-MM-DD")
    user: User; 
    newsletter: Newsletter | null; 
  }