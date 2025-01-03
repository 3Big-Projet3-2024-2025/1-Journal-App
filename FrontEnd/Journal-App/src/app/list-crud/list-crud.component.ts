import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ManageNewsletterService } from '../services/manage-newsletter.service';
import { ArticleService } from '../services/article.service';
import { CommentService } from '../services/comment.service';
import { UsersService } from '../services/users.service';
import { Article } from '../models/article';
import { Newsletter } from '../models/newsletter';
import { Comments } from '../models/comment';
import { User } from '../models/user';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-list-crud',
  templateUrl: './list-crud.component.html',
  styleUrls: ['./list-crud.component.css'],
})
export class ListCrudComponent {
  type: string = '';
  titre: string = '';
  titretable: string = '';
  validArticles: Article[] = [];
  nonValidArticles: Article[] = [];
  newsletters: Newsletter[] = [];
  comments: Comments[] = [];
  gdprUsers: User[] = []; // GDPR users list

  constructor(
    private route: ActivatedRoute,
    private articleService: ArticleService,
    private manageNewsletterService: ManageNewsletterService,
    private commentService: CommentService,
    private usersService: UsersService,
    private cookieService: CookieService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const storedType = localStorage.getItem('newsletter');
    

    if (storedType === '6') {
      this.type = 'gdpr';
    } else if (storedType === '1') {
      this.type = 'newsletters';
    } else if (storedType === '3') {
      this.type = 'comments';
    } else {
      this.type = 'articles';
    }

    this.loadDataBasedOnType();
  }

  loadDataBasedOnType(): void {
    if (this.type === 'articles') {
      this.titre = 'Management of the Articles';
      this.titretable = 'articles';

      const userRole = localStorage.getItem('userRole');
      const userId = Number(localStorage.getItem('userId'));

      if (userRole === 'ADMIN') {
        this.articleService.getArticles().subscribe(
          (articles) => {
            this.validArticles = articles.filter((article) => article.valid);
            this.nonValidArticles = articles.filter((article) => !article.valid);
          },
          (error) => {
            console.error('Error fetching articles:', error);
          }
        );
      } else if (userRole === 'EDITOR') {
        this.articleService.getArticlesByEditorId(userId).subscribe(
          (articles) => {
            this.validArticles = articles.filter((article) => article.valid);
            this.nonValidArticles = articles.filter((article) => !article.valid);
          },
          (error) => {
            console.error('Error fetching articles for editor:', error);
          }
        );
      } else {
        console.error('Unknown user role:', userRole);
      }
    } else if (this.type === 'newsletters') {
      this.titre = 'Management of the Newsletters';
      this.titretable = 'newsletters';

      this.manageNewsletterService.GetALlnewsletter().subscribe(
        (newsletters) => {
          const userId = Number(localStorage.getItem('userId'));
          const userRole = localStorage.getItem('userRole');

          if (userRole === 'ADMIN') {
            this.newsletters = newsletters;
          } else {
            this.newsletters = newsletters.filter(
              (newsletter) =>
                newsletter.creator.userId === userId && userRole === 'EDITOR'
            );
          }

          console.log(this.newsletters);
        },
        (error) => {
          console.error('Error fetching newsletters:', error);
        }
      );
    } else if (this.type === 'comments') {
      this.titre = 'Management of the Comments';
      this.titretable = 'comments';

      this.commentService.getAllComments().subscribe(
        (comments) => {
          const userId = Number(localStorage.getItem('userId'));
          const userRole = localStorage.getItem('userRole');

          this.comments = comments.filter((comment) => {
            const newsletterCreatorId =
              comment.article?.newsletter?.creator?.userId || null;
            return (
              userRole === 'ADMIN' ||
              (userRole === 'EDITOR' && newsletterCreatorId === userId)
            );
          });

          console.log('Filtered comments:', this.comments);
        },
        (error) => {
          console.error('Error fetching comments:', error);
        }
      );
    } else if (this.type === 'gdpr') {
      this.titre = 'Management of GDPR Requests';
      this.titretable = 'gdpr requests';

      this.usersService.getUsersWithGdprRequests().subscribe(
        (users) => {
          this.gdprUsers = users;
          //alert(this.gdprUsers[0].firstName)
          console.log('Users with GDPR requests:', this.gdprUsers);
        },
        (error) => {
          console.error('Error fetching GDPR users:', error);
        }
      );
    }
  }

