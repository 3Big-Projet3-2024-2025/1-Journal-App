export interface Article {
    articleId : number;
    title : string;
    content : string;
    publicationDate : Date;
    longitude : number;
    latitude : number ;
    user_id : number ;
    newsletter_id : number ;
    valid : boolean
}
