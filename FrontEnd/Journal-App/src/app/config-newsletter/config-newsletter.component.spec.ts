import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfNewsletterComponent } from './config-newsletter.component';

describe('ConfigNewsletterComponent', () => {
  let component: ConfNewsletterComponent;
  let fixture: ComponentFixture<ConfNewsletterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConfNewsletterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConfNewsletterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