  validateArticle(article: Article): void {
    if (confirm('Are you sure you want to validate this article?')) {
      const updatedArticle = { ...article, valid: true };
      this.articleService.validateArticle(article.articleId).subscribe(
        () => {
          alert('The article has been successfully validated.');
          this.loadDataBasedOnType();
        },
        (error) => {
          console.error('Error validating article:', error);
        }
      );
    }
  }

  unvalidateArticle(article: Article): void {
    if (confirm('Are you sure you want to unvalidate this article?')) {
      const updatedArticle = { ...article, valid: false };
      this.articleService.unvalidateArticle(article.articleId).subscribe(
        () => {
          alert('The article has been successfully unvalidated.');
          this.loadDataBasedOnType();
        },
        (error) => {
          console.error('Error unvalidating article:', error);
        }
      );
    }
  }

  confirmDelete(id: number, type: string): void {
    if (confirm('Are you sure you want to delete this item?')) {
      if (type === 'article') {
        this.articleService.deleteArticle(id).subscribe(
          () => {
            this.loadDataBasedOnType();
            this.router.navigate(['/crud/article']);
            alert('The article has been successfully deleted.');
          },
          (error) => {
            console.error('Error deleting article:', error);
            this.router.navigate(['/crud/article']);
          }
        );
      } else if (type === 'newsletter') {
        this.manageNewsletterService.deletenewsletter(id).subscribe(
          () => {
            this.router.navigate(['/home']);
            alert('The newsletter has been successfully deleted.');
          },
          (error) => {
            console.error('Error deleting newsletter:', error);
            this.router.navigate(['/home']);
          }
        );
      } else if (type === 'comment') {
        this.commentService.deleteComment(id).subscribe(
          () => {
            this.router.navigate(['/home']);
            alert('The comment has been successfully deleted.');
          },
          (error) => {
            console.error('Error deleting comment:', error);
            this.router.navigate(['/home']);
          }
        );
      }
    }
  }

  click(id: number): void {
    this.router.navigate(['/update-newsletter']);
    localStorage.setItem('put', 'edit');
    localStorage.setItem('idnewsletter', id.toString());
  }

  clickvoir(id: number): void {
    this.router.navigate(['/see-newsletter']);
    localStorage.setItem('see', 'look');
    localStorage.setItem('seeidnewsletter', id.toString());
    localStorage.setItem('fromArticleDetail', '');
  }

  getCommentsGroupedByArticle(): { [key: string]: Comments[] } {
    if (!this.comments) {
      return {};
    }
    return this.comments.reduce((grouped: { [key: string]: Comments[] }, comment: Comments) => {
      const articleTitle = comment.article.title;
      if (!grouped[articleTitle]) {
        grouped[articleTitle] = [];
      }
      grouped[articleTitle].push(comment);
      return grouped;
    }, {});
  }

  getObjectKeys(obj: any): string[] {
    return Object.keys(obj);
  }
  viewGdprDetails(userId: number): void {
    // Navigate to a detailed view of the user's GDPR requests
    this.router.navigate(['/seergpdrequest', userId]);
  }
  
  deleteGdprRequest(userId: number,gdprRequest :string): void {
    if (confirm('Are you sure you want to delete this GDPR request?')) {
      // Logic to delete GDPR requests for the user
      this.usersService.removeGdprRequest(userId,gdprRequest).subscribe(
        () => {
          alert('GDPR request deleted successfully.');
          location.reload() // Reload data after deletion
        },
        (error) => {
          console.error('Error deleting GDPR request:', error);
        }
      );
    }
  }
  
}
