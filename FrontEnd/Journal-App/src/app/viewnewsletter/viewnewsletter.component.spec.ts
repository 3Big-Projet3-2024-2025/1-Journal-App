import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewnewsletterComponent } from './viewnewsletter.component';

describe('ViewnewsletterComponent', () => {
  let component: ViewnewsletterComponent;
  let fixture: ComponentFixture<ViewnewsletterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ViewnewsletterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewnewsletterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
