import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ManageNewsletterService } from '../services/manage-newsletter.service';

@Component({
  selector: 'app-list-crud',
  templateUrl: './list-crud.component.html',
  styleUrl: './list-crud.component.css',
})
export class ListCrudComponent {
  type: string = ''; 
  titre: string = '';
  titretable: string = '';
  constructor(
    private route: ActivatedRoute,
    private Managenewsletter: ManageNewsletterService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.type = params['type']; 
      this.loadDataBasedOnType(); 
    });
  }

  loadDataBasedOnType() {
    if (this.type === 'articles') {
      console.log('Charger les articles');
      this.titre = 'Management of the articles';
      this.titretable = 'articles';
     
    } else if (this.type === 'newsletters') {
      
      console.log('Charger les newsletters');
      this.titre = 'Management of the Newsletter';
      this.titretable = 'newsletters';

      this.Managenewsletter.GetALlnewsletter().subscribe({
        next: (data) => {
          console.log(data);
        },
      });

    } else {
      console.error('Type invalidezezef !');
    }
  }
}
