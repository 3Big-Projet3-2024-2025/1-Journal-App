import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyReadArticlesComponent } from './my-read-articles.component';

describe('MyReadArticlesComponent', () => {
  let component: MyReadArticlesComponent;
  let fixture: ComponentFixture<MyReadArticlesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MyReadArticlesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyReadArticlesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
