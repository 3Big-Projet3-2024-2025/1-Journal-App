import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewArticleDetailComponent } from './view-article-detail.component';

describe('ViewArticleDetailComponent', () => {
  let component: ViewArticleDetailComponent;
  let fixture: ComponentFixture<ViewArticleDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ViewArticleDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewArticleDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
