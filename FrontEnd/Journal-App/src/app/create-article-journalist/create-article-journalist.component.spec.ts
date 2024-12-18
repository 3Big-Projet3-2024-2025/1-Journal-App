import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateArticleJournalistComponent } from './create-article-journalist.component';

describe('CreateArticleJournalistComponent', () => {
  let component: CreateArticleJournalistComponent;
  let fixture: ComponentFixture<CreateArticleJournalistComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateArticleJournalistComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateArticleJournalistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
