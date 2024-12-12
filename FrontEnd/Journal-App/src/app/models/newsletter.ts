import { Article } from "./article";
import { User } from "./user";

export interface Newsletter {
    
    newsletterId: number;
    title: string;
    subtitle: string;
    publicationDate: string;  // (format: "YYYY-MM-DD")
    isRead?: boolean;
    articles?: Article[];  
    creator: string; 
    comments?: Comment[];
}
