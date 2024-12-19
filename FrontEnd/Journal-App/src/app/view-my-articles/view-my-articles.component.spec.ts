import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewMyArticlesComponent } from './view-my-articles.component';

describe('ViewMyArticlesComponent', () => {
  let component: ViewMyArticlesComponent;
  let fixture: ComponentFixture<ViewMyArticlesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ViewMyArticlesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewMyArticlesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
