import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageNewsletterFormComponent } from './manage-newsletter-form.component';

describe('ManageNewsletterFormComponent', () => {
  let component: ManageNewsletterFormComponent;
  let fixture: ComponentFixture<ManageNewsletterFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ManageNewsletterFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ManageNewsletterFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
